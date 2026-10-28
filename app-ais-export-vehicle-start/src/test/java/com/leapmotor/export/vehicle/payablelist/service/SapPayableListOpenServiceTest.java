package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.PayableListSyncSapHandler;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.enums.SyncResultEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 应付清单手动补推SAP服务单元测试
 *
 * <p>补推已改为"重发事件"而非直连 SAP：本服务不再负责组装明细与回写结果（那些在
 * PayableListSyncSapHandler 里），因此本测试聚焦补推入口独有的三道守卫与"事件按编号发布"：
 * 编号非空、清单已确认、已同步成功需 force 才允许重推。
 *
 * <p>防重守卫必须由本服务承担：事件框架的 repeatableCheck 只防同一事件行被并发重复消费，
 * 不防同一清单被重复发布，连点两次补推就是两次真推送。
 *
 * @author huangtao
 * @since 2026-10-28
 */
@DisplayName("应付清单手动补推SAP服务单元测试")
@ExtendWith(MockitoExtension.class)
public class SapPayableListOpenServiceTest {

    @Mock
    private PayableListGateway payableListGateway;
    @Mock
    private EventHandlerHelper eventHandlerHelper;
    @Mock
    private SapGateway sapGateway;
    @Mock
    private PayableListSyncSapHandler payableListSyncSapHandler;

    private SapPayableListOpenService service;

    private static final String PAYABLE_LIST_NO = "YF202606000001";
    private static final String PAYABLE_LIST_ID = "PL-001";

    @BeforeEach
    public void setUp() {
        service = new SapPayableListOpenService(
                payableListGateway, eventHandlerHelper, sapGateway, payableListSyncSapHandler);
    }

    // ==================== 入参与存在性 ====================

    @Test
    @DisplayName("编号为空 — 应抛异常且不查库不发布事件")
    public void testSyncPayableListByNo_blankNo() {
        BizException exception = assertThrows(BizException.class,
                () -> service.syncPayableListByNo("  ", false));

        assertTrue(exception.getMessage().contains("应付清单编号不能为空"),
                "异常消息应明确提示清单编号缺失，实际：" + exception.getMessage());
        verifyNoInteractions(payableListGateway, eventHandlerHelper);
    }

    @Test
    @DisplayName("清单不存在 — 应抛异常且不发布事件")
    public void testSyncPayableListByNo_listNotExist() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(null);

