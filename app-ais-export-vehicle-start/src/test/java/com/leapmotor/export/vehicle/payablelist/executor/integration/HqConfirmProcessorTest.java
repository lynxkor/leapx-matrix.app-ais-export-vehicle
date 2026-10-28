package com.leapmotor.export.vehicle.payablelist.executor.integration;

import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 红旗应付清单确认结果处理器单元测试
 *
 * <p>对应《红旗G117项目整车销售-应付清单管理.md》"应付清单-财务提交"章节的红旗确认回调场景，
 * 并覆盖文档未提及但代码已实现的关键逻辑：重复回调幂等、状态机边界、并发锁、SAP事件发布。
 *
 * @author huangtao
 * @since 2026-09-02
 */
@DisplayName("红旗确认结果处理器单元测试")
@ExtendWith(MockitoExtension.class)
public class HqConfirmProcessorTest {

    @Mock
    private PayableListGateway payableListGateway;
    @Mock
    private PayableListDetailGateway payableListDetailGateway;
    @Mock
    private EventHandlerHelper eventHandlerHelper;
    @Mock
    private DistributedLockTemplate distributedLockTemplate;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private BlockLock blockLock;

    private HqConfirmProcessor processor;

    private static final String PAYABLE_LIST_NO = "YF202606000001";

    /**
     * 红旗确认结果值域（HqConfirmReq.status）：A=确认 / R=驳回。
     * 与单据状态码（PayableListStatusEnum：30/40）不同域，勿混用。
     */
    private static final String CONFIRM_RESULT_APPROVE = "A";
    private static final String CONFIRM_RESULT_REJECT = "R";

    @BeforeEach
    public void setUp() {
        processor = new HqConfirmProcessor(payableListGateway, payableListDetailGateway,
                eventHandlerHelper, distributedLockTemplate, transactionTemplate);
        // 事务回调真实执行：模拟事务内逻辑；lenient 容忍锁失败等场景下未被使用的 stub
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<Object> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        lenient().when(distributedLockTemplate.blockLock(anyString(), anyLong(), anyLong())).thenReturn(blockLock);
    }

    // ==================== 确认场景 ====================

    @Test
    @DisplayName("应付清单编号为空 — 应抛出明确提示且不进入锁与事务")
    public void testProcess_blankPayableListNo() {
        BizException exception = assertThrows(BizException.class,
                () -> processor.process(null, CONFIRM_RESULT_APPROVE, LocalDateTime.now(), "确认"));

        assertTrue(exception.getMessage().contains("应付清单编号不能为空"),
                "异常消息应明确提示清单编号缺失");
        verifyNoInteractions(distributedLockTemplate);
    }

