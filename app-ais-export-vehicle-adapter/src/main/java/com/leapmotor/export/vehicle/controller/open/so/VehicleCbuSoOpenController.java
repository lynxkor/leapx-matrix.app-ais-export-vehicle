package com.leapmotor.export.vehicle.controller.open.so;

import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CBU 整车销售订单主表开放接口（供 APS / 红旗回调）。
 * 签名校验由上层网关统一处理，本地不注册 SignUrlInterceptor。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@Tag(name = "CBU整车销售订单主表开放接口", description = "供APS/红旗等外部系统回调")
@RestController
@AllArgsConstructor
@RequestMapping("/openApi/so/vehicleCbuSo")
public class VehicleCbuSoOpenController {

    private VehicleCbuSoService vehicleCbuSoService;

    @Operation(summary = "接收 APS 生产状态回传")
    @PostMapping("/receiveProductionStatus")
    public AjaxResult<Void> receiveProductionStatus(@RequestBody VehicleCbuSoReceiveStatusReq req) {
        vehicleCbuSoService.receiveProductionStatus(req);
        return AjaxResult.success();
    }

    @Operation(summary = "接收红旗质检结果回传")
    @PostMapping("/receiveQcResult")
    public AjaxResult<Void> receiveQcResult(@RequestBody VehicleCbuSoReceiveQcReq req) {
        vehicleCbuSoService.receiveQcResult(req);
        return AjaxResult.success();
    }
}
