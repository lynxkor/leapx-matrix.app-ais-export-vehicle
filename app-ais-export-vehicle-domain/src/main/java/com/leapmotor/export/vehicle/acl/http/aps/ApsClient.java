package com.leapmotor.export.vehicle.acl.http.aps;

import com.leapmotor.export.vehicle.acl.http.aps.response.ApsResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * APS 系统（4.5 产销平衡驳回，存在排产中 SO 时同步作废）。契约待 APS 侧，先占位。
 * 评审要求事件机制最终一致，当前先同步 Feign 占位，APS 契约到后改事件机制。
 */
@Component
@FeignClient(name = "app-ais-aps", contextId = "ApsClient", path = "/app/aps")
public interface ApsClient {

    /** SO 订单作废同步 APS（存在排产中 SO 时调） */
    @PostMapping("/soCancelSync")
    ApsResult soCancelSync(@RequestBody String poNo);
}
