package com.leapmotor.export.vehicle.infrastructure.acl.http.sap.client;

import com.leapmotor.export.vehicle.acl.http.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.acl.http.sap.resp.SapResult;
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
@FeignClient(name = "app-ais-xuanniao", contextId = "SapClientApi", path = "/external/sap")
public interface SapClientApi {

    /**
     * SO 订单同步 SAP
     */
    @PostMapping("/orderSync")
    SapResult orderSync(@RequestBody SapOrderSyncReq req);
}
