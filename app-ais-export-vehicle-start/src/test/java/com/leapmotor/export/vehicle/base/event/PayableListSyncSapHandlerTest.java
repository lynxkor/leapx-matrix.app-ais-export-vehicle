package com.leapmotor.export.vehicle.base.event;

import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 应付清单同步SAP事件处理器单元测试
 *
 * <p>覆盖：按 bizNo 回查清单+全量明细 -> SAP 契约请求（IV_SYSID/IM_TAB 行项目）映射；
 * ES_RET.RET_CODE = S 成功不重试、E/框架失败/null 抛异常触发重试；同步结果回写与报文截断。
 *
 * <p>事件载荷已从 data_param 改为消费时回查，因此本测试不再向 task 灌 payload，
 * 只灌 bizNo —— 明细一律来自 gateway 桩数据。
 *
 * @author huangtao
 * @since 2026-09-04
 */
@DisplayName("应付清单同步SAP事件处理器单元测试")
@ExtendWith(MockitoExtension.class)
public class PayableListSyncSapHandlerTest {

    @Mock
    private SapGateway sapGateway;
    @Mock
    private PayableListGateway payableListGateway;
    @Mock
    private PayableListDetailGateway payableListDetailGateway;

    private PayableListSyncSapHandler handler;

    private static final String PAYABLE_LIST_NO = "YF202606000001";
    private static final String PAYABLE_LIST_ID = "PL-001";

    /** SAP 契约默认系统标识：模型字段初始值为 AIS（字段注释写的是 DMS，与代码不一致，以代码为准） */
    private static final String EXPECTED_SYS_ID = "AIS";

