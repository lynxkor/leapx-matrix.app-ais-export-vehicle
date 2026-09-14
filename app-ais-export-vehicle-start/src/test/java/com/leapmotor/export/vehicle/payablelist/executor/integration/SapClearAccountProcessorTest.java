package com.leapmotor.export.vehicle.payablelist.executor.integration;

import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.letterGuarantee.enums.RecordTypeEnum;
import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountItem;
import com.leapmotor.export.vehicle.payablelist.enums.IsReceivedEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.platform.ais.common.redis.BlockLock;
import com.leapmotor.platform.ais.common.redis.DistributedLockTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SAP清账结果处理器单元测试
 *
 * <p>对应《红旗G117项目整车销售-应付清单管理.md》"验证应付清单SAP清账结果同步"章节测试用例：
 * 明细是否收款更新、SO状态更新、保函主数据变动、释放应收流水生成、抬头状态全收/部分收；
 * 并覆盖文档未提及但代码已实现的关键逻辑：重复清账幂等、PO关联保函应收不足、释放失败回滚，
 * 整批预校验（逐条校验、错误聚合为一个错误原因返回、任一条有误即整批拒绝执行），
 * 以及入参校验与数据不匹配场景一律抛出业务异常（不再静默跳过）。
 *
 * @author huangtao
 * @since 2026-09-02
 */
@DisplayName("SAP清账结果处理器单元测试")
@ExtendWith(MockitoExtension.class)
public class SapClearAccountProcessorTest {

    @Mock
    private PayableListGateway payableListGateway;
    @Mock
    private PayableListDetailGateway payableListDetailGateway;
    @Mock
    private VehicleCbuSoGateway vehicleCbuSoGateway;
    @Mock
    private ExportVehicleLetterGuaranteeRecordGateway letterGuaranteeRecordGateway;
    @Mock
    private ExportVehicleLetterGuaranteeGateway letterGuaranteeGateway;
    @Mock
    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;
    @Mock
    private DistributedLockTemplate distributedLockTemplate;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private BlockLock blockLock;

    private SapClearAccountProcessor processor;

    private static final String SO_NO = "SO0001";
    private static final String LG_ID = "LG-001";
    private static final String PO_NO = "PO0001";
    private static final String PAYABLE_LIST_ID = "PL-001";
    private static final String PAYABLE_LIST_NO = "YF202606000001";
    private static final String SO_ID = "SO-DB-001";
    private static final String VIN = "LFZ_001";

    @BeforeEach
    public void setUp() {
        processor = new SapClearAccountProcessor(payableListGateway, payableListDetailGateway,
                vehicleCbuSoGateway, letterGuaranteeRecordGateway, letterGuaranteeGateway,
                purchaseOrderGuaranteeGateway, distributedLockTemplate, transactionTemplate);
        // 事务回调真实执行，锁正常获取；lenient 容忍锁失败等场景下未被使用的 stub
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<Object> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(distributedLockTemplate.blockLock(anyString(), anyLong(), anyLong())).thenReturn(blockLock);
    }

    // ==================== 正常清账流程 ====================

    @Test
    @DisplayName("正常清账 — 明细收款更新、SO状态更新、保函应收释放、PO累计应收回写")
    public void testProcess_normalFlow() {
        // 幂等检查：无 type=40 释放流水；转应收(30)查询返回记录（按查询类型区分，不依赖调用顺序）
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);

