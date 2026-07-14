package com.leapmotor.export.vehicle.acl.http.hongqi;

import com.leapmotor.export.vehicle.acl.http.hongqi.fallback.HongqiClientFallback;
import com.leapmotor.export.vehicle.acl.http.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.http.hongqi.response.HongqiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 红旗系统对接 — 接口2：CBU订单审批结果（出站 PO→红旗，已确认 2026-07-14，目标联调 8/20）。
 * URL 待红旗提供，先用占位名 hongqi-system。降级返回 mock 成功，不阻塞事件消费链路。
 */
@Component
@FeignClient(name = "hongqi-system", contextId = "HongqiClient", path = "/external/hongqi",
        fallback = HongqiClientFallback.class)
public interface HongqiClient {

    /** 推送 CBU 审批结果到红旗 */
    @PostMapping("/cbu/approval/result")
    HongqiResult pushCbuApprovalResult(@RequestBody CbuApprovalResultReq request);
}
