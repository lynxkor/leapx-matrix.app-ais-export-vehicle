package com.leapmotor.export.vehicle.infrastructure.acl.rpc.aps.client;

import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsCancelReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsIssueReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.resp.ApsResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * APS 产销平台 Feign 客户端，经统一出口网关 app-ais-xuanniao 调用，前缀 /external/mom-cloud/aps。
 *
 * <p>出口网关剥离 /external/mom-cloud/aps 前缀后转发 APS 真实地址 /api/abroad/*。
 * 契约来源：《海外销售订单接口文档》。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Component
@FeignClient(name = "app-ais-xuanniao", contextId = "ApsClientApi", path = "/external/mom-cloud/aps")
public interface ApsClientApi {

    /**
     * SO 下发排产同步 APS：POST /api/abroad/batchOrder
     */
    @PostMapping("/api/abroad/batchOrder")
    ApsResult soIssueSync(@RequestBody ApsIssueReq req);

    /**
     * SO 作废同步 APS：POST /api/abroad/cancelOrder（vehPoName/vehCbuSoName，changeType=-1）
     */
    @PostMapping("/api/abroad/cancelOrder")
    ApsResult soCancelSync(@RequestBody ApsCancelReq req);
}
