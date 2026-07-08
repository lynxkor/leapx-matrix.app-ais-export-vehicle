package com.leapmotor.export.vehicle.acl.http.so;

import com.leapmotor.export.vehicle.acl.http.so.request.SoCreateReq;
import com.leapmotor.export.vehicle.acl.http.so.response.SoResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SO 订单服务（4.4 供需审核通过生成 SO 单）。契约待 SO 侧提供，先占位。
 * PO 侧只把 PO 参数作为入参调 SO 创建接口；SO 单号/行号/一车一单拆分由 SO 侧实现。
 */
@Component
@FeignClient(name = "app-ais-so", contextId = "SoClient", path = "/app/so")
public interface SoClient {

    /** 创建 SO 单（一车一单，SO 侧拆分行号 1..N） */
    @PostMapping("/create")
    SoResult createSo(@RequestBody SoCreateReq req);
}
