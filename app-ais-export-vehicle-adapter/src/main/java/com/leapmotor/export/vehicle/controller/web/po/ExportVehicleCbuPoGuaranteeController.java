package com.leapmotor.export.vehicle.controller.web.po;

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
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoGuaranteeService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
/**
 * PO-保函关联表(一个PO多保函) PC端接口
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "PC端 PO-保函关联表(一个PO多保函)管理" , description = "PO-保函关联表(一个PO多保函)的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/po/purchaseOrderGuarantee")
public class ExportVehicleCbuPoGuaranteeController {

    private ExportVehicleCbuPoGuaranteeService purchaseOrderGuaranteeService;

    @Operation(summary = "PO-保函关联表(一个PO多保函)列表查询")
    @ResourcePermission(value="web:po:exportVehicleCbuPoGuarantee:list",desc="PO-保函关联表(一个PO多保函)列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportVehicleCbuPoGuaranteeParam purchaseOrderGuaranteeParam) {
        List<ExportVehicleCbuPoGuaranteeResp> purchaseOrderGuaranteeRespList =purchaseOrderGuaranteeService.listExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeParam);
        return AjaxResult.success(ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainsToOptionals(purchaseOrderGuaranteeRespList));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)分页查询")
    @ResourcePermission(value="web:po:exportVehicleCbuPoGuarantee:page",desc="PO-保函关联表(一个PO多保函)分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleCbuPoGuaranteeResp>> page(@RequestBody ExportVehicleCbuPoGuaranteeParam purchaseOrderGuaranteeParam) {
        return AjaxResult.success(purchaseOrderGuaranteeService.pageExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeParam));
    }

    @Operation(summary = "PO-保函关联表 (一个PO多保函)详情信息")
    @ResourcePermission(value="web:po:exportVehicleCbuPoGuarantee:get",desc="PO-保函关联表 (一个PO多保函)详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleCbuPoGuaranteeResp> getExportVehicleCbuPoGuaranteeById(@RequestBody LpBaseParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            return AjaxResult.fail("参数ID不能为空");
        }
        return AjaxResult.success(purchaseOrderGuaranteeService.getExportVehicleCbuPoGuaranteeById(param.getId()));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)新增/编辑")
    @ResourcePermission(value="web:po:exportVehicleCbuPoGuarantee:addOrUpdate",desc="PO-保函关联表(一个PO多保函)新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated ExportVehicleCbuPoGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq) {
        return AjaxResult.success(purchaseOrderGuaranteeService.addOrUpdateExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeAddOrUpdateReq));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)删除")
    @ResourcePermission(value="web:po:exportVehicleCbuPoGuarantee:delete",desc="PO-保函关联表(一个PO多保函)删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        purchaseOrderGuaranteeService.deleteExportVehicleCbuPoGuaranteeByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "PO-保函关联表(一个PO多保函)启用/禁用")
    @ResourcePermission(value="web:po:exportVehicleCbuPoGuarantee:enableOrDisable",desc="PO-保函关联表(一个PO多保函)启用/禁用")
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
        purchaseOrderGuaranteeService.enableOrDisableExportVehicleCbuPoGuaranteeByIds(param);
        return AjaxResult.success();
    }
}
