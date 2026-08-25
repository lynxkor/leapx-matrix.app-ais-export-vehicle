package com.leapmotor.export.vehicle.controller.app.so;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoInterfaceSyncService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * SO接口同步记录表 移动端接口控制器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "移动端 SO接口同步记录表管理", description = "SO接口同步记录表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/app/so/vehicleCbuSoInterfaceSync")
public class VehicleCbuSoInterfaceSyncAppController {

    private VehicleCbuSoInterfaceSyncService vehicleCbuSoInterfaceSyncService;

    @Operation(summary = "SO接口同步记录表分页查询")
    @ResourcePermission("app:so:vehicleCbuSoInterfaceSync:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<VehicleCbuSoInterfaceSyncResp>> page(@RequestBody VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam) {
        return AjaxResult.success(vehicleCbuSoInterfaceSyncService.pageVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncParam));
    }

    @Operation(summary = "SO接口同步记录表详情信息")
    @ResourcePermission("app:so:vehicleCbuSoInterfaceSync:get")
    @PostMapping(value = "/get")
    public AjaxResult<VehicleCbuSoInterfaceSyncResp> getVehicleCbuSoInterfaceSyncById(@RequestBody LpBaseParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            return AjaxResult.fail("参数ID不能为空");
        }
        return AjaxResult.success(vehicleCbuSoInterfaceSyncService.getVehicleCbuSoInterfaceSyncById(param.getId()));
    }

}
