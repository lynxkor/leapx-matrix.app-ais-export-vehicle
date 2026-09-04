package com.leapmotor.export.vehicle.payablelist.executor.integration;

import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.letterGuarantee.enums.RecordTypeEnum;
import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountReq;
import com.leapmotor.export.vehicle.payablelist.enums.IsReceivedEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
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
 * 并覆盖文档未提及但代码已实现的关键逻辑：重复清账幂等、PO关联保函应收不足、释放失败告警。
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
        // 幂等检查：无 type=40 释放流水
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())   // 第一次：type=40 幂等检查 → 空
                .thenReturn(buildTransferRecords());    // 第二次：type=30 转应收记录

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
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Arrays.asList(detail));

        processor.process(buildReq(LocalDateTime.of(2026, 8, 20, 15, 0, 0)));

        // 1. 明细更新：是否收款=是 + 收款时间
        ArgumentCaptor<PayableListDetailDo> detailCaptor = ArgumentCaptor.forClass(PayableListDetailDo.class);
        verify(payableListDetailGateway).updatePayableListDetail(detailCaptor.capture());
        assertEquals(IsReceivedEnum.YES.getCode(), detailCaptor.getValue().getIsReceived(),
                "明细是否收款应更新为是");
        assertEquals(LocalDateTime.of(2026, 8, 20, 15, 0, 0), detailCaptor.getValue().getReceiveTime(),
                "明细收款时间应记录");

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

        // 5. 抬头状态：全部收款 → 已收款
        ArgumentCaptor<PayableListDo> headerCaptor = ArgumentCaptor.forClass(PayableListDo.class);
        verify(payableListGateway).updatePayableList(headerCaptor.capture());
        assertEquals(PayableListStatusEnum.RECEIVED.getCode(), headerCaptor.getValue().getBillStatus(),
                "全部收款后抬头状态应为已收款");
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
        // 两条转应收记录对应两个不同保函，分别回写
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee("PG-001", LG_ID, PO_NO, new BigDecimal("100000"))))
                .thenReturn(Arrays.asList(buildPoGuarantee("PG-002", "LG-002", "PO0002", new BigDecimal("200000"))));

        processor.process(buildReq(LocalDateTime.now()));

        verify(letterGuaranteeGateway, times(2)).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(purchaseOrderGuaranteeGateway, times(2)).updateExportVehicleCbuPoGuarantee(any());
    }

    // ==================== 幂等与跳过场景 ====================

    @Test
    @DisplayName("重复清账 — 已存在释放流水应幂等跳过，不做任何更新")
    public void testProcess_idempotentSkip() {
        // 幂等检查命中：存在 type=40 流水
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Arrays.asList(buildTransferRecord(LG_ID, PO_NO, new BigDecimal("50000"))));

        processor.process(buildReq(LocalDateTime.now()));

        verify(payableListDetailGateway, never()).updatePayableListDetail(any());
        verify(vehicleCbuSoGateway, never()).updateVehicleCbuSo(any());
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
    }

    @Test
    @DisplayName("明细不存在 — SO不在清单明细中应跳过处理")
    public void testProcess_detailNotExistSkip() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList());
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(null);

        processor.process(buildReq(LocalDateTime.now()));

        verify(payableListDetailGateway, never()).updatePayableListDetail(any());
        verify(vehicleCbuSoGateway, never()).updateVehicleCbuSo(any());
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

        // 明细与SO状态正常更新
        verify(payableListDetailGateway).updatePayableListDetail(any());
        verify(vehicleCbuSoGateway).updateVehicleCbuSo(any());
        // 无资金动作
        verify(letterGuaranteeGateway, never()).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
        // 锁正常释放
        verify(blockLock).close();
    }

    // ==================== 异常与失败场景 ====================

    @Test
    @DisplayName("PO应收不足 — 应抛出异常使事务回滚")
    public void testProcess_poReceivableInsufficient() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);

        // PO 累计应收只有 30000，需释放 50000 → 不足
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("30000"))));

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));
        assertTrue(exception.getMessage().contains("PO关联保函应收不足"),
                "异常消息应包含 'PO关联保函应收不足'");
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
        // 事务内异常后锁仍应在 finally 中释放
        verify(blockLock).close();
    }

    @Test
    @DisplayName("PO无关联保函记录 — 回写静默跳过不抛异常")
    public void testProcess_poNoAssociationEmpty() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        // PO 关联表无该保函记录（空列表，走 for 循环不匹配后静默结束）
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Collections.emptyList());

        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Arrays.asList(detail));

        assertDoesNotThrow(() -> processor.process(buildReq(LocalDateTime.now())));
        // 释放成功但 PO 无匹配记录时不做回写，不影响后续流程
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
    }

    @Test
    @DisplayName("保函释放失败 — 返回false时应告警且不回写PO")
    public void testProcess_releaseFailNoPoWriteback() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(false);

        processor.process(buildReq(LocalDateTime.now()));

        verify(purchaseOrderGuaranteeGateway, never()).listExportVehicleCbuPoGuarantee(any());
        verify(purchaseOrderGuaranteeGateway, never()).updateExportVehicleCbuPoGuarantee(any());
    }

    @Test
    @DisplayName("SO主表不存在 — 仅告警不抛异常，保函释放仍继续")
    public void testProcess_soNotExistWarnOnly() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(null);
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));

        assertDoesNotThrow(() -> processor.process(buildReq(LocalDateTime.now())));
        verify(letterGuaranteeGateway, times(1)).releaseReceivable(anyString(), any(), anyString(),
                anyString(), anyString(), anyString(), anyString());
        verify(vehicleCbuSoGateway, never()).updateVehicleCbuSo(any());
    }

    @Test
    @DisplayName("获取锁失败 — 应抛出异常且不执行清账逻辑")
    public void testProcess_lockFail() {
        when(distributedLockTemplate.blockLock(anyString(), anyLong(), anyLong()))
                .thenThrow(new RuntimeException("redis连接失败"));

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(buildReq(LocalDateTime.now())));
        assertTrue(exception.getMessage().contains("SAP清账获取锁失败"),
                "异常消息应包含 'SAP清账获取锁失败'");
        verify(payableListDetailGateway, never()).getBySoNo(anyString());
    }

    // ==================== 抬头状态统计场景 ====================

    @Test
    @DisplayName("抬头状态 — 部分收款应更新为部分收款")
    public void testProcess_headerPartialReceived() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(Arrays.asList(buildPoGuarantee(new BigDecimal("100000"))));

        // 抬头下共 2 条明细：1 条已收款 + 1 条未收款 → 部分收款
        PayableListDetailDo unpaid = buildDetail();
        unpaid.setId("D-002");
        unpaid.setIsReceived(IsReceivedEnum.NO.getCode());
        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Arrays.asList(detail, unpaid));

        processor.process(buildReq(LocalDateTime.now()));

        ArgumentCaptor<PayableListDo> headerCaptor = ArgumentCaptor.forClass(PayableListDo.class);
        verify(payableListGateway).updatePayableList(headerCaptor.capture());
        assertEquals(PayableListStatusEnum.PARTIAL_RECEIVED.getCode(), headerCaptor.getValue().getBillStatus(),
                "部分收款后抬头状态应为部分收款");
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

        SapClearAccountReq req = new SapClearAccountReq();
        SapClearAccountReq.SapClearAccountItem item1 = new SapClearAccountReq.SapClearAccountItem();
        item1.setSoNo("SO0001");
        item1.setVin(VIN);
        item1.setReceiveTime(LocalDateTime.now());
        SapClearAccountReq.SapClearAccountItem item2 = new SapClearAccountReq.SapClearAccountItem();
        item2.setSoNo("SO0002");
        item2.setVin(VIN);
        item2.setReceiveTime(LocalDateTime.now());
        req.setClearAccountList(Arrays.asList(item1, item2));

        processor.process(req);

        // 同一抬头只统计并更新一次（去重）
        ArgumentCaptor<PayableListDo> headerCaptor = ArgumentCaptor.forClass(PayableListDo.class);
        verify(payableListGateway, times(1)).updatePayableList(headerCaptor.capture());
        assertEquals(PayableListStatusEnum.RECEIVED.getCode(), headerCaptor.getValue().getBillStatus(),
                "两明细均收款后抬头状态应为已收款");
    }

    @Test
    @DisplayName("抬头不存在 — 状态统计跳过该明细不抛异常")
    public void testProcess_headerNotExistSkip() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);
        when(vehicleCbuSoGateway.getVehicleCbuSoById(SO_ID)).thenReturn(buildSoDo());
        when(letterGuaranteeGateway.releaseReceivable(anyString(), any(), anyString(), anyString(),
                anyString(), anyString(), anyString())).thenReturn(true);
        // PO 关联查询返回 null（脏数据场景，走告警分支）
        when(purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(any()))
                .thenReturn(null);
        // 抬头查不到（脏数据）：updateHeaderStatus 中 continue
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(null);

        assertDoesNotThrow(() -> processor.process(buildReq(LocalDateTime.now())));
        // 抬头不存在时不做任何抬头更新
        verify(payableListGateway, never()).updatePayableList(any());
    }

    @Test
    @DisplayName("抬头状态 — 无收款明细时保持原状态不变")
    public void testProcess_headerNoReceivedKeepStatus() {
        when(letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(any()))
                .thenReturn(Collections.emptyList())
                .thenReturn(buildTransferRecords());

        PayableListDetailDo detail = buildDetail();
        when(payableListDetailGateway.getBySoNo(SO_NO)).thenReturn(detail);

        PayableListDo header = buildHeader(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID)).thenReturn(header);
        // 统计用独立对象（未收款），不受 processOne 对共享对象修改的影响
        PayableListDetailDo unpaid = buildDetail();
        unpaid.setId("D-002");
        unpaid.setIsReceived(IsReceivedEnum.NO.getCode());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(Arrays.asList(unpaid));

        processor.process(buildReq(LocalDateTime.now()));

        ArgumentCaptor<PayableListDo> headerCaptor = ArgumentCaptor.forClass(PayableListDo.class);
        verify(payableListGateway).updatePayableList(headerCaptor.capture());
        assertEquals(PayableListStatusEnum.CONFIRMED.getCode(), headerCaptor.getValue().getBillStatus(),
                "0收款时抬头状态应保持不变");
    }

    // ==================== 辅助方法 ====================

    private SapClearAccountReq buildReq(LocalDateTime receiveTime) {
        SapClearAccountReq req = new SapClearAccountReq();
        SapClearAccountReq.SapClearAccountItem item = new SapClearAccountReq.SapClearAccountItem();
        item.setSoNo(SO_NO);
        item.setVin(VIN);
        item.setReceiveTime(receiveTime);
        req.setClearAccountList(Arrays.asList(item));
        return req;
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
        header.setPayableListNo("YF202606000001");
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
