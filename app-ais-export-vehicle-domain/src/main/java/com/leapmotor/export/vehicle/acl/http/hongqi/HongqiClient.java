package com.leapmotor.export.vehicle.acl.http.hongqi;

import com.leapmotor.export.vehicle.acl.http.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.http.hongqi.response.HongqiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 红旗系统对接（CBU 审批结果回传）。服务名/契约待红旗提供，先占位。
 */
@Component
@FeignClient(name = "hongqi-system", contextId = "HongqiClient", path = "/external/hongqi")
public interface HongqiClient {

    /** 推送 CBU 审批结果到红旗 */
    @PostMapping("/cbu/approval/result")
    HongqiResult pushCbuApprovalResult(@RequestBody CbuApprovalResultReq request);
}
