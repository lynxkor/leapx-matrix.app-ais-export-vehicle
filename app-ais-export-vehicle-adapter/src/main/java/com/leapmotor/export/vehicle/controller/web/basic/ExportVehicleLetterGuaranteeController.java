package com.leapmotor.export.vehicle.controller.web.basic;

import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.basic.req.*;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeBatchStateMachineResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.enums.BooleanEnum;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * 保函主表 PC端接口
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Tag(name = "PC端 保函主表管理", description = "保函主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/basic/exportVehicleLetterGuarantee")
public class ExportVehicleLetterGuaranteeController {

    private ExportVehicleLetterGuaranteeService exportVehicleLetterGuaranteeService;

    // ============== 基础 CRUD ==============

    @Operation(summary = "保函主表列表查询")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:list", desc = "保函主表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportVehicleLetterGuaranteeParam param) {
        List<ExportVehicleLetterGuaranteeResp> rows = exportVehicleLetterGuaranteeService.listExportVehicleLetterGuarantee(param);
        return AjaxResult.success(ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToOptionals(rows));
    }

    @Operation(summary = "保函主表分页查询")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:page", desc = "保函主表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleLetterGuaranteeResp>> page(@RequestBody ExportVehicleLetterGuaranteeParam param) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.pageExportVehicleLetterGuarantee(param));
    }

    @Operation(summary = "保函主表详情信息")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:get", desc = "保函主表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleLetterGuaranteeResp> getExportVehicleLetterGuaranteeById(@RequestBody String id) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.getExportVehicleLetterGuaranteeById(id));
    }

    @Operation(summary = "保函主表新增/编辑")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:addOrUpdate", desc = "保函主表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated ExportVehicleLetterGuaranteeAddOrUpdateReq req) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.addOrUpdateExportVehicleLetterGuarantee(req));
    }

    @Operation(summary = "保函主表删除")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:delete", desc = "保函主表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        exportVehicleLetterGuaranteeService.deleteExportVehicleLetterGuaranteeByIds(ids);
        return AjaxResult.success();
    }

    @Operation(summary = "保函主表启用/禁用")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:enableOrDisable", desc = "保函主表启用/禁用")
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
        exportVehicleLetterGuaranteeService.enableOrDisableExportVehicleLetterGuaranteeByIds(param);
        return AjaxResult.success();
    }

    // ============== 状态机(单条/批量统一入口,Req 传 id 或 ids) ==============

    @Operation(summary = "保函提交(草稿 → 已提交;传 id 单条,传 ids 批量)")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:submit", desc = "保函提交")
    @PostMapping("/submit")
    public AjaxResult<ExportVehicleLetterGuaranteeBatchStateMachineResp> submit(@RequestBody ExportVehicleLetterGuaranteeSubmitReq req) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.submit(req));
    }

    @Operation(summary = "保函审核(已提交 → 通过/驳回;传 id 单条,传 ids 批量)")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:approve", desc = "保函审核")
    @PostMapping("/approve")
    public AjaxResult<ExportVehicleLetterGuaranteeBatchStateMachineResp> approve(@RequestBody @Validated ExportVehicleLetterGuaranteeApproveReq req) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.approve(req));
    }

    @Operation(summary = "保函作废(草稿/驳回 → 作废;传 id 单条,传 ids 批量)")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:cancel", desc = "保函作废")
    @PostMapping("/cancel")
    public AjaxResult<ExportVehicleLetterGuaranteeBatchStateMachineResp> cancel(
            @RequestBody @Validated ExportVehicleLetterGuaranteeCancelReq req) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.cancel(req));
    }

    @Operation(summary = "保函提前失效(审核通过 → 失效)")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuarantee:expireEarly", desc = "保函提前失效")
    @PostMapping("/expireEarly")
    public AjaxResult<Void> expireEarly(@RequestBody ExportVehicleLetterGuaranteeExpireEarlyReq req) {
        exportVehicleLetterGuaranteeService.expireEarly(req);
        return AjaxResult.success();
    }
}
