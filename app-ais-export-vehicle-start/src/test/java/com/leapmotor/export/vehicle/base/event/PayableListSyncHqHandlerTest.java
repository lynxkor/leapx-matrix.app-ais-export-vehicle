package com.leapmotor.export.vehicle.base.event;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 应付清单同步红旗事件处理器单元测试
 *
 * <p>聚焦同步结果回写：只调用 updateSyncHqResultById 写同步维度三列，
 * 不得使用查询快照全量回写（快照到回写之间跨越红旗接口网络调用，
 * 会把并发变更的单据状态、版本号等字段覆盖回旧值，导致抬头与明细不一致）。
 *
 * @author huangtao
 * @since 2026-09-11
 */
@DisplayName("应付清单同步红旗事件处理器单元测试")
@ExtendWith(MockitoExtension.class)
public class PayableListSyncHqHandlerTest {

    @Mock
    private HongqiGateway hongqiGateway;
    @Mock
    private PayableListGateway payableListGateway;
    @Mock
    private PayableListDetailGateway payableListDetailGateway;

    private PayableListSyncHqHandler handler;

    private static final String PAYABLE_LIST_NO = "YF202606000001";
    private static final String PAYABLE_LIST_ID = "PL-001";

    @BeforeEach
    public void setUp() {
        handler = new PayableListSyncHqHandler(hongqiGateway, payableListGateway, payableListDetailGateway);
    }

    @Test
    @DisplayName("处理成功 — 只回写同步结果三列，不用快照全量回写")
    public void testHandle_successWriteSyncResultOnly() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(buildPayableList());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Collections.emptyList());
        when(hongqiGateway.syncPayableListEp(any())).thenReturn(buildHongqiResult(200));

        assertDoesNotThrow(() -> handler.handle(buildTask()));

        verify(payableListGateway).updateSyncHqResultById(eq(PAYABLE_LIST_ID), any(LocalDateTime.class),
                eq("SUCCESS"), anyString());
        // 关键：不得走快照全量回写（会覆盖并发变更的单据状态/版本号）
        verify(payableListGateway, never()).updatePayableList(any());
    }

    @Test
    @DisplayName("处理失败 — 先落库FAIL再抛异常触发重试")
    public void testHandle_failWriteSyncResultBeforeThrow() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(buildPayableList());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Collections.emptyList());
        when(hongqiGateway.syncPayableListEp(any())).thenReturn(buildHongqiResult(500));

        assertThrows(BizException.class, () -> handler.handle(buildTask()));

        verify(payableListGateway).updateSyncHqResultById(eq(PAYABLE_LIST_ID), any(LocalDateTime.class),
                eq("FAIL"), anyString());
        verify(payableListGateway, never()).updatePayableList(any());
    }

    @Test
    @DisplayName("处理失败 — 接口返回null时报文为空仍落库FAIL")
    public void testHandle_nullResultWriteFail() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(buildPayableList());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Collections.emptyList());
        when(hongqiGateway.syncPayableListEp(any())).thenReturn(null);

        assertThrows(BizException.class, () -> handler.handle(buildTask()));

        verify(payableListGateway).updateSyncHqResultById(eq(PAYABLE_LIST_ID), any(LocalDateTime.class),
                eq("FAIL"), isNull());
    }

    @Test
    @DisplayName("结果回写 — 超长报文按2000字符上限截断")
    public void testHandle_messageTruncated() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(buildPayableList());
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Collections.emptyList());
        HongqiResult result = buildHongqiResult(200);
        result.setMessage("x".repeat(3000));
        when(hongqiGateway.syncPayableListEp(any())).thenReturn(result);

        handler.handle(buildTask());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(payableListGateway).updateSyncHqResultById(eq(PAYABLE_LIST_ID), any(LocalDateTime.class),
                eq("SUCCESS"), messageCaptor.capture());
        assertEquals(2000, messageCaptor.getValue().length(), "报文应截断至字段长度上限2000");
    }

    // ==================== 辅助方法 ====================

    private EventNotifyTask buildTask() {
        EventNotifyTask task = mock(EventNotifyTask.class);
        when(task.getBizNo()).thenReturn(PAYABLE_LIST_NO);
        return task;
    }

    private PayableListDo buildPayableList() {
        PayableListDo payableList = new PayableListDo();
        payableList.setId(PAYABLE_LIST_ID);
        payableList.setPayableListNo(PAYABLE_LIST_NO);
        return payableList;
    }

    @SuppressWarnings("rawtypes")
    private HongqiResult buildHongqiResult(int code) {
        HongqiResult result = new HongqiResult();
        result.setCode(code);
        result.setMessage("mock");
        return result;
    }
}
