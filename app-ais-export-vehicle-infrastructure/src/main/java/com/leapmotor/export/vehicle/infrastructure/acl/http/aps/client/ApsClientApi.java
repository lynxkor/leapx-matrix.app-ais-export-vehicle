package com.leapmotor.export.vehicle.infrastructure.acl.http.aps.client;

import com.leapmotor.export.vehicle.acl.http.aps.req.ApsCancelReq;
import com.leapmotor.export.vehicle.acl.http.aps.req.ApsIssueReq;
import com.leapmotor.export.vehicle.acl.http.aps.resp.ApsResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * APS 产销平台 Feign 客户端，经统一出口网关 app-ais-xuanniao 调用，前缀 /external/aps。
 *
 * <p>评审要求最终走事件机制保证最终一致，当前先同步 Feign 占位，APS 契约到后改事件机制。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Component
@FeignClient(name = "app-ais-xuanniao", contextId = "ApsClientApi", path = "/external/aps")
public interface ApsClientApi {

    /**
     * SO 下发排产同步 APS
     */
    @PostMapping("/soIssueSync")
    ApsResult soIssueSync(@RequestBody ApsIssueReq req);

    /**
     * SO 作废同步 APS（poNo 与 soNo 至少传一个，changeType = -1）
     */
    @PostMapping("/soCancelSync")
    ApsResult soCancelSync(@RequestBody ApsCancelReq req);
}
