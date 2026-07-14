package com.leapmotor.export.vehicle.acl.http.so;

import com.leapmotor.export.vehicle.acl.http.so.fallback.SoClientFallback;
import com.leapmotor.export.vehicle.acl.http.so.request.SoCreateReq;
import com.leapmotor.export.vehicle.acl.http.so.request.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.acl.http.so.response.SoResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * SO 订单服务（4.4 供需审核通过生成 SO 单 / 4.6 刷新价格联动）。契约待 SO 侧提供，先占位。
 * PO 侧只把 PO 参数作为入参调 SO 创建接口；SO 单号/行号/一车一单拆分由 SO 侧实现。
 * 降级：SO 服务不可用时返回 mock 成功，不阻塞冒烟测试。
 */
@Component
@FeignClient(name = "app-ais-so", contextId = "SoClient", path = "/app/so",
        fallback = SoClientFallback.class)
public interface SoClient {

    /** 创建 SO 单（一车一单，SO 侧拆分行号 1..N） */
    @PostMapping("/create")
    SoResult createSo(@RequestBody SoCreateReq req);

    /** 查 PO 下所有 SO 状态（4.5 驳回校验 / 4.6 刷新前校验） */
    @PostMapping("/queryStatus")
    SoResult queryStatus(@RequestBody String poNo);

    /** SO 作废（4.5 驳回，PO 下所有 SO → 已作废；已是已作废的不重复处理） */
    @PostMapping("/cancel")
    SoResult cancel(@RequestBody String poNo);

    /** 更新 SO 车辆价格/冻结金额（4.6 刷新价格联动，占位） */
    @PostMapping("/updatePrice")
    SoResult updatePrice(@RequestBody SoUpdatePriceReq req);
}