    @Test
    @DisplayName("确认成功 — 已提交状态确认后变为已确认并发布SAP同步事件")
    public void testProcess_confirmSuccess() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.SUBMITTED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        LocalDateTime confirmTime = LocalDateTime.of(2026, 8, 20, 10, 30, 0);
        processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_APPROVE, confirmTime, "确认通过");

        // 状态更新并落库
        ArgumentCaptor<PayableListDo> updateCaptor = ArgumentCaptor.forClass(PayableListDo.class);
        verify(payableListGateway).updatePayableList(updateCaptor.capture());
        assertEquals(PayableListStatusEnum.CONFIRMED.getCode(), updateCaptor.getValue().getBillStatus(),
                "确认后单据状态应为已确认");
        assertEquals(confirmTime, updateCaptor.getValue().getConfirmTime(), "确认时间应记录");

        // 明细表冗余字段同步（单据状态 + 确认时间；提交日期不回写）
        verify(payableListDetailGateway).updateBillStatusByPayableListId(
                eq(db.getId()), eq(PayableListStatusEnum.CONFIRMED.getCode()), isNull(), eq(confirmTime));

        // SAP 同步事件发布：只携带清单编号，载荷不预构建（明细由消费端回查）
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventHandlerHelper).publishAsync(
                eq(ThirdTargetPlatformEnum.SAP),
                eq(SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT),
                eq(PAYABLE_LIST_NO),
                payloadCaptor.capture());
        assertNull(payloadCaptor.getValue(), "事件载荷应为 null：全量明细不再写入定长 data_param");

        // 确认环节不应发生明细查询：明细属于消费端职责，此处查了也不用
        verify(payableListDetailGateway, never()).listByPayableListId(anyString());
    }

    @Test
    @DisplayName("确认幂等 — 已是已确认状态重复确认应直接成功且不重复发布SAP事件")
    public void testProcess_confirmIdempotent() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_APPROVE,
                LocalDateTime.now(), "重复确认");

        verify(payableListGateway, never()).updatePayableList(any());
        verifyNoInteractions(eventHandlerHelper);
        verify(payableListDetailGateway, never()).updateBillStatusByPayableListId(any(), any(), any(), any());
    }

    @Test
    @DisplayName("确认状态机 — 草稿状态确认应抛出异常")
    public void testProcess_confirmFromDraftRejected() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.DRAFT.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_APPROVE,
                        LocalDateTime.now(), "确认"));
        assertTrue(exception.getMessage().contains("当前状态不允许确认"),
                "异常消息应包含 '当前状态不允许确认'");
        verify(payableListGateway, never()).updatePayableList(any());
        verifyNoInteractions(eventHandlerHelper);
    }

    // ==================== 驳回场景 ====================

    @Test
    @DisplayName("驳回成功 — 已提交状态驳回后变为驳回并记录原因")
    public void testProcess_rejectSuccess() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.SUBMITTED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        LocalDateTime confirmTime = LocalDateTime.of(2026, 8, 20, 11, 0, 0);
        processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_REJECT, confirmTime, "信息有误");

        ArgumentCaptor<PayableListDo> updateCaptor = ArgumentCaptor.forClass(PayableListDo.class);
        verify(payableListGateway).updatePayableList(updateCaptor.capture());
        assertEquals(PayableListStatusEnum.REJECTED.getCode(), updateCaptor.getValue().getBillStatus(),
                "驳回后单据状态应为驳回");
        assertEquals("信息有误", updateCaptor.getValue().getConfirmRemark(), "驳回原因应记录");
        assertEquals(confirmTime, updateCaptor.getValue().getConfirmTime(), "驳回时间应记录");

        // 明细表冗余字段同步（单据状态 + 确认时间）
        verify(payableListDetailGateway).updateBillStatusByPayableListId(
                eq(db.getId()), eq(PayableListStatusEnum.REJECTED.getCode()), isNull(), eq(confirmTime));

        // 驳回不发布 SAP 事件
        verifyNoInteractions(eventHandlerHelper);
    }

    @Test
    @DisplayName("驳回幂等 — 已是驳回状态重复驳回应直接成功")
    public void testProcess_rejectIdempotent() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.REJECTED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_REJECT,
                LocalDateTime.now(), "再次驳回");

        verify(payableListGateway, never()).updatePayableList(any());
        verify(payableListDetailGateway, never()).updateBillStatusByPayableListId(any(), any(), any(), any());
    }

    @Test
    @DisplayName("驳回校验 — 驳回原因为空应抛出异常")
    public void testProcess_rejectRemarkBlank() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.SUBMITTED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_REJECT,
                        LocalDateTime.now(), "  "));
        assertTrue(exception.getMessage().contains("驳回原因不能为空"),
                "异常消息应包含 '驳回原因不能为空'");
        verify(payableListGateway, never()).updatePayableList(any());
    }

    @Test
    @DisplayName("驳回状态机 — 已确认状态驳回应抛出异常")
    public void testProcess_rejectFromConfirmedRejected() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.CONFIRMED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_REJECT,
                        LocalDateTime.now(), "驳回"));
        assertTrue(exception.getMessage().contains("当前状态不允许驳回"),
                "异常消息应包含 '当前状态不允许驳回'");
        verify(payableListGateway, never()).updatePayableList(any());
    }

    // ==================== 异常与边界场景 ====================

    @Test
    @DisplayName("清单不存在 — 应抛出异常")
    public void testProcess_listNotExist() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(null);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_APPROVE,
                        LocalDateTime.now(), "确认"));
        assertTrue(exception.getMessage().contains("应付清单不存在"),
                "异常消息应包含 '应付清单不存在'");
    }

    @Test
    @DisplayName("无效确认结果 — 应抛出异常")
    public void testProcess_invalidConfirmResult() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.SUBMITTED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        BizException exception = assertThrows(BizException.class,
                () -> processor.process(PAYABLE_LIST_NO, "99", LocalDateTime.now(), "未知"));
        assertTrue(exception.getMessage().contains("无效的确认结果"),
                "异常消息应包含 '无效的确认结果'");
    }

    @Test
    @DisplayName("确认成功 — 清单无明细时仍发布事件，明细缺失由消费端拦截")
    public void testProcess_confirmSuccessNoDetails() {
        PayableListDo db = buildPayableList(PayableListStatusEnum.SUBMITTED.getCode());
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(db);

        processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_APPROVE,
                LocalDateTime.of(2026, 8, 20, 10, 30, 0), "确认通过");

        // 事件照常发布：确认环节不查明细，因此"无明细"不会在此处暴露或阻断；
        // 明细是否存在、是否完整由 PayableListSyncSapHandler 回查时校验并抛错重试
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventHandlerHelper).publishAsync(
                eq(ThirdTargetPlatformEnum.SAP),
                eq(SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT),
                eq(PAYABLE_LIST_NO),
                payloadCaptor.capture());
        assertNull(payloadCaptor.getValue(), "无明细时事件载荷同样为 null");
    }

    @Test
    @DisplayName("获取锁失败 — 应抛出异常且不执行任何更新")
    public void testProcess_lockFail() {
        when(distributedLockTemplate.blockLock(anyString(), anyLong(), anyLong()))
                .thenThrow(new RuntimeException("redis连接失败"));

        // BizException(ResultEnum, String) 的 getMessage 返回 ResultEnum 默认消息，故仅断言异常类型
        assertThrows(BizException.class,
                () -> processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_APPROVE,
                        LocalDateTime.now(), "确认"));
        verify(payableListGateway, never()).getPayableListByNo(anyString());
        verify(payableListGateway, never()).updatePayableList(any());
    }

    @Test
    @DisplayName("事务内异常 — 锁应在finally中释放")
    public void testProcess_lockReleasedAfterException() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(null);

        // 事务回调内抛 BizException（清单不存在），验证锁仍被释放
        assertThrows(BizException.class,
                () -> processor.process(PAYABLE_LIST_NO, CONFIRM_RESULT_APPROVE,
                        LocalDateTime.now(), "确认"));

        verify(blockLock).close();
    }

    // ==================== 辅助方法 ====================

    private PayableListDo buildPayableList(String billStatus) {
        PayableListDo db = new PayableListDo();
        db.setId("PL-001");
        db.setPayableListNo(PAYABLE_LIST_NO);
        db.setBillStatus(billStatus);
        db.setCustomerCode("CUST001");
        db.setCustomerName("测试客户");
        db.setTotalAmount(new BigDecimal("300000.00"));
        return db;
    }
}
