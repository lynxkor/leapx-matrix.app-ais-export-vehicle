package com.leapmotor.export.vehicle.infrastructure.acl.rpc.sap.gatewayimpl;

import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.infrastructure.acl.rpc.sap.client.SapClientApi;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SAP 领域网关实现单元测试（应付清单同步）
 *
 * <p>覆盖 {@link SapGatewayImpl#syncPayableList}：请求透传、正常返回、
 * BizException 原样传播、其他异常包装为 BizException、null 原样返回。
 *
 * @author huangtao
 * @since 2026-09-08
 */
@DisplayName("SapGatewayImpl 应付清单同步单元测试")
@ExtendWith(MockitoExtension.class)
public class SapGatewayImplTest {

    @Mock
    private SapClientApi sapClientApi;

    private SapGatewayImpl sapGatewayImpl;

    @BeforeEach
    public void setUp() {
        sapGatewayImpl = new SapGatewayImpl(sapClientApi);
    }

    @Test
    @DisplayName("同步成功 — 请求原样透传Feign并返回SAP结果")
    public void testSyncPayableList_success() {
        PayableListSyncSapReq req = buildReq();
        PayableListSyncSapResult expected = buildSapResult();
        when(sapClientApi.syncPayableList(any())).thenReturn(expected);

        PayableListSyncSapResult result = sapGatewayImpl.syncPayableList(req);

        assertSame(expected, result, "应原样返回SAP调用结果");
        // 请求对象应原样透传给 Feign 客户端（同一实例，不做二次组装）
        ArgumentCaptor<PayableListSyncSapReq> reqCaptor = ArgumentCaptor.forClass(PayableListSyncSapReq.class);
        verify(sapClientApi).syncPayableList(reqCaptor.capture());
        assertSame(req, reqCaptor.getValue(), "Feign调用应使用调用方传入的同一请求对象");
    }

    @Test
    @DisplayName("返回null — 网关层原样返回不抛异常（成功判定由上层Handler负责）")
    public void testSyncPayableList_nullResult() {
        when(sapClientApi.syncPayableList(any())).thenReturn(null);

        PayableListSyncSapResult result = sapGatewayImpl.syncPayableList(buildReq());

        assertNull(result, "null结果应原样透传");
        verify(sapClientApi).syncPayableList(any());
    }

    @Test
    @DisplayName("Feign抛BizException — 应原样传播不做包装")
    public void testSyncPayableList_bizExceptionPropagated() {
        BizException original = new BizException("SAP返回业务错误");
        when(sapClientApi.syncPayableList(any())).thenThrow(original);

        BizException exception = assertThrows(BizException.class,
                () -> sapGatewayImpl.syncPayableList(buildReq()));
        assertSame(original, exception, "BizException 应原样传播，不重新包装");
    }

    @Test
    @DisplayName("Feign抛其他异常 — 应包装为BizException并携带原异常消息")
    public void testSyncPayableList_otherExceptionWrapped() {
        when(sapClientApi.syncPayableList(any()))
                .thenThrow(new RuntimeException("connection refused"));

        BizException exception = assertThrows(BizException.class,
                () -> sapGatewayImpl.syncPayableList(buildReq()));
        assertTrue(exception.getMessage().contains("应付清单同步SAP异常"),
                "异常消息应包含 '应付清单同步SAP异常'");
        assertTrue(exception.getMessage().contains("connection refused"),
                "异常消息应携带原始异常信息");
    }

    // ==================== 辅助方法 ====================

    private PayableListSyncSapReq buildReq() {
        PayableListSyncSapReq req = new PayableListSyncSapReq();
        PayableListSyncSapReq.ImTabItem item = new PayableListSyncSapReq.ImTabItem();
        item.setPaynr("YF202606000001");
        item.setPorder("PO0001");
        item.setSorder("SO0001");
        item.setVhvin("LFZ_001");
        req.setImTab(java.util.Collections.singletonList(item));
        return req;
    }

    private PayableListSyncSapResult buildSapResult() {
        PayableListSyncSapResult result = new PayableListSyncSapResult();
        result.setSuccess(true);
        result.setCode("0");
        result.setMessage("调用成功");
        PayableListSyncSapResult.EsRet ret = new PayableListSyncSapResult.EsRet();
        ret.setRetCode("S");
        ret.setRetReason("成功");
        PayableListSyncSapResult.Body body = new PayableListSyncSapResult.Body();
        body.setEsRet(ret);
        result.setBody(body);
        return result;
    }
}
