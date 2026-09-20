package com.leapmotor.export.vehicle.controller.app.so;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * CBU整车销售订单主表 移动端接口控制器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "移动端 CBU整车销售订单主表管理", description = "CBU整车销售订单主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/app/so/vehicleCbuSo")
public class VehicleCbuSoAppController {

    private VehicleCbuSoService vehicleCbuSoService;

    @Operation(summary = "CBU整车销售订单主表分页查询")
    @ResourcePermission("app:so:vehicleCbuSo:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<VehicleCbuSoResp>> page(@RequestBody VehicleCbuSoParam vehicleCbuSoParam) {
        return AjaxResult.success(vehicleCbuSoService.pageVehicleCbuSo(vehicleCbuSoParam));
    }

    @Operation(summary = "CBU整车销售订单主表详情信息")
    @ResourcePermission("app:so:vehicleCbuSo:get")
    @PostMapping(value = "/get")
    public AjaxResult<VehicleCbuSoResp> getVehicleCbuSoById(@RequestBody LpBaseParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            return AjaxResult.fail("参数ID不能为空");
        }
        return AjaxResult.success(vehicleCbuSoService.getVehicleCbuSoById(param.getId()));
    }

}