        BizException exception = assertThrows(BizException.class,
                () -> service.syncPayableListByNo(PAYABLE_LIST_NO, false));
        assertTrue(exception.getMessage().contains("应付清单不存在"),
                "异常消息应包含 '应付清单不存在'，实际：" + exception.getMessage());
        verifyNoInteractions(eventHandlerHelper);
    }

    // ==================== 守卫1：单据状态 ====================

    @Test
    @DisplayName("守卫1 状态 — 草稿清单补推应被拦截（消费侧不校验状态，人工口子必须拦）")
    public void testSyncPayableListByNo_draftStatusRejected() {
        stubPayableList(PayableListStatusEnum.DRAFT.getCode(), null);

        BizException exception = assertThrows(BizException.class,
                () -> service.syncPayableListByNo(PAYABLE_LIST_NO, false));
        assertTrue(exception.getMessage().contains("非已确认状态"),
                "异常消息应提示状态不符，实际：" + exception.getMessage());
        assertTrue(exception.getMessage().contains(PayableListStatusEnum.DRAFT.getCode()),
                "异常消息应回显当前状态码便于定位，实际：" + exception.getMessage());
        verifyNoInteractions(eventHandlerHelper);
    }

    @Test
    @DisplayName("守卫1 状态 — force 也不放开状态校验（只放开重复推送的限制）")
    public void testSyncPayableListByNo_forceDoesNotBypassStatus() {
        stubPayableList(PayableListStatusEnum.REJECTED.getCode(), null);

        assertThrows(BizException.class, () -> service.syncPayableListByNo(PAYABLE_LIST_NO, true));
        verifyNoInteractions(eventHandlerHelper);
    }

    // ==================== 守卫2：防重复推送 ====================

    @Test
    @DisplayName("守卫2 防重 — 已同步成功且未 force 应拦截，避免向SAP重复推送同一清单")
    public void testSyncPayableListByNo_alreadySuccessRejected() {
        stubPayableList(PayableListStatusEnum.CONFIRMED.getCode(), SyncResultEnum.SUCCESS.getCode());

        BizException exception = assertThrows(BizException.class,
                () -> service.syncPayableListByNo(PAYABLE_LIST_NO, false));
        assertTrue(exception.getMessage().contains("已同步SAP成功"),
                "异常消息应提示已成功，实际：" + exception.getMessage());
        assertTrue(exception.getMessage().contains("force=true"),
                "异常消息应给出 force=true 的解法，实际：" + exception.getMessage());
        verifyNoInteractions(eventHandlerHelper);
    }

    @Test
    @DisplayName("守卫2 防重 — force=true 时已成功的清单允许重推（对账场景需人工重发）")
    public void testSyncPayableListByNo_forceAllowsSuccess() {
        stubPayableList(PayableListStatusEnum.CONFIRMED.getCode(), SyncResultEnum.SUCCESS.getCode());

        assertDoesNotThrow(() -> service.syncPayableListByNo(PAYABLE_LIST_NO, true));

        verifyPublishedWithNullPayload();
    }

    @Test
    @DisplayName("守卫2 防重 — 上次推送失败(FAIL)必须放行，否则失败清单被永久挡在门外")
    public void testSyncPayableListByNo_failResultAllowed() {
        stubPayableList(PayableListStatusEnum.CONFIRMED.getCode(), SyncResultEnum.FAIL.getCode());

        assertDoesNotThrow(() -> service.syncPayableListByNo(PAYABLE_LIST_NO, false));

        verifyPublishedWithNullPayload();
    }

    // ==================== 正常发布 ====================

    @Test
    @DisplayName("首次补推 — 按编号发布事件且载荷为null，明细交由消费端回查")
    public void testSyncPayableListByNo_neverSyncedPublishesNullPayload() {
        // 从未同步：sync_sap_result 为 null，不得被防重守卫误拦
        stubPayableList(PayableListStatusEnum.CONFIRMED.getCode(), null);

        String tip = service.syncPayableListByNo(PAYABLE_LIST_NO, false);

        verifyPublishedWithNullPayload();

        // 异步链路拿不到 SAP 返回，提示语必须自带结果查询路径
        assertTrue(tip.contains(PAYABLE_LIST_NO), "提示语应回显清单号，实际：" + tip);
        assertTrue(tip.contains("sync_sap_result"), "提示语应指引结果查询位置，实际：" + tip);
        assertTrue(tip.contains("event_notify_task"), "提示语应指引事件留痕查询位置，实际：" + tip);
    }

    @Test
    @DisplayName("补推不直连SAP — 不再自行组装明细，也不自行回写同步结果")
    public void testSyncPayableListByNo_doesNotCallSapOrWriteBack() {
        stubPayableList(PayableListStatusEnum.CONFIRMED.getCode(), null);

        service.syncPayableListByNo(PAYABLE_LIST_NO, false);

        // 组装与回写只属于 PayableListSyncSapHandler，补推侧若还留一套就会与主链路漂移
        verify(payableListGateway, never()).updateSyncSapResultById(any(), any(), any(), any());
    }

    // ==================== 直连补推（同步返回 SAP 结果） ====================

    @Test
    @DisplayName("直连补推 — 复用 handler 组装与回写，不发布事件")
    public void testSyncPayableListDirect_callsGatewayAndWritesBackViaHandler() {
        stubPayableList(PayableListStatusEnum.CONFIRMED.getCode(), null);

        PayableListSyncSapReq req = new PayableListSyncSapReq();
        req.setImTab(List.of(new PayableListSyncSapReq.ImTabItem()));
        PayableListSyncSapResult sapResult = new PayableListSyncSapResult();

        when(payableListSyncSapHandler.assembleFromDb(any(PayableListDo.class))).thenReturn(req);
        when(sapGateway.syncPayableList(req)).thenReturn(sapResult);

        PayableListSyncSapResult returned = service.syncPayableListDirect(PAYABLE_LIST_NO, false);

        // 返回的就是 SAP 原始应答，调用方无需再查表
        assertSame(sapResult, returned, "直连接口应原路返回 SAP 结果");

        // 组包与回写均必须走 handler：自己拼一套就会与事件链路漂移
        verify(payableListSyncSapHandler).assembleFromDb(any(PayableListDo.class));
        verify(payableListSyncSapHandler).writeBackSyncResult(any(PayableListDo.class), eq(sapResult));
        // 直连不落事件表，data_param 不存在超长风险
        verifyNoInteractions(eventHandlerHelper);
    }

    @Test
    @DisplayName("直连补推 — 守卫与事件入口一致：非已确认状态直接拒，不碰 SAP")
    public void testSyncPayableListDirect_statusGuardSameAsEventEntry() {
        stubPayableList(PayableListStatusEnum.PARTIAL_RECEIVED.getCode(), null);

        BizException exception = assertThrows(BizException.class,
                () -> service.syncPayableListDirect(PAYABLE_LIST_NO, false));
        assertTrue(exception.getMessage().contains("非已确认状态"),
                "异常消息应提示状态不符，实际：" + exception.getMessage());
        verifyNoInteractions(sapGateway, payableListSyncSapHandler);
    }

    @Test
    @DisplayName("直连补推 — 已同步成功且未 force 应拦截，force 时放行")
    public void testSyncPayableListDirect_duplicateGuard() {
        stubPayableList(PayableListStatusEnum.CONFIRMED.getCode(), SyncResultEnum.SUCCESS.getCode());

        assertThrows(BizException.class, () -> service.syncPayableListDirect(PAYABLE_LIST_NO, false));
        verifyNoInteractions(sapGateway);

        // force 重推：向 SAP 重复推同一清单必须显式表态
        PayableListSyncSapReq req = new PayableListSyncSapReq();
        req.setImTab(List.of(new PayableListSyncSapReq.ImTabItem()));
        when(payableListSyncSapHandler.assembleFromDb(any(PayableListDo.class))).thenReturn(req);
        when(sapGateway.syncPayableList(req)).thenReturn(new PayableListSyncSapResult());

        assertDoesNotThrow(() -> service.syncPayableListDirect(PAYABLE_LIST_NO, true));
        verify(sapGateway).syncPayableList(req);
    }

    // ==================== 辅助方法 ====================

    /**
     * 校验按 Object 重载发布了事件且载荷为 null。
     *
     * <p>不能用 {@code any(Object.class)}：Mockito 2+ 的该类匹配器<b>排除 null</b>，
     * 而生产代码传的就是 (Object) null，会报 Argument(s) are different。
     * 也不能用 {@code isNull()}：无类型线索时重载解析会绑到 {@code String relatedNo} 重载，校验形同虚设。
     */
    private void verifyPublishedWithNullPayload() {
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventHandlerHelper).publishAsync(
                eq(ThirdTargetPlatformEnum.SAP),
                eq(SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT),
                eq(PAYABLE_LIST_NO),
                payloadCaptor.capture());
        assertNull(payloadCaptor.getValue(),
                "补推与确认/页面推送同口径：只传编号，明细不进 data_param");
    }

    private void stubPayableList(String billStatus, String syncSapResult) {
        PayableListDo payableList = new PayableListDo();
        payableList.setId(PAYABLE_LIST_ID);
        payableList.setPayableListNo(PAYABLE_LIST_NO);
        payableList.setBillStatus(billStatus);
        payableList.setCustomerCode("CUST001");
        payableList.setTotalVehicles(2);
        payableList.setTotalAmount(new BigDecimal("300000.00"));
        payableList.setSyncSapResult(syncSapResult);
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(payableList);
    }
}
