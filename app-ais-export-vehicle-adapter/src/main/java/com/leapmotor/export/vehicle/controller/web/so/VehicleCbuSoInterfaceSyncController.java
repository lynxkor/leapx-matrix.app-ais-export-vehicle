package com.leapmotor.export.vehicle.controller.web.so;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import com.leapmotor.paltform.ais.common.model.enums.BooleanEnum;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoInterfaceSyncDTOStructMapper;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoInterfaceSyncService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;

/**
 * SO接口同步记录表 PC端接口
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "PC端 SO接口同步记录表管理", description = "SO接口同步记录表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/so/vehicleCbuSoInterfaceSync")
public class VehicleCbuSoInterfaceSyncController {

    private VehicleCbuSoInterfaceSyncService vehicleCbuSoInterfaceSyncService;

    @Operation(summary = "SO接口同步记录表列表查询")
    @ResourcePermission(value = "web:so:vehicleCbuSoInterfaceSync:list", desc = "SO接口同步记录表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam) {
        List<VehicleCbuSoInterfaceSyncResp> vehicleCbuSoInterfaceSyncRespList = vehicleCbuSoInterfaceSyncService.listVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncParam);
        return AjaxResult.success(VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.domainsToOptionals(vehicleCbuSoInterfaceSyncRespList));
    }

    @Operation(summary = "SO接口同步记录表分页查询")
    @ResourcePermission(value = "web:so:vehicleCbuSoInterfaceSync:page", desc = "SO接口同步记录表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<VehicleCbuSoInterfaceSyncResp>> page(@RequestBody VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam) {
        return AjaxResult.success(vehicleCbuSoInterfaceSyncService.pageVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncParam));
    }

    @Operation(summary = "SO接口同步记录表详情信息")
    @ResourcePermission(value = "web:so:vehicleCbuSoInterfaceSync:get", desc = "SO接口同步记录表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<VehicleCbuSoInterfaceSyncResp> getVehicleCbuSoInterfaceSyncById(@RequestBody LpBaseParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            return AjaxResult.fail("参数ID不能为空");
        }
        return AjaxResult.success(vehicleCbuSoInterfaceSyncService.getVehicleCbuSoInterfaceSyncById(param.getId()));
    }

    @Operation(summary = "SO接口同步记录表新增/编辑")
    @ResourcePermission(value = "web:so:vehicleCbuSoInterfaceSync:addOrUpdate", desc = "SO接口同步记录表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated VehicleCbuSoInterfaceSyncAddOrUpdateReq vehicleCbuSoInterfaceSyncAddOrUpdateReq) {
        return AjaxResult.success(vehicleCbuSoInterfaceSyncService.addOrUpdateVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncAddOrUpdateReq));
    }

    @Operation(summary = "SO接口同步记录表删除")
    @ResourcePermission(value = "web:so:vehicleCbuSoInterfaceSync:delete", desc = "SO接口同步记录表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        vehicleCbuSoInterfaceSyncService.deleteVehicleCbuSoInterfaceSyncByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "SO接口同步记录表启用/禁用")
    @ResourcePermission(value = "web:so:vehicleCbuSoInterfaceSync:enableOrDisable", desc = "SO接口同步记录表启用/禁用")
    @PostMapping("/enableOrDisable")
    public AjaxResult<Void> enableOrDisable(@RequestBody LpBaseParam param) {
        if (CollectionUtils.isEmpty(param.getIdSet())) {
            return AjaxResult.fail("请选择要操作的记录");
        }
        if (Objects.isNull(BooleanEnum.getByCode(param.getStatus()))) {
            return AjaxResult.fail("请选择要操作的状态");
        }

        StatusEnum statusEnum = StatusEnum.matchByStatus(param.getStatus());
        if (statusEnum == null) {
            return AjaxResult.fail("状态参数错误");
        }
        vehicleCbuSoInterfaceSyncService.enableOrDisableVehicleCbuSoInterfaceSyncByIds(param);
        return AjaxResult.success();
    }
}
