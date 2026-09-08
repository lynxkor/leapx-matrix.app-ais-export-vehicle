package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.payablelist.dto.PayableListForSapDto;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 应付清单同步SAP事件处理器单元测试
 *
 * <p>覆盖：事件 payload -> SAP 契约请求（IV_SYSID/IM_TAB 行项目）映射；
 * ES_RET.RET_CODE = S 成功不重试、E/框架失败/null 抛异常触发重试。
 *
 * @author huangtao
 * @since 2026-09-04
 */
@DisplayName("应付清单同步SAP事件处理器单元测试")
@ExtendWith(MockitoExtension.class)
public class PayableListSyncSapHandlerTest {

    @Mock
    private SapGateway sapGateway;

    private PayableListSyncSapHandler handler;

    private static final String PAYABLE_LIST_NO = "YF202606000001";

    @BeforeEach
    public void setUp() {
        handler = new PayableListSyncSapHandler(sapGateway);
    }

    // ==================== 请求组装场景 ====================

    @Test
    @DisplayName("请求组装 — 事件payload应映射为SAP契约请求IV_SYSID+IM_TAB")
    public void testBuildRequest_contractMapping() {
        EventNotifyTask task = buildTask(buildPayload());

        PayableListSyncSapReq req = (PayableListSyncSapReq) handler.buildRequest(task);

        assertNotNull(req, "请求不应为空");
        assertEquals("DMS", req.getIvSysid(), "IV_SYSID 应默认 DMS");
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
    @DisplayName("请求组装 — 无明细时IM_TAB为空列表且不抛异常")
    public void testBuildRequest_emptyDetails() {
        PayableListForSapDto payload = buildPayload();
        payload.setDetails(null);
        EventNotifyTask task = buildTask(payload);

        PayableListSyncSapReq req = (PayableListSyncSapReq) handler.buildRequest(task);

        assertNotNull(req, "请求不应为空");
        assertEquals("DMS", req.getIvSysid(), "IV_SYSID 应默认 DMS");
        assertNotNull(req.getImTab(), "IM_TAB 应为空列表而非null");
        assertEquals(0, req.getImTab().size(), "无明细时 IM_TAB 应为空");
    }

    // ==================== 成功与失败场景 ====================

    @Test
    @DisplayName("处理成功 — RET_CODE=S 时正常完成不抛异常")
    public void testHandle_sapSuccess() {
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("S", "成功"));

        assertDoesNotThrow(() -> handler.handle(buildTask(buildPayload())));

        // 请求已按契约组装后透传网关
        ArgumentCaptor<PayableListSyncSapReq> reqCaptor = ArgumentCaptor.forClass(PayableListSyncSapReq.class);
        verify(sapGateway).syncPayableList(reqCaptor.capture());
        assertEquals("DMS", reqCaptor.getValue().getIvSysid(), "请求应携带默认系统标识");
        assertEquals(2, reqCaptor.getValue().getImTab().size(), "请求应携带全部明细行");
    }

    @Test
    @DisplayName("处理失败 — RET_CODE=E 时抛异常且提示只携带RET_REASON")
    public void testHandle_sapErrorRetCode() {
        // 框架级成功但业务级失败：SAP 返回 E 表示该清单未接收成功，必须重试
        when(sapGateway.syncPayableList(any())).thenReturn(buildSapResult("E", "无效的车辆编号LFZJ9X4K8PGH65270U"));

        BizException exception = assertThrows(BizException.class,
                () -> handler.handle(buildTask(buildPayload())));
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

        BizException exception = assertThrows(BizException.class,
                () -> handler.handle(buildTask(buildPayload())));
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

        BizException exception = assertThrows(BizException.class,
                () -> handler.handle(buildTask(buildPayload())));
        assertEquals("应付清单同步SAP失败", exception.getMessage(),
                "RET_REASON 为空串时应仅提示同步失败");
    }

    @Test
    @DisplayName("处理失败 — 框架级success=false 时应抛异常触发重试")
    public void testHandle_frameworkFail() {
        PayableListSyncSapResult result = buildSapResult("S", "成功");
        result.setSuccess(false);
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        assertThrows(BizException.class, () -> handler.handle(buildTask(buildPayload())));
    }

    @Test
    @DisplayName("处理失败 — 返回null时应抛异常触发重试")
    public void testHandle_nullResult() {
        when(sapGateway.syncPayableList(any())).thenReturn(null);

        assertThrows(BizException.class, () -> handler.handle(buildTask(buildPayload())));
    }

    @Test
    @DisplayName("处理失败 — body/ES_RET为空时应抛异常触发重试")
    public void testHandle_missingEsRet() {
        PayableListSyncSapResult result = new PayableListSyncSapResult();
        result.setSuccess(true);
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        assertThrows(BizException.class, () -> handler.handle(buildTask(buildPayload())));
    }

    @Test
    @DisplayName("处理失败 — 业务数据为空时应抛异常")
    public void testHandle_nullDataParam() {
        EventNotifyTask task = mock(EventNotifyTask.class);
        when(task.getDataParam()).thenReturn(null);

        BizException exception = assertThrows(BizException.class, () -> handler.handle(task));
        assertTrue(exception.getMessage().contains("业务数据为空"),
                "异常消息应包含 '业务数据为空'");
    }

    @Test
    @DisplayName("处理失败 — 总状态E时提示应取RET_TAB明细失败行RET_REASON")
    public void testHandle_sapErrorReasonOnDetailRows() {
        // 总状态报错，具体原因在明细行上：ES_RET 只有总状态，明细失败行携带逐车原因
        PayableListSyncSapResult result = buildSapResultWithRetTab("E", "存在错误明细",
                buildRetTabItem("E", "无效的车辆编号LFZJ9X4K8PGH65270U"),
                buildRetTabItem("S", "成功"));
        when(sapGateway.syncPayableList(any())).thenReturn(result);

        BizException exception = assertThrows(BizException.class,
                () -> handler.handle(buildTask(buildPayload())));
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

        BizException exception = assertThrows(BizException.class,
                () -> handler.handle(buildTask(buildPayload())));
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

        assertDoesNotThrow(() -> handler.handle(buildTask(buildPayload())));
    }

    // ==================== 辅助方法 ====================

    private EventNotifyTask buildTask(PayableListForSapDto payload) {
        EventNotifyTask task = mock(EventNotifyTask.class);
        when(task.getDataParam()).thenReturn(JSON.toJSONString(payload));
        return task;
    }

    private PayableListForSapDto buildPayload() {
        PayableListForSapDto dto = new PayableListForSapDto();
        dto.setPayableListNo(PAYABLE_LIST_NO);
        dto.setCustomerCode("CUST001");
        dto.setCustomerName("测试客户");
        dto.setTotalAmount(new BigDecimal("300000.00"));

        PayableListForSapDto.PayableListDetailForSapDto d1 = new PayableListForSapDto.PayableListDetailForSapDto();
        d1.setSoNo("SO0001");
        d1.setPoNo("PO0001");
        d1.setVin("LFZ_001");
        d1.setPrice(new BigDecimal("150000.00"));
        PayableListForSapDto.PayableListDetailForSapDto d2 = new PayableListForSapDto.PayableListDetailForSapDto();
        d2.setSoNo("SO0002");
        d2.setPoNo("PO0002");
        d2.setVin("LFZ_002");
        d2.setPrice(new BigDecimal("150000.00"));
        dto.setDetails(Arrays.asList(d1, d2));
        return dto;
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
