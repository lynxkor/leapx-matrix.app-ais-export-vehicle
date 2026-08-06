package com.leapmotor.export.vehicle.infrastructure.acl.rpc.sap.gatewayimpl;

import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.SapResult;
import com.leapmotor.export.vehicle.infrastructure.acl.rpc.sap.client.SapClientApi;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SAP 领域网关实现：调用 Feign 客户端并处理响应，失败抛业务异常。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@AllArgsConstructor
@Service
@Slf4j
public class SapGatewayImpl implements SapGateway {

    private SapClientApi sapClientApi;

    @Override
    public SapResult syncSap(SapOrderSyncReq req) {
        try {
            log.info("SO订单同步SAP, request:{}", req);
            SapResult result = sapClientApi.orderSync(req);
            log.info("SO订单同步SAP, request:{}, response:{}", req, result);
            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("SO订单同步SAP异常：{}", e.getMessage());
            throw new BizException("SO订单同步SAP异常：" + e.getMessage());
        }
    }

    @Override
    public SapResult syncPayableList(PayableListSyncSapReq req) {
        try {
            log.info("应付清单同步SAP, request:{}", req);
            SapResult result = sapClientApi.syncPayableList(req);
            log.info("应付清单同步SAP, request:{}, response:{}", req, result);
            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("应付清单同步SAP异常：{}", e.getMessage());
            throw new BizException("应付清单同步SAP异常：" + e.getMessage());
        }
    }
}
