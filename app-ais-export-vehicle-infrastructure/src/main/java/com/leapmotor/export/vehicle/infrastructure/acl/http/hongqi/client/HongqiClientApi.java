package com.leapmotor.export.vehicle.infrastructure.acl.http.hongqi.client;

import com.leapmotor.export.vehicle.acl.http.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.http.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.http.hongqi.req.HongqiProductionNodeNotifyReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 红旗系统 Feign 客户端，经统一出口网关 app-ais-xuanniao 调用，前缀 /external/hongqi。服务名/契约待红旗提供，先占位。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@Component
@FeignClient(name = "app-ais-xuanniao", contextId = "HongqiClientApi", path = "/external/hongqi")
public interface HongqiClientApi {

    /**
     * 推送 CBU 审批结果到红旗
     */
    @PostMapping("/cbu/approval/result")
    HongqiResult pushCbuApprovalResult(@RequestBody CbuApprovalResultReq request);

    /**
     * 车辆生产信息同步红旗（VDO53003，契约待红旗提供，先占位）
     */
    @PostMapping("/cbu/production/node/sync")
    HongqiResult pushProductionNodeSync(@RequestBody java.util.List<HongqiProductionNodeNotifyReq> request);
}
