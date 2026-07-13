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
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDTOStructMapper;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;

/**
 * CBU整车销售订单主表 PC端接口
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "PC端 CBU整车销售订单主表管理", description = "CBU整车销售订单主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/so/vehicleCbuSo")
public class VehicleCbuSoController {

    private VehicleCbuSoService vehicleCbuSoService;

    @Operation(summary = "CBU整车销售订单主表列表查询")
    @ResourcePermission(value = "web:so:vehicleCbuSo:list", desc = "CBU整车销售订单主表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody VehicleCbuSoParam vehicleCbuSoParam) {
        List<VehicleCbuSoResp> vehicleCbuSoRespList = vehicleCbuSoService.listVehicleCbuSo(vehicleCbuSoParam);
        return AjaxResult.success(VehicleCbuSoDTOStructMapper.INSTANCE.domainsToOptionals(vehicleCbuSoRespList));
    }

    @Operation(summary = "CBU整车销售订单主表分页查询")
    @ResourcePermission(value = "web:so:vehicleCbuSo:page", desc = "CBU整车销售订单主表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<VehicleCbuSoResp>> page(@RequestBody VehicleCbuSoParam vehicleCbuSoParam) {
        return AjaxResult.success(vehicleCbuSoService.pageVehicleCbuSo(vehicleCbuSoParam));
    }

    @Operation(summary = "CBU整车销售订单主表详情信息")
    @ResourcePermission(value = "web:so:vehicleCbuSo:get", desc = "CBU整车销售订单主表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<VehicleCbuSoResp> getVehicleCbuSoById(@RequestBody String id) {
        return AjaxResult.success(vehicleCbuSoService.getVehicleCbuSoById(id));
    }

    @Operation(summary = "CBU整车销售订单主表新增/编辑")
    @ResourcePermission(value = "web:so:vehicleCbuSo:addOrUpdate", desc = "CBU整车销售订单主表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated VehicleCbuSoAddOrUpdateReq vehicleCbuSoAddOrUpdateReq) {
        return AjaxResult.success(vehicleCbuSoService.addOrUpdateVehicleCbuSo(vehicleCbuSoAddOrUpdateReq));
    }

    @Operation(summary = "CBU整车销售订单主表删除")
    @ResourcePermission(value = "web:so:vehicleCbuSo:delete", desc = "CBU整车销售订单主表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        vehicleCbuSoService.deleteVehicleCbuSoByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "CBU整车销售订单主表启用/禁用")
    @ResourcePermission(value = "web:so:vehicleCbuSo:enableOrDisable", desc = "CBU整车销售订单主表启用/禁用")
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
        vehicleCbuSoService.enableOrDisableVehicleCbuSoByIds(param);
        return AjaxResult.success();
    }
}
