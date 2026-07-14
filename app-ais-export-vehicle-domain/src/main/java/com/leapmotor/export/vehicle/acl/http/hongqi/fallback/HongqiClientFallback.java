package com.leapmotor.export.vehicle.acl.http.hongqi.fallback;

import com.leapmotor.export.vehicle.acl.http.hongqi.HongqiClient;
import com.leapmotor.export.vehicle.acl.http.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.http.hongqi.response.HongqiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 红旗出站降级（占位，红旗出站 URL 待定，冒烟测试用）。
 * 返回 mock 成功，事件机制落库后消费端不因调不通报错。
 *
 * @author leapmotor
 * @since 2026-07-14
 */
@Slf4j
@Component
public class HongqiClientFallback implements HongqiClient {

    @Override
    public HongqiResult pushCbuApprovalResult(CbuApprovalResultReq request) {
        log.warn("红旗出站不可用(占位降级) pushCbuApprovalResult poNo={}", request != null ? request.getPoNo() : null);
        HongqiResult r = new HongqiResult();
        r.setCode(200);
        r.setMessage("mock success - Hongqi placeholder");
        return r;
    }
}
