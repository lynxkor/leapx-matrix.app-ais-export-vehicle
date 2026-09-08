package com.leapmotor.export.vehicle.acl.rpc.sap.gateway;

import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.SapResult;

/**
 * SAP 领域网关（domain 定义接口，infrastructure 提供 Feign 实现）。
 *
 * <p>用于 SO 订单同步 SAP、应付清单同步 SAP。服务名/契约待 SAP 侧提供，先占位。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
public interface SapGateway {

    /**
     * SO 订单同步 SAP。
     *
     * @param req SAP 同步请求
     * @return SAP 返回
     */
    SapResult syncSap(SapOrderSyncReq req);

    /**
     * 应付清单同步 SAP。
     *
     * @param req 应付清单同步请求
     * @return SAP 返回（应付清单专用：ES_RET 总状态 + RET_TAB 明细状态）
     */
    PayableListSyncSapResult syncPayableList(PayableListSyncSapReq req);
}