        VehicleCbuSoDo soDo = buildSoDo();
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(soDo);

        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);

        ExportVehicleCbuPoGuaranteeDo poGuarantee = buildPoGuarantee(new BigDecimal("100000"));
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(poGuarantee));

        // 抬头状态统计：仅1条明细且已收款 → 全部收款
        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        PayableListDetailDo received = buildDetail();
        received.setIsReceived(IsReceivedEnum.YES.getCode());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Arrays.asList(received));

        processor.process(buildReq(LocalDateTime.of(2026, 8, 20, 15, 0, 0)));

        // 1. 明细更新：只写收款状态与收款时间（条件 SET，不覆盖其它列）
        verify(payableListDetailGateway).updateReceiveStatusById("D-001", IsReceivedEnum.YES.getCode(),
                LocalDateTime.of(2026, 8, 20, 15, 0, 0));

        // 2. SO 状态更新为已收款
        ArgumentCaptor<VehicleCbuSoDo> soCaptor = ArgumentCaptor.forClass(VehicleCbuSoDo.class);
        verify(vehicleCbuSoGateway).updateVehicleCbuSo(soCaptor.capture());
        assertEquals(VehicleCbuSoStatusEnum.RECEIVED.getCode(), soCaptor.getValue().getOrderStatus(),
                "SO订单状态应更新为已收款");

        // 3. 保函应收释放：VIN、释放原因、金额透传
        ArgumentCaptor<String> vinCaptor = ArgumentCaptor.forClass(String.class);
        verify(letterGuaranteeGateway).releaseReceivable(
                eq(LG_ID), eq(new BigDecimal("50000")), eq(SO_ID), eq(PO_NO), eq(SO_NO),
                anyString(), vinCaptor.capture());
        assertEquals(VIN, vinCaptor.getValue(), "释放应收流水应携带VIN");

        // 4. PO 关联保函回写：累计应收扣减
        ArgumentCaptor<ExportVehicleCbuPoGuaranteeDo> poCaptor = ArgumentCaptor.forClass(ExportVehicleCbuPoGuaranteeDo.class);
        verify(purchaseOrderGuaranteeGateway).updateExportVehicleCbuPoGuarantee(poCaptor.capture());
        assertEquals(new BigDecimal("50000"), poCaptor.getValue().getPoCurrentReceivableAmount(),
                "PO累计应收应扣减变动金额");

        // 5. 抬头状态：全部收款 → 已收款（单调前进更新，不做全量回写）
        verify(payableListGateway).advanceBillStatus(PAYABLE_LIST_ID, PayableListStatusEnum.RECEIVED.getCode());

        // 6. 明细冗余单据状态同步（同样只前进不回退）
        verify(payableListDetailGateway).advanceBillStatusByPayableListId(
                PAYABLE_LIST_ID, PayableListStatusEnum.RECEIVED.getCode());
    }

    @Test
    @DisplayName("正常清账 — 多条转应收记录应逐条释放并逐条回写")
    public void testProcess_multipleTransferRecords() {
        // 幂等检查(40)返回空，转应收(30)返回两条记录（分属两个保函）
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList()
                        : Arrays.asList(
                                buildTransferRecord("LG-001", PO_NO, new BigDecimal("30000")),
                                buildTransferRecord("LG-002", "PO0002", new BigDecimal("20000"))));

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        // 两条转应收记录对应两个不同保函，分别回写（预校验与执行期均会查询，按 poNo 稳定返回）
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenAnswer(invocation -> {
                    ExportVehicleCbuPoGuaranteeQo qo = invocation.getArgument(0);
                    if ("PO0002".equals(qo.getPoNo())) {
                        return Arrays.asList(buildPoGuarantee("PG-002", "LG-002", "PO0002", new BigDecimal("200000")));
                    }
                    return Arrays.asList(buildPoGuarantee("PG-001", LG_ID, PO_NO, new BigDecimal("100000")));
                });
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        // 本用例只关注资金动作，抬头明细统计返回空避免影响
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Collections.emptyList());

        processor.process(buildReq(LocalDateTime.now()));

        verify(letterGuaranteeGateway, times(2)).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(purchaseOrderGuaranteeGateway, times(2)).updateExportVehicleCbuPoGuarantee(any());
    }

    // ==================== 幂等与跳过场景 ====================

    @Test
    @DisplayName("重复清账 — 同一(lgId,poNo)已释放流水应跳过资金动作，状态更新幂等执行")
    public void testProcess_sameRecordAlreadyReleasedSkip() {
        // 已释放流水与转应收记录同属 (LG-001, PO0001)：资金动作全部跳过
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        // 抬头统计：该明细已收款 → 已收款
        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        PayableListDetailDo received = buildDetail();
        received.setIsReceived(IsReceivedEnum.YES.getCode());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Arrays.asList(received));

        processor.process(buildReq(LocalDateTime.now()));

        // 资金动作全部跳过（不重复扣减）
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
        // 收款状态与抬头状态仍幂等更新，保证历史脏数据可被纠正
        verify(payableListDetailGateway).updateReceiveStatusById(eq("D-001"),
                eq(IsReceivedEnum.YES.getCode()), any());
        verify(payableListGateway).advanceBillStatus(PAYABLE_LIST_ID, PayableListStatusEnum.RECEIVED.getCode());
    }

    @Test
    @DisplayName("明细不存在 — SO不在清单明细中应抛出业务异常")
    public void testProcess_detailNotExist() {
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(null);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));

        assertTrue(exception.getMessage().contains("不在应付清单明细中"),
                "异常消息应说明 SO 不在清单明细中");
        verify(payableListDetailGateway, never()).updateReceiveStatusById(any(), any(), any());
        verify(vehicleCbuSoGateway, never()).updateVehicleCbuSo(any());
        verify(letterGuaranteeRecordGateway, never()).listExportVehicleLetterGuaranteeRecord(any());
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("无转应收记录 — 仅更新明细/SO状态，不触发保函释放与PO回写")
    public void testProcess_noTransferRecord() {
        // 幂等检查(40)与转应收(30)查询均返回空：该SO未占用保函应收
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());

        // 抬头统计：独立未收款对象，0收款保持原状态
        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        PayableListDetailDo unpaid = buildDetail();
        unpaid.setId("D-002");
        unpaid.setIsReceived(IsReceivedEnum.NO.getCode());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Arrays.asList(unpaid));

        processor.process(buildReq(LocalDateTime.now()));

        // 明细收款字段与SO状态正常更新
        verify(payableListDetailGateway).updateReceiveStatusById(eq("D-001"),
                eq(IsReceivedEnum.YES.getCode()), any());
        verify(vehicleCbuSoGateway).updateVehicleCbuSo(any());
        // 无资金动作
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
        // 0收款时抬头与明细状态均不更新
        verify(payableListGateway, never()).advanceBillStatus(any(), any());
        verify(payableListDetailGateway, never()).advanceBillStatusByPayableListId(any(), any());
        // 锁正常释放
        verify(blockLock).close();
    }

    // ==================== 异常与失败场景 ====================

    @Test
    @DisplayName("PO应收不足 — 预校验拦截，整批拒绝且不进入锁与事务")
    public void testProcess_poReceivableInsufficient() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());

        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        // PO 累计应收只有 30000，需释放 50000 → 不足
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("30000"))));

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));
        assertTrue(exception.getMessage().contains("PO关联保函应收不足"),
                "异常消息应包含 'PO关联保函应收不足'");
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
        // 预校验阶段即拦截，不获取锁、不进入事务
        verify(distributedLockTemplate, never()).blockLock(anyString(), anyLong(), anyLong());
    }

    @Test
    @DisplayName("PO无关联保函记录 — 应抛出业务异常")
    public void testProcess_poNoAssociationEmpty() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        // PO 关联表无该保函记录（空列表）
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Collections.emptyList());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));

        assertTrue(exception.getMessage().contains("PO关联保函记录不存在"),
                "异常消息应说明 PO 无关联保函记录");
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
    }

    @Test
    @DisplayName("保函释放失败 — 应抛出异常回滚SO事务，避免留下部分释放状态")
    public void testProcess_releaseFailRollback() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        // 预校验要求 PO 关联保函余额充足，本用例只关注释放调用失败
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(false);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));
        assertTrue(exception.getMessage().contains("保函应收释放失败"),
                "异常消息应包含 '保函应收释放失败'");
        // 释放失败时不回写PO累计应收，整个SO事务回滚后由SAP重推重放
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
        // 锁仍应在 finally 中释放
        verify(blockLock).close();
    }

    @Test
    @DisplayName("SO主表不存在 — 应抛出业务异常")
    public void testProcess_soNotExist() {
        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        // SO 主表查不到：预校验阶段即拦截，不涉及流水查询
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(null);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));

        assertTrue(exception.getMessage().contains("SO记录不存在"),
                "异常消息应说明 SO 记录不存在");
        verify(vehicleCbuSoGateway, never()).updateVehicleCbuSo(any());
        // 异常中断后不进行保函释放与PO回写
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(purchaseOrderGuaranteeGateway, never()).listExportVehicleCbuPoGuarantee(any());
    }

    @Test
    @DisplayName("获取锁失败 — 应抛出异常且不执行清账逻辑")
    public void testProcess_lockFail() {
        // 预校验通过所需的只读数据（本用例只关注锁获取失败）
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(buildDetail());
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList());
        when(distributedLockTemplate.blockLock(anyString(), anyLong(), anyLong()))
                .thenThrow(new RuntimeException("redis连接失败"));

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));
        assertTrue(exception.getMessage().contains("SAP清账获取锁失败"),
                "异常消息应包含 'SAP清账获取锁失败'");
        // 锁失败后不执行任何清账写入
        verify(payableListDetailGateway, never()).updateReceiveStatusById(any(), any(), any());
        verify(vehicleCbuSoGateway, never()).updateVehicleCbuSo(any());
    }

    @Test
    @DisplayName("清账列表为空 — 应抛出业务异常")
    public void testProcess_emptyClearAccountList() {
        List<SapClearAccountItem> clearAccountList = Collections.emptyList();

        BizException exception = assertThrows(BizException.class, () -> processor.process(clearAccountList));

        assertTrue(exception.getMessage().contains("清账车辆列表不能为空"),
                "异常消息应提示清账车辆列表不能为空");
        verify(payableListDetailGateway, never()).getBySoNo(anyString());
    }

    @Test
    @DisplayName("sorder为空 — 应抛出业务异常")
    public void testProcess_blankSorder() {
        SapClearAccountItem item = new SapClearAccountItem();
        item.setPaynr(PAYABLE_LIST_NO);
        List<SapClearAccountItem> clearAccountList = Arrays.asList(item);

        BizException exception = assertThrows(BizException.class, () -> processor.process(clearAccountList));

        assertTrue(exception.getMessage().contains("sorder"),
                "异常消息应提示 SO订单号不能为空");
        verify(payableListDetailGateway, never()).getBySoNo(anyString());
    }

    @Test
    @DisplayName("paynr为空 — 应抛出业务异常")
    public void testProcess_blankPaynr() {
        SapClearAccountItem item = new SapClearAccountItem();
        item.setSorder(SO_NO);
        List<SapClearAccountItem> clearAccountList = Arrays.asList(item);

        BizException exception = assertThrows(BizException.class, () -> processor.process(clearAccountList));

        assertTrue(exception.getMessage().contains("paynr"),
                "异常消息应提示应付清单编号不能为空");
        verify(payableListDetailGateway, never()).getBySoNo(anyString());
    }

    @Test
    @DisplayName("paynr与明细所属清单不一致 — 应抛出业务异常")
    public void testProcess_paynrMismatch() {
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(buildDetail());
        // 明细所属清单编号为 YF202606000001，回调传入其它编号
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));

        List<SapClearAccountItem> clearAccountList = buildReq(LocalDateTime.now());
        clearAccountList.get(0).setPaynr("YF202609000005");

        BizException exception = assertThrows(BizException.class, () -> processor.process(clearAccountList));

        assertTrue(exception.getMessage().contains("应付清单编号不一致"),
                "异常消息应说明 paynr 与明细所属清单不一致");
        verify(payableListDetailGateway, never()).updateReceiveStatusById(any(), any(), any());
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("多条记录有误 — 所有错误聚合到同一错误原因返回，且不产生任何写入")
    public void testProcess_multipleErrorsAggregated() {
        // 第1条 sorder 缺失；第2条 paynr 与明细所属清单不一致；第3条 SO 不在清单明细中
        when(payableListDetailGateway.getBySoNo("SO0002")).thenReturn(buildDetail());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        when(payableListDetailGateway.getBySoNo("SO0003")).thenReturn(null);

        SapClearAccountItem item1 = new SapClearAccountItem();
        item1.setPaynr(PAYABLE_LIST_NO);
        SapClearAccountItem item2 = new SapClearAccountItem();
        item2.setSorder("SO0002");
        item2.setPaynr("YF202609000005");
        SapClearAccountItem item3 = new SapClearAccountItem();
        item3.setSorder("SO0003");
        item3.setPaynr(PAYABLE_LIST_NO);
        List<SapClearAccountItem> clearAccountList = Arrays.asList(item1, item2, item3);

        BizException exception = assertThrows(BizException.class, () -> processor.process(clearAccountList));

        String message = exception.getMessage();
        assertTrue(message.contains("共3项"), "应汇总全部错误项数，实际：" + message);
        assertTrue(message.contains("第1条") && message.contains("sorder"),
                "应包含第1条 sorder 缺失，实际：" + message);
        assertTrue(message.contains("第2条") && message.contains("应付清单编号不一致"),
                "应包含第2条 paynr 不一致，实际：" + message);
        assertTrue(message.contains("第3条") && message.contains("不在应付清单明细中"),
                "应包含第3条明细缺失，实际：" + message);
        // 任一错误即整批拒绝：不获取锁、不产生任何写入
        verify(distributedLockTemplate, never()).blockLock(anyString(), anyLong(), anyLong());
        verify(payableListDetailGateway, never()).updateReceiveStatusById(any(), any(), any());
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("同一请求内 sorder 重复 — 按重复报错并整批拒绝，不误报应收不足")
    public void testProcess_duplicateSorderInSameRequest() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());
        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        // PO 关联保函应收刚好只够释放一条：若第2条参与累加会变成 100000 > 50000 而误报"应收不足"
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("50000"))));

        SapClearAccountItem item1 = new SapClearAccountItem();
        item1.setPaynr(PAYABLE_LIST_NO);
        item1.setSorder(SO_NO);
        item1.setVhvin(VIN);
        item1.setAugdt(LocalDateTime.now());
        SapClearAccountItem item2 = new SapClearAccountItem();
        item2.setPaynr(PAYABLE_LIST_NO);
        item2.setSorder(SO_NO);
        item2.setVhvin(VIN);
        item2.setAugdt(LocalDateTime.now());
        List<SapClearAccountItem> clearAccountList = Arrays.asList(item1, item2);

        BizException exception = assertThrows(BizException.class, () -> processor.process(clearAccountList));

        String message = exception.getMessage();
        assertTrue(message.contains("共1项"), "重复只汇总一条错误，实际：" + message);
        assertTrue(message.contains("第2条") && message.contains("sorder=" + SO_NO)
                        && message.contains("与第1条重复"),
                "应指出第2条 sorder 与第1条重复，实际：" + message);
        assertFalse(message.contains("应收不足"), "重复条目不应再参与应收金额累加，实际：" + message);
        // 整批拒绝：不获取锁、不产生任何写入
        verify(distributedLockTemplate, never()).blockLock(anyString(), anyLong(), anyLong());
        verify(payableListDetailGateway, never()).updateReceiveStatusById(any(), any(), any());
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("其中一条有误 — 整批拒绝执行，合法记录也不进入清账流程")
    public void testProcess_oneInvalidRejectsWholeBatch() {
        // 第1条数据完整合法，第2条 SO 不在清单明细中
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(buildDetail());
        when(payableListDetailGateway.getBySoNo("SO0002")).thenReturn(null);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList());

        SapClearAccountItem item1 = new SapClearAccountItem();
        item1.setSorder(SO_NO);
        item1.setPaynr(PAYABLE_LIST_NO);
        item1.setVhvin(VIN);
        item1.setAugdt(LocalDateTime.now());
        SapClearAccountItem item2 = new SapClearAccountItem();
        item2.setSorder("SO0002");
        item2.setPaynr(PAYABLE_LIST_NO);
        item2.setVhvin(VIN);
        item2.setAugdt(LocalDateTime.now());
        List<SapClearAccountItem> clearAccountList = Arrays.asList(item1, item2);

        BizException exception = assertThrows(BizException.class, () -> processor.process(clearAccountList));

        assertTrue(exception.getMessage().contains("第2条")
                        && exception.getMessage().contains("不在应付清单明细中"),
                "错误定位应指向第2条，实际：" + exception.getMessage());
        // 第1条虽已通过校验，也必须整批拒绝：不收款、不释放、不推进状态、不获取锁
        verify(payableListDetailGateway, never()).updateReceiveStatusById(any(), any(), any());
        verify(vehicleCbuSoGateway, never()).updateVehicleCbuSo(any());
        verify(payableListGateway, never()).advanceBillStatus(any(), any());
        verify(distributedLockTemplate, never()).blockLock(anyString(), anyLong(), anyLong());
    }

    // ==================== 抬头状态统计场景 ====================

    @Test
    @DisplayName("抬头状态 — 部分收款应更新为部分收款")
    public void testProcess_headerPartialReceived() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));

        // 抬头下共 2 条明细：1 条已收款 + 1 条未收款 → 部分收款
        PayableListDetailDo received = buildDetail();
        received.setIsReceived(IsReceivedEnum.YES.getCode());
        PayableListDetailDo unpaid = buildDetail();
        unpaid.setId("D-002");
        unpaid.setIsReceived(IsReceivedEnum.NO.getCode());
        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Arrays.asList(received, unpaid));

        processor.process(buildReq(LocalDateTime.now()));

        // 部分收款 → 抬头与明细状态单调前进到部分收款
        verify(payableListGateway).advanceBillStatus(PAYABLE_LIST_ID,
                PayableListStatusEnum.PARTIAL_RECEIVED.getCode());
        verify(payableListDetailGateway).advanceBillStatusByPayableListId(PAYABLE_LIST_ID,
                PayableListStatusEnum.PARTIAL_RECEIVED.getCode());
    }

    @Test
    @DisplayName("抬头状态 — 同一抬头多条明细清账时按抬头去重统计一次")
    public void testProcess_headerDeduplicatedByPayableListId() {
        // 两个 SO 属于同一抬头，流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail1 = buildDetail();
        PayableListDetailDo detail2 = buildDetail();
        detail2.setId("D-002");
        detail2.setSoNo("SO0002");
        when(payableListDetailGateway.getBySoNo("SO0001")).thenReturn(detail1);
        when(payableListDetailGateway.getBySoNo("SO0002")).thenReturn(detail2);

        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenAnswer(invocation -> Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));

        // 抬头下共 2 条明细，清账后统计应全部已收款 → 已收款
        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        PayableListDetailDo received1 = buildDetail();
        received1.setIsReceived(IsReceivedEnum.YES.getCode());
        PayableListDetailDo received2 = buildDetail();
        received2.setId("D-002");
        received2.setIsReceived(IsReceivedEnum.YES.getCode());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Arrays.asList(received1, received2));

        SapClearAccountItem item1 = new SapClearAccountItem();
        item1.setPaynr(PAYABLE_LIST_NO);
        item1.setSorder("SO0001");
        item1.setVhvin(VIN);
        item1.setAugdt(LocalDateTime.now());
        SapClearAccountItem item2 = new SapClearAccountItem();
        item2.setPaynr(PAYABLE_LIST_NO);
        item2.setSorder("SO0002");
        item2.setVhvin(VIN);
        item2.setAugdt(LocalDateTime.now());
        List<SapClearAccountItem> clearAccountList = Arrays.asList(item1, item2);

        processor.process(clearAccountList);

        // 同一抬头只统计并更新一次（去重）
        verify(payableListGateway, times(1)).advanceBillStatus(PAYABLE_LIST_ID,
                PayableListStatusEnum.RECEIVED.getCode());
        // 明细冗余单据状态同步同样按抬头去重，只同步一次
        verify(payableListDetailGateway, times(1)).advanceBillStatusByPayableListId(
                PAYABLE_LIST_ID, PayableListStatusEnum.RECEIVED.getCode());
    }

    @Test
    @DisplayName("抬头不存在 — 应抛出业务异常")
    public void testProcess_headerNotExist() {
        // 明细存在但所属抬头查不到（脏数据）
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(buildDetail());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(null);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));

        assertTrue(exception.getMessage().contains("应付清单不存在"),
                "异常消息应说明明细所属应付清单不存在");
        // 抬头不存在时不做任何抬头/明细状态更新
        verify(payableListGateway, never()).advanceBillStatus(any(), any());
        verify(payableListDetailGateway, never()).advanceBillStatusByPayableListId(any(), any());
    }

    @Test
    @DisplayName("抬头状态 — 无收款明细时保持原状态不变")
    public void testProcess_headerNoReceivedKeepStatus() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        // 资金动作正常完成，本用例只关注抬头统计结果
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));

        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        // 统计用独立对象（未收款），不受 processOne 对共享对象修改的影响
        PayableListDetailDo unpaid = buildDetail();
        unpaid.setId("D-002");
        unpaid.setIsReceived(IsReceivedEnum.NO.getCode());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Arrays.asList(unpaid));

        processor.process(buildReq(LocalDateTime.now()));

        // 0收款时抬头与明细状态均保持不变（不做任何状态更新）
        verify(payableListGateway, never()).advanceBillStatus(any(), any());
        verify(payableListDetailGateway, never()).advanceBillStatusByPayableListId(any(), any());
    }

    @Test
    @DisplayName("抬头驳回态 — 前置校验拦截，不允许被清账推进收款状态")
    public void testProcess_rejectedHeaderNotPromoted() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        // 资金动作正常完成，本用例只关注状态推进被前置校验拦住
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));

        // 抬头处于驳回(40)：编码比较 40 < 50/60，若无前置校验会被单调前进直接推成已收款
        PayableListDo header = buildHeader(PayableListStatusEnum.REJECTED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);

        processor.process(buildReq(LocalDateTime.now()));

        // 明细收款状态仍更新（资金链路不受状态校验影响）
        verify(payableListDetailGateway).updateReceiveStatusById(eq("D-001"),
                eq(IsReceivedEnum.YES.getCode()), any());
        // 抬头与明细冗余状态均不推进
        verify(payableListGateway, never()).advanceBillStatus(any(), any());
        verify(payableListDetailGateway, never()).advanceBillStatusByPayableListId(any(), any());
    }

    @Test
    @DisplayName("部分已释放 — 仅补齐未释放的转应收记录，已释放记录不重复扣减")
    public void testProcess_partialReleasedOnlyReleaseRemaining() {
        // 已释放流水仅含 (LG-001, PO0001)；转应收记录有两条：(LG-001,PO0001) 与 (LG-002,PO0002)
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Arrays.asList(buildTransferRecord("LG-001", PO_NO, new BigDecimal("30000")))
                        : Arrays.asList(
                                buildTransferRecord("LG-001", PO_NO, new BigDecimal("30000")),
                                buildTransferRecord("LG-002", "PO0002", new BigDecimal("20000"))));

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee("PG-002", "LG-002", "PO0002", new BigDecimal("200000"))));
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildHeader(PayableListStatusEnum.CONFIRMED.getCode()));
        // 本用例只关注资金动作，抬头明细统计返回空避免影响
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Collections.emptyList());

        processor.process(buildReq(LocalDateTime.now()));

        // 只补齐未释放的 LG-002，已释放的 LG-001 不重复释放
        ArgumentCaptor<String> lgCaptor = ArgumentCaptor.forClass(String.class);
        verify(letterGuaranteeGateway, times(1)).releaseReceivable(lgCaptor.capture(), any(), anyString(),
                eq("PO0002"), anyString(), anyString(), anyString());
        assertEquals("LG-002", lgCaptor.getValue(), "仅应释放未释放过的保函记录");
        verify(purchaseOrderGuaranteeGateway, times(1)).updateExportVehicleCbuPoGuarantee(any());
    }

    @Test
    @DisplayName("抬头下无明细 — 统计跳过，不更新任何状态")
    public void testProcess_headerEmptyDetailsSkip() {
        // 流水查询按类型区分：幂等检查(40)返回空，转应收(30)返回记录
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenAnswer(invocation -> isReleaseReceivableQuery(invocation.getArgument(0))
                        ? Collections.emptyList() : buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));

        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        // 脏数据场景：抬头下明细为空，不得因 0==0 被误判为"全部收款"
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Collections.emptyList());

        processor.process(buildReq(LocalDateTime.now()));

        verify(payableListGateway, never()).advanceBillStatus(any(), any());
        verify(payableListDetailGateway, never()).advanceBillStatusByPayableListId(any(), any());
    }

    // ==================== 辅助方法 ====================

    private List<SapClearAccountItem> buildReq(LocalDateTime receiveTime) {
        SapClearAccountItem item = new SapClearAccountItem();
        item.setPaynr(PAYABLE_LIST_NO);
        item.setSorder(SO_NO);
        item.setVhvin(VIN);
        item.setAugdt(receiveTime);
        return Arrays.asList(item);
    }

    private List<ExportVehicleLetterGuaranteeRecordDo> buildTransferRecords() {
        return Arrays.asList(buildTransferRecord(LG_ID, PO_NO, new BigDecimal("50000")));
    }

    private ExportVehicleLetterGuaranteeRecordDo buildTransferRecord(String lgId, String poNo, BigDecimal amount) {
        ExportVehicleLetterGuaranteeRecordDo record = new ExportVehicleLetterGuaranteeRecordDo();
        record.setLgId(lgId);
        record.setPoNo(poNo);
        record.setSoNo(SO_NO);
        record.setTransactionAmount(amount);
        record.setTransactionType(RecordTypeEnum.TRANSFER_RECEIVABLE.getCode());
        return record;
    }

    private PayableListDetailDo buildDetail() {
        PayableListDetailDo detail = new PayableListDetailDo();
        detail.setId("D-001");
        detail.setPayableListId(PAYABLE_LIST_ID);
        detail.setSoId(SO_ID);
        detail.setSoNo(SO_NO);
        detail.setVin(VIN);
        detail.setIsReceived(IsReceivedEnum.NO.getCode());
        return detail;
    }

    private VehicleCbuSoDo buildSoDo() {
        VehicleCbuSoDo soDo = new VehicleCbuSoDo();
        soDo.setId(SO_ID);
        soDo.setSoNo(SO_NO);
        soDo.setOrderStatus(VehicleCbuSoStatusEnum.QC_PASSED.getCode());
        return soDo;
    }

    private PayableListDo buildHeader(String billStatus) {
        PayableListDo header = new PayableListDo();
        header.setId(PAYABLE_LIST_ID);
        header.setPayableListNo(PAYABLE_LIST_NO);
        header.setBillStatus(billStatus);
        return header;
    }

    private ExportVehicleCbuPoGuaranteeDo buildPoGuarantee(BigDecimal receivable) {
        return buildPoGuarantee("PG-001", LG_ID, PO_NO, receivable);
    }

    private ExportVehicleCbuPoGuaranteeDo buildPoGuarantee(String id, String lgId, String poNo, BigDecimal receivable) {
        ExportVehicleCbuPoGuaranteeDo poGuarantee = new ExportVehicleCbuPoGuaranteeDo();
        poGuarantee.setId(id);
        poGuarantee.setPoNo(poNo);
        poGuarantee.setGuaranteeId(lgId);
        poGuarantee.setPoCurrentReceivableAmount(receivable);
        return poGuarantee;
    }

    /**
     * 判断流水查询是否为应收释放(40)幂等检查，用于按查询类型区分 mock 返回
     */
    private boolean isReleaseReceivableQuery(Object qo) {
        if (!(qo instanceof com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo)) {
            return false;
        }
        return RecordTypeEnum.RELEASE_RECEIVABLE.getCode()
                .equals(((com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo) qo).getTransactionType());
    }
}
