package com.leapmotor.export.vehicle.controller.web.basic;

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
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
/**
 * 保函主表 PC端接口
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Tag(name = "PC端 保函主表管理" , description = "保函主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/basic/exportVehicleLetterGuarantee")
public class ExportVehicleLetterGuaranteeController {

    private ExportVehicleLetterGuaranteeService exportVehicleLetterGuaranteeService;

    @Operation(summary = "保函主表列表查询")
    @ResourcePermission(value="web:basic:exportVehicleLetterGuarantee:list",desc="保函主表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam) {
        List<ExportVehicleLetterGuaranteeResp> exportVehicleLetterGuaranteeRespList =exportVehicleLetterGuaranteeService.listExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeParam);
        return AjaxResult.success(ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToOptionals(exportVehicleLetterGuaranteeRespList));
    }

    @Operation(summary = "保函主表分页查询")
    @ResourcePermission(value="web:basic:exportVehicleLetterGuarantee:page",desc="保函主表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleLetterGuaranteeResp>> page(@RequestBody ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.pageExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeParam));
    }

    @Operation(summary = "保函主表详情信息")
    @ResourcePermission(value="web:basic:exportVehicleLetterGuarantee:get",desc="保函主表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleLetterGuaranteeResp> getExportVehicleLetterGuaranteeById(@RequestBody String id) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.getExportVehicleLetterGuaranteeById(id));
    }

    @Operation(summary = "保函主表新增/编辑")
    @ResourcePermission(value="web:basic:exportVehicleLetterGuarantee:addOrUpdate",desc="保函主表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated ExportVehicleLetterGuaranteeAddOrUpdateReq exportVehicleLetterGuaranteeAddOrUpdateReq) {
        return AjaxResult.success(exportVehicleLetterGuaranteeService.addOrUpdateExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeAddOrUpdateReq));
    }

    @Operation(summary = "保函主表删除")
    @ResourcePermission(value="web:basic:exportVehicleLetterGuarantee:delete",desc="保函主表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        exportVehicleLetterGuaranteeService.deleteExportVehicleLetterGuaranteeByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "保函主表启用/禁用")
    @ResourcePermission(value="web:basic:exportVehicleLetterGuarantee:enableOrDisable",desc="保函主表启用/禁用")
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
}
