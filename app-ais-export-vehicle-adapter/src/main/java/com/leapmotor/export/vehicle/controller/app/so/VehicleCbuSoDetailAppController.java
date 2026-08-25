package com.leapmotor.export.vehicle.controller.app.so;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoDetailService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * SO车辆明细表 移动端接口控制器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "移动端 SO车辆明细表管理", description = "SO车辆明细表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/app/so/vehicleCbuSoDetail")
public class VehicleCbuSoDetailAppController {

    private VehicleCbuSoDetailService vehicleCbuSoDetailService;

    @Operation(summary = "SO车辆明细表分页查询")
    @ResourcePermission("app:so:vehicleCbuSoDetail:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<VehicleCbuSoDetailResp>> page(@RequestBody VehicleCbuSoDetailParam vehicleCbuSoDetailParam) {
        return AjaxResult.success(vehicleCbuSoDetailService.pageVehicleCbuSoDetail(vehicleCbuSoDetailParam));
    }

    @Operation(summary = "SO车辆明细表详情信息")
    @ResourcePermission("app:so:vehicleCbuSoDetail:get")
    @PostMapping(value = "/get")
    public AjaxResult<VehicleCbuSoDetailResp> getVehicleCbuSoDetailById(@RequestBody LpBaseParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            return AjaxResult.fail("参数ID不能为空");
        }
        return AjaxResult.success(vehicleCbuSoDetailService.getVehicleCbuSoDetailById(param.getId()));
    }

}
