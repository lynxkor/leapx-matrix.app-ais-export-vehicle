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
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoDetailService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;

/**
 * SO车辆明细表 PC端接口
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "PC端 SO车辆明细表管理", description = "SO车辆明细表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/so/vehicleCbuSoDetail")
public class VehicleCbuSoDetailController {

    private VehicleCbuSoDetailService vehicleCbuSoDetailService;

    @Operation(summary = "SO车辆明细表列表查询")
    @ResourcePermission(value = "web:so:vehicleCbuSoDetail:list", desc = "SO车辆明细表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody VehicleCbuSoDetailParam vehicleCbuSoDetailParam) {
        List<VehicleCbuSoDetailResp> vehicleCbuSoDetailRespList = vehicleCbuSoDetailService.listVehicleCbuSoDetail(vehicleCbuSoDetailParam);
        return AjaxResult.success(VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainsToOptionals(vehicleCbuSoDetailRespList));
    }

    @Operation(summary = "SO车辆明细表分页查询")
    @ResourcePermission(value = "web:so:vehicleCbuSoDetail:page", desc = "SO车辆明细表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<VehicleCbuSoDetailResp>> page(@RequestBody VehicleCbuSoDetailParam vehicleCbuSoDetailParam) {
        return AjaxResult.success(vehicleCbuSoDetailService.pageVehicleCbuSoDetail(vehicleCbuSoDetailParam));
    }

    @Operation(summary = "SO车辆明细表详情信息")
    @ResourcePermission(value = "web:so:vehicleCbuSoDetail:get", desc = "SO车辆明细表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<VehicleCbuSoDetailResp> getVehicleCbuSoDetailById(@RequestBody String id) {
        return AjaxResult.success(vehicleCbuSoDetailService.getVehicleCbuSoDetailById(id));
    }

    @Operation(summary = "SO车辆明细表新增/编辑")
    @ResourcePermission(value = "web:so:vehicleCbuSoDetail:addOrUpdate", desc = "SO车辆明细表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated VehicleCbuSoDetailAddOrUpdateReq vehicleCbuSoDetailAddOrUpdateReq) {
        return AjaxResult.success(vehicleCbuSoDetailService.addOrUpdateVehicleCbuSoDetail(vehicleCbuSoDetailAddOrUpdateReq));
    }

    @Operation(summary = "SO车辆明细表删除")
    @ResourcePermission(value = "web:so:vehicleCbuSoDetail:delete", desc = "SO车辆明细表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        vehicleCbuSoDetailService.deleteVehicleCbuSoDetailByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "SO车辆明细表启用/禁用")
    @ResourcePermission(value = "web:so:vehicleCbuSoDetail:enableOrDisable", desc = "SO车辆明细表启用/禁用")
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
        vehicleCbuSoDetailService.enableOrDisableVehicleCbuSoDetailByIds(param);
        return AjaxResult.success();
    }
}
