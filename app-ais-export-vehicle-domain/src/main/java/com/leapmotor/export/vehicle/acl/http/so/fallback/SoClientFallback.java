package com.leapmotor.export.vehicle.acl.http.so.fallback;

import com.leapmotor.export.vehicle.acl.http.so.SoClient;
import com.leapmotor.export.vehicle.acl.http.so.request.SoCreateReq;
import com.leapmotor.export.vehicle.acl.http.so.response.SoResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SO 服务降级（占位，SO 接口契约未定，冒烟测试用）。
 * 返回 mock 成功，不阻塞审批主流程。
 *
 * @author leapmotor
 * @since 2026-07-14
 */
@Slf4j
@Component
public class SoClientFallback implements SoClient {

    @Override
    public SoResult createSo(SoCreateReq req) {
        log.warn("SO服务不可用(占位降级) createSo poNo={}", req != null ? req.getPoNo() : null);
        SoResult r = new SoResult();
        r.setCode(200);
        r.setMessage("mock success - SO placeholder");
        return r;
    }

    @Override
    public SoResult queryStatus(String poNo) {
        log.warn("SO服务不可用(占位降级) queryStatus poNo={}", poNo);
        SoResult r = new SoResult();
        r.setCode(200);
        r.setMessage("mock success - SO placeholder");
        return r;
    }

    @Override
    public SoResult cancel(String poNo) {
        log.warn("SO服务不可用(占位降级) cancel poNo={}", poNo);
        SoResult r = new SoResult();
        r.setCode(200);
        r.setMessage("mock success - SO placeholder");
        return r;
    }
}
