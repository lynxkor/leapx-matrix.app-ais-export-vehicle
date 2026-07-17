package com.leapmotor.export.vehicle.acl.http.sap.gateway;

import com.leapmotor.export.vehicle.acl.http.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.acl.http.sap.resp.SapResult;

/**
 * SAP 领域网关（domain 定义接口，infrastructure 提供 Feign 实现）。
 *
 * <p>用于 SO 订单同步 SAP。服务名/契约待 SAP 侧提供，先占位。
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
}
