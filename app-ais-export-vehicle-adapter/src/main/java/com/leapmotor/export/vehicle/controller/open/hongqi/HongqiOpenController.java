package com.leapmotor.export.vehicle.controller.open.hongqi;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 红旗系统开放接口（红旗 → PO，入站）。
 * 路径前缀 /openApi（外部系统对接，参照 maintain controller/open/{系统}），不走 @ResourcePermission（系统间调用）。
 * 鉴权/签名由上层网关统一处理，本接口不配签名拦截器。
 * TODO 红旗契约到后：Request/返回格式按真契约微调。
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "红旗系统开放接口", description = "红旗 → PO 入站接口")
@RestController
@AllArgsConstructor
@RequestMapping("/openApi")
public class HongqiOpenController {

    private ExportVehicleCbuPoService purchaseOrderService;

    /** 4.1 红旗CBU订单采购申请 → 创建草稿PO订单（主表+明细联保存） */
    @Operation(summary = "红旗CBU订单采购申请-创建草稿订单")
    @PostMapping("/hongqi/api/purchaseOrder/sync")
    public AjaxResult<String> createFromHongqi(@RequestBody @Valid HongqiCbuPurchaseOrderReq req) {
        return AjaxResult.success(purchaseOrderService.createFromHongqi(req));
    }
}