    @BeforeEach
    public void setUp() {
        handler = new PayableListSyncSapHandler(sapGateway, payableListGateway, payableListDetailGateway);
        // 默认给出"清单存在且有两条明细"的回查结果；异常场景由用例各自覆盖
        lenient().when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(buildPayableList());
        lenient().when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID))
                .thenReturn(buildDetails());
    }

    // ==================== 载荷来源：不读 data_param ====================

    @Test
    @DisplayName("载荷不预构建 — buildRequest应返回null，明细不进 data_param")
    public void testBuildRequest_notPrebuilt() {
        // 不桩 bizNo：预构建阶段本就不该读事件的任何字段，读了就是又走回快照老路
        EventNotifyTask task = mock(EventNotifyTask.class);

        assertNull(handler.buildRequest(task), "事件载荷应为 null，避免全量明细写入定长 TEXT 列");
        // 发布环节不应触发任何清单/明细查询
        verifyNoInteractions(payableListGateway, payableListDetailGateway);
    }

    @Test
    @DisplayName("存量坏快照自愈 — data_param 是被截断的残缺JSON时仍按编号回查并推送成功")
    public void testHandle_damagedDataParamIgnoredAndRecovers() {
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("S", "成功"));
        EventNotifyTask task = mock(EventNotifyTask.class);
        when(task.getBizNo()).thenReturn(PAYABLE_LIST_NO);
        // 历史事件行里被 substring 截断出的非法 JSON：改造后完全不参与解析，不得影响推送
        // lenient：新实现不再读 data_param，此桩是"投毒"用，本就不该被调用
        lenient().when(task.getDataParam())
                .thenReturn("{\"customerCode\":\"67001529\",\"details\":[{\"poNo\":\"HQ2026");

        assertDoesNotThrow(() -> handler.handle(task));

        ArgumentCaptor<PayableListSyncSapReq> reqCaptor = ArgumentCaptor.forClass(PayableListSyncSapReq.class);
        verify(sapGateway).syncPayableList(reqCaptor.capture());
        assertEquals(2, reqCaptor.getValue().getImTab().size(), "应推送回查到的全部明细，不受坏快照影响");
    }

    // ==================== 请求组装场景 ====================

    @Test
    @DisplayName("请求组装 — 回查明细应映射为SAP契约请求IV_SYSID+IM_TAB")
    public void testAssembleFromDb_contractMapping() {
        PayableListSyncSapReq req = handler.assembleFromDb(buildPayableList());

        assertNotNull(req, "请求不应为空");
        assertEquals(EXPECTED_SYS_ID, req.getIvSysid(), "IV_SYSID 应取模型默认值");
        assertNotNull(req.getImTab(), "IM_TAB 不应为空");
        assertEquals(2, req.getImTab().size(), "IM_TAB 行数应与明细数一致");

        PayableListSyncSapReq.ImTabItem first = req.getImTab().get(0);
        assertEquals(PAYABLE_LIST_NO, first.getPaynr(), "PAYNR 应为应付清单编号");
        assertEquals("PO0001", first.getPorder(), "PORDER 应为红旗PO");
        assertEquals("SO0001", first.getSorder(), "SORDER 应为销服SO");
        assertEquals("LFZ_001", first.getVhvin(), "VHVIN 应为VIN码");

        // 同一清单的每条明细逐行携带相同 PAYNR
        PayableListSyncSapReq.ImTabItem second = req.getImTab().get(1);
        assertEquals(PAYABLE_LIST_NO, second.getPaynr(), "每条明细均应携带清单编号");
        assertEquals("PO0002", second.getPorder());
        assertEquals("SO0002", second.getSorder());
        assertEquals("LFZ_002", second.getVhvin());
    }

    @Test
    @DisplayName("完整性 — 明细行数等于抬头台数时全部逐行推送，不丢任何一行")
    public void testAssembleFromDb_allDetailsCarried() {
        List<PayableListDetailDo> details = new ArrayList<>();
        for (int i = 0; i < 600; i++) {
            details.add(buildDetail("SO-" + i, "PO-" + i, "VIN" + i));
        }
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(details);
        PayableListDo payableList = buildPayableList();
        payableList.setTotalVehicles(600);

        PayableListSyncSapReq req = handler.assembleFromDb(payableList);

        // 600 条明细在旧实现下会撑爆 data_param；回查模式下规模不再是约束
        assertEquals(600, req.getImTab().size(), "全量明细应一行不少地进入 IM_TAB");
        assertEquals("VIN599", req.getImTab().get(599).getVhvin(), "末行明细不应丢失");
    }

    @Test
    @DisplayName("完整性 — 无明细时抛错终止，不给SAP推空表")
    public void testAssembleFromDb_emptyDetailsThrows() {
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(Collections.emptyList());

        BizException exception = assertThrows(BizException.class,
                () -> handler.assembleFromDb(buildPayableList()));
        assertTrue(exception.getMessage().contains("应付清单无有效明细"),
                "异常消息应提示无有效明细，实际：" + exception.getMessage());
    }

    @Test
    @DisplayName("完整性 — 明细查询返回null时同样抛错而非推空表")
    public void testAssembleFromDb_nullDetailsThrows() {
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(null);

        assertThrows(BizException.class, () -> handler.assembleFromDb(buildPayableList()));
    }

    @Test
    @DisplayName("完整性 — 抬头台数与明细行数不一致时只告警不拦截（total_vehicles 可被人工编辑）")
    public void testAssembleFromDb_totalVehiclesMismatchStillAssembles() {
        PayableListDo payableList = buildPayableList();
        // total_vehicles 是可编辑字段（PayableListUpdateExe 取请求值回写），不能据此硬拦正常清单
        payableList.setTotalVehicles(99);

        PayableListSyncSapReq req = handler.assembleFromDb(payableList);

        assertEquals(2, req.getImTab().size(), "台数不一致时仍按实际明细推送，不因脏台数阻断");
    }

    @Test
    @DisplayName("完整性 — VIN为空的明细只告警，不连带拦下其余正常车辆")
    public void testAssembleFromDb_blankVinStillAssembled() {
        List<PayableListDetailDo> details = Arrays.asList(
                buildDetail("SO0001", "PO0001", "LFZ_001"),
                buildDetail("SO0002", "PO0002", "  "));
        when(payableListDetailGateway.listByPayableListId(PAYABLE_LIST_ID)).thenReturn(details);

        PayableListSyncSapReq req = handler.assembleFromDb(buildPayableList());

        assertEquals(2, req.getImTab().size(), "SAP 逐行返回 RET_TAB，本地不拦空VIN行");
        assertEquals("  ", req.getImTab().get(1).getVhvin(), "空VIN原样透传，由SAP判定并回传原因");
    }

    // ==================== 成功与失败场景 ====================

    @Test
    @DisplayName("处理成功 — RET_CODE=S 时正常完成不抛异常")
    public void testHandle_sapSuccess() {
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("S", "成功"));

        assertDoesNotThrow(() -> handler.handle(buildTask()));

        // 请求已按契约组装后透传网关
        ArgumentCaptor<PayableListSyncSapReq> reqCaptor = ArgumentCaptor.forClass(PayableListSyncSapReq.class);
        verify(sapGateway).syncPayableList(reqCaptor.capture());
        assertEquals(EXPECTED_SYS_ID, reqCaptor.getValue().getIvSysid(), "请求应携带默认系统标识");
        assertEquals(2, reqCaptor.getValue().getImTab().size(), "请求应携带全部明细行");
    }

    @Test
    @DisplayName("处理失败 — 应付清单不存在时应抛异常且不触碰SAP")
    public void testHandle_payableListNotExist() {
        when(payableListGateway.getPayableListByNo(PAYABLE_LIST_NO)).thenReturn(null);

        BizException exception = assertThrows(BizException.class, () -> handler.handle(buildTask()));
        assertTrue(exception.getMessage().contains("应付清单不存在"),
                "异常消息应包含 '应付清单不存在'，实际：" + exception.getMessage());
        // 定位不到业务对象时必须停在调用外部系统之前
        verifyNoInteractions(sapGateway);
    }

    @Test
    @DisplayName("处理失败 — RET_CODE=E 时抛异常且提示只携带RET_REASON")
    public void testHandle_sapErrorRetCode() {
        // 框架级成功但业务级失败：SAP 返回 E 表示该清单未接收成功，必须重试
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("E", "无效的车辆编号LFZJ9X4K8PGH65270U"));

        BizException exception = assertThrows(BizException.class, () -> handler.handle(buildTask()));
        assertTrue(exception.getMessage().contains("应付清单同步SAP失败"),
                "异常消息应包含 '应付清单同步SAP失败'");
        assertTrue(exception.getMessage().contains("无效的车辆编号LFZJ9X4K8PGH65270U"),
                "异常消息应携带 SAP 返回的 RET_REASON");
        assertFalse(exception.getMessage().contains("RET_CODE"),
                "异常消息不应再打印整段返回 JSON");
        assertFalse(exception.getMessage().contains("body"),
                "异常消息不应再打印整段返回 JSON");
    }

    @Test
    @DisplayName("处理失败 — RET_REASON为空时仅提示同步失败不打印JSON")
    public void testHandle_sapErrorNoReason() {
        // 业务级失败但 RET_REASON 为空：异常消息只提示同步失败，不拼接无信息量内容
        PayableListSyncSapResult result = buildSapResult("E", null);
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        BizException exception = assertThrows(BizException.class, () -> handler.handle(buildTask()));
        assertEquals("应付清单同步SAP失败", exception.getMessage(),
                "RET_REASON 为空时应仅提示同步失败");
        assertFalse(exception.getMessage().contains("RET_CODE"),
                "RET_REASON 为空时不应输出完整返回 JSON");
    }

    @Test
    @DisplayName("处理失败 — RET_REASON为空串时仅提示同步失败不打印JSON")
    public void testHandle_sapErrorBlankReason() {
        // RET_REASON 为空串 "" 同样视为无有效原因，仅提示同步失败
        PayableListSyncSapResult result = buildSapResult("E", "");
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        BizException exception = assertThrows(BizException.class, () -> handler.handle(buildTask()));
        assertEquals("应付清单同步SAP失败", exception.getMessage(),
                "RET_REASON 为空串时应仅提示同步失败");
    }

    @Test
    @DisplayName("处理失败 — 框架级success=false 时应抛异常触发重试")
    public void testHandle_frameworkFail() {
        PayableListSyncSapResult result = buildSapResult("S", "成功");
        result.setSuccess(false);
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        assertThrows(BizException.class, () -> handler.handle(buildTask()));
    }

    @Test
    @DisplayName("处理失败 — 返回null时应抛异常触发重试")
    public void testHandle_nullResult() {
        when(sapGateway.syncPayableList(any())).thenReturn(null);

        assertThrows(BizException.class, () -> handler.handle(buildTask()));
    }

    @Test
    @DisplayName("处理失败 — body/ES_RET为空时应抛异常触发重试")
    public void testHandle_missingEsRet() {
        PayableListSyncSapResult result = new PayableListSyncSapResult();
        result.setSuccess(true);
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        assertThrows(BizException.class, () -> handler.handle(buildTask()));
    }

    @Test
    @DisplayName("处理失败 — 总状态E时提示应取RET_TAB明细失败行RET_REASON")
    public void testHandle_sapErrorReasonOnDetailRows() {
        // 总状态报错，具体原因在明细行上：ES_RET 只有总状态，明细失败行携带逐车原因
        PayableListSyncSapResult result = buildSapResultWithRetTab("E", "存在错误明细",
                buildRetTabItem("E", "无效的车辆编号LFZJ9X4K8PGH65270U"),
                buildRetTabItem("S", "成功"));
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        BizException exception = assertThrows(BizException.class, () -> handler.handle(buildTask()));
        assertTrue(exception.getMessage().contains("无效的车辆编号LFZJ9X4K8PGH65270U"),
                "异常消息应携带明细失败行的 RET_REASON");
        assertFalse(exception.getMessage().contains("存在错误明细"),
                "有明细失败原因时不应再提示总状态文本");
        assertFalse(exception.getMessage().contains("成功"),
                "明细成功行原因不应出现在失败提示中");
    }

    @Test
    @DisplayName("处理失败 — 明细失败行无RET_REASON时应回退总状态RET_REASON")
    public void testHandle_sapErrorDetailBlankReasonFallbackEsRet() {
        // 明细失败行原因为空时回退总状态文本（兼容只有总状态原因的返回）
        PayableListSyncSapResult result = buildSapResultWithRetTab("E", "无效的车辆编号LFZJ9X4K8PGH65270U",
                buildRetTabItem("E", ""));
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        BizException exception = assertThrows(BizException.class, () -> handler.handle(buildTask()));
        assertEquals("应付清单同步SAP失败: 无效的车辆编号LFZJ9X4K8PGH65270U", exception.getMessage(),
                "明细无原因时应回退总状态 RET_REASON");
    }

    @Test
    @DisplayName("处理成功 — 总状态S时不解析明细，即使明细存在失败行也不抛异常")
    public void testHandle_sapSuccessIgnoreRetTabDetail() {
        // 总状态成功即整体成功，明细状态无需逐行判定
        PayableListSyncSapResult result = buildSapResultWithRetTab("S", "成功",
                buildRetTabItem("E", "无效的车辆编号LFZJ9X4K8PGH65270U"));
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        assertDoesNotThrow(() -> handler.handle(buildTask()));
    }

    // ==================== 同步结果回写场景 ====================

    @Test
    @DisplayName("结果回写 — 成功后落库SUCCESS与报文")
    public void testHandle_successWriteSyncResult() {
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("S", "成功"));

        handler.handle(buildTask());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(payableListGateway).updateSyncSapResultById(eq(PAYABLE_LIST_ID), any(LocalDateTime.class),
                eq("SUCCESS"), messageCaptor.capture());
        // 报文按 SAP 契约字段名序列化，总状态落在 ES_RET.RET_CODE
        assertTrue(messageCaptor.getValue().contains("RET_CODE"), "应落库SAP返回报文");
    }

    @Test
    @DisplayName("结果回写 — 失败时先落库FAIL再抛异常触发重试")
    public void testHandle_failWriteSyncResultBeforeThrow() {
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("E", "无效的车辆编号"));

        assertThrows(BizException.class, () -> handler.handle(buildTask()));

        verify(payableListGateway).updateSyncSapResultById(eq(PAYABLE_LIST_ID), any(LocalDateTime.class),
                eq("FAIL"), anyString());
    }

    @Test
    @DisplayName("结果回写 — 复用handle已查到的清单，不按编号二次查询")
    public void testHandle_noSecondQueryByNo() {
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("S", "成功"));

        handler.handle(buildTask());

        verify(payableListGateway, times(1)).getPayableListByNo(PAYABLE_LIST_NO);
    }

    @Test
    @DisplayName("结果回写 — 报文超出2000字符应截断留存，不得因超长导致回写失败")
    public void testHandle_longMessageTruncated() {
        // 大清单逐行返回失败原因时报文可达数十 KB，回写列虽为 TEXT 也不应放任无界写入
        List<PayableListSyncSapResult.RetTabItem> retTab = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            retTab.add(buildRetTabItem("E", "无效的车辆编号LFZJ9X4K8PGH00000" + i + "，请核对VIN与SAP主数据"));
        }
        PayableListSyncSapResult result = buildSapResult("S", "成功");
        result.getBody().setRetTab(retTab);
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        handler.handle(buildTask());

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(payableListGateway).updateSyncSapResultById(eq(PAYABLE_LIST_ID), any(LocalDateTime.class),
                eq("SUCCESS"), messageCaptor.capture());
        assertEquals(2000, messageCaptor.getValue().length(),
                "报文应被截断至2000字符上限（utf8mb4 下最坏8000字节，仍远低于 TEXT 的65535字节）");
    }

    @Test
    @DisplayName("结果回写 — 回写异常不应阻断同步主流程（避免已成功的推送被重复执行）")
    public void testHandle_writeBackExceptionDoesNotBreakFlow() {
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("S", "成功"));
        when(payableListGateway.updateSyncSapResultById(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("sync_sap_message 超长落库失败"));

        // 推送已成功，回写失败只应告警：抛出去会让 Job 重试并向 SAP 重复推送同一清单
        assertDoesNotThrow(() -> handler.handle(buildTask()));
    }

    // ==================== 辅助方法 ====================

    /**
     * 事件只携带业务编号：不再向 data_param 灌载荷
     */
    private EventNotifyTask buildTask() {
        EventNotifyTask task = mock(EventNotifyTask.class);
        when(task.getBizNo()).thenReturn(PAYABLE_LIST_NO);
        return task;
    }

    private PayableListDo buildPayableList() {
        PayableListDo payableList = new PayableListDo();
        payableList.setId(PAYABLE_LIST_ID);
        payableList.setPayableListNo(PAYABLE_LIST_NO);
        payableList.setCustomerCode("CUST001");
        payableList.setCustomerName("测试客户");
        payableList.setTotalVehicles(2);
        payableList.setTotalAmount(new BigDecimal("300000.00"));
        return payableList;
    }

    private List<PayableListDetailDo> buildDetails() {
        return Arrays.asList(
                buildDetail("SO0001", "PO0001", "LFZ_001"),
                buildDetail("SO0002", "PO0002", "LFZ_002"));
    }

    private PayableListDetailDo buildDetail(String soNo, String poNo, String vin) {
        PayableListDetailDo detail = new PayableListDetailDo();
        detail.setSoNo(soNo);
        detail.setPoNo(poNo);
        detail.setVin(vin);
        detail.setPrice(new BigDecimal("150000.00"));
        return detail;
    }

    /**
     * 构造带 RET_TAB 明细行的 SAP 返回（总状态 + 逐行明细状态）
     */
    private PayableListSyncSapResult buildSapResultWithRetTab(String esRetCode, String esRetReason,
            PayableListSyncSapResult.RetTabItem... retTabItems) {
        PayableListSyncSapResult result = buildSapResult(esRetCode, esRetReason);
        if (retTabItems.length > 0) {
            result.getBody().setRetTab(Arrays.asList(retTabItems));
        }
        return result;
    }

    private PayableListSyncSapResult.RetTabItem buildRetTabItem(String retCode, String retReason) {
        PayableListSyncSapResult.RetTabItem item = new PayableListSyncSapResult.RetTabItem();
        item.setRetCode(retCode);
        item.setRetReason(retReason);
        return item;
    }

    private PayableListSyncSapResult buildSapResult(String retCode, String retReason) {
        PayableListSyncSapResult result = new PayableListSyncSapResult();
        result.setSuccess(true);
        result.setCode("0");
        result.setMessage("调用成功");
        PayableListSyncSapResult.EsRet ret = new PayableListSyncSapResult.EsRet();
        ret.setRetCode(retCode);
        ret.setRetReason(retReason);
        PayableListSyncSapResult.Body body = new PayableListSyncSapResult.Body();
        body.setEsRet(ret);
        result.setBody(body);
        return result;
    }
}
