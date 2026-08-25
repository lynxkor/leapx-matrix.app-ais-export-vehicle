package com.leapmotor.export.vehicle.controller.web.basic;

import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeRecordDTOStructMapper;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeRecordService;
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
 * 保函交易明细 PC端接口
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Tag(name = "PC端 保函交易明细管理", description = "保函交易明细的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/basic/exportVehicleLetterGuaranteeRecord")
public class ExportVehicleLetterGuaranteeRecordController {

    private ExportVehicleLetterGuaranteeRecordService exportVehicleLetterGuaranteeRecordService;

    @Operation(summary = "保函交易明细列表查询")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuaranteeRecord:list", desc = "保函交易明细列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
        List<ExportVehicleLetterGuaranteeRecordResp> exportVehicleLetterGuaranteeRecordRespList = exportVehicleLetterGuaranteeRecordService.listExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordParam);
        return AjaxResult.success(ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToOptionals(exportVehicleLetterGuaranteeRecordRespList));
    }

    @Operation(summary = "保函交易明细分页查询")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuaranteeRecord:page", desc = "保函交易明细分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleLetterGuaranteeRecordResp>> page(@RequestBody ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
        return AjaxResult.success(exportVehicleLetterGuaranteeRecordService.pageExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordParam));
    }

    @Operation(summary = "保函交易明细详情信息")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuaranteeRecord:get", desc = "保函交易明细详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleLetterGuaranteeRecordResp> getExportVehicleLetterGuaranteeRecordById(@RequestBody LpBaseParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            return AjaxResult.fail("参数ID不能为空");
        }
        return AjaxResult.success(exportVehicleLetterGuaranteeRecordService.getExportVehicleLetterGuaranteeRecordById(param.getId()));
    }

    @Operation(summary = "保函交易明细新增/编辑")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuaranteeRecord:addOrUpdate", desc = "保函交易明细新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated ExportVehicleLetterGuaranteeRecordAddOrUpdateReq exportVehicleLetterGuaranteeRecordAddOrUpdateReq) {
        return AjaxResult.success(exportVehicleLetterGuaranteeRecordService.addOrUpdateExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordAddOrUpdateReq));
    }

    @Operation(summary = "保函交易明细删除")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuaranteeRecord:delete", desc = "保函交易明细删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        exportVehicleLetterGuaranteeRecordService.deleteExportVehicleLetterGuaranteeRecordByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "保函交易明细启用/禁用")
    @ResourcePermission(value = "web:basic:exportVehicleLetterGuaranteeRecord:enableOrDisable", desc = "保函交易明细启用/禁用")
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
        exportVehicleLetterGuaranteeRecordService.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param);
        return AjaxResult.success();
    }
}
