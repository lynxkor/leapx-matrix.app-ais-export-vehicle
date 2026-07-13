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
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoDetailService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
/**
 * PO订单明细表 PC端接口
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "PC端 PO订单明细表管理" , description = "PO订单明细表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/po/purchaseOrderDetail")
public class ExportVehicleCbuPoDetailController {

    private ExportVehicleCbuPoDetailService purchaseOrderDetailService;

    @Operation(summary = "PO订单明细表列表查询")
    @ResourcePermission(value="web:po:purchaseOrderDetail:list",desc="PO订单明细表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportVehicleCbuPoDetailParam purchaseOrderDetailParam) {
        List<ExportVehicleCbuPoDetailResp> purchaseOrderDetailRespList =purchaseOrderDetailService.listExportVehicleCbuPoDetail(purchaseOrderDetailParam);
        return AjaxResult.success(ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainsToOptionals(purchaseOrderDetailRespList));
    }

    @Operation(summary = "PO订单明细表分页查询")
    @ResourcePermission(value="web:po:purchaseOrderDetail:page",desc="PO订单明细表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleCbuPoDetailResp>> page(@RequestBody ExportVehicleCbuPoDetailParam purchaseOrderDetailParam) {
        return AjaxResult.success(purchaseOrderDetailService.pageExportVehicleCbuPoDetail(purchaseOrderDetailParam));
    }

    @Operation(summary = "PO订单明细表详情信息")
    @ResourcePermission(value="web:po:purchaseOrderDetail:get",desc="PO订单明细表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleCbuPoDetailResp> getExportVehicleCbuPoDetailById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderDetailService.getExportVehicleCbuPoDetailById(id));
    }

    @Operation(summary = "PO订单明细表新增/编辑")
    @ResourcePermission(value="web:po:purchaseOrderDetail:addOrUpdate",desc="PO订单明细表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated ExportVehicleCbuPoDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq) {
        return AjaxResult.success(purchaseOrderDetailService.addOrUpdateExportVehicleCbuPoDetail(purchaseOrderDetailAddOrUpdateReq));
    }

    @Operation(summary = "PO订单明细表删除")
    @ResourcePermission(value="web:po:purchaseOrderDetail:delete",desc="PO订单明细表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        purchaseOrderDetailService.deleteExportVehicleCbuPoDetailByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "PO订单明细表启用/禁用")
    @ResourcePermission(value="web:po:purchaseOrderDetail:enableOrDisable",desc="PO订单明细表启用/禁用")
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
        purchaseOrderDetailService.enableOrDisableExportVehicleCbuPoDetailByIds(param);
        return AjaxResult.success();
    }
}
