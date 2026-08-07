package com.leapmotor.export.vehicle.infrastructure.acl.rpc.sap.client;

import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.SapResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SAP 同步 Feign 客户端，经统一出口网关 app-ais-xuanniao 调用，前缀 /external/sap。服务名/契约待 SAP 侧提供，先占位。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@Component
@FeignClient(name = "app-ais-xuanniao", contextId = "SapClientApi", path = "/external/mdmapi")
public interface SapClientApi {

    /**
     * SO 订单同步 SAP
     */
    @PostMapping("/rest/sap/v4/orderSync")
    SapResult orderSync(@RequestBody SapOrderSyncReq req);

    /**
     * 应付清单同步 SAP（契约待 SAP 侧提供，先占位）
     */
    @PostMapping("/payableList/sync")
    SapResult syncPayableList(@RequestBody PayableListSyncSapReq req);
}
