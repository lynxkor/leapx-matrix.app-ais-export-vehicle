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
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailParam;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoDetailResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoDetailService;
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
public class ExportCbuVehiclePoDetailController {

    private ExportCbuVehiclePoDetailService purchaseOrderDetailService;

    @Operation(summary = "PO订单明细表列表查询")
    @ResourcePermission(value="web:po:purchaseOrderDetail:list",desc="PO订单明细表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportCbuVehiclePoDetailParam purchaseOrderDetailParam) {
        List<ExportCbuVehiclePoDetailResp> purchaseOrderDetailRespList =purchaseOrderDetailService.listExportCbuVehiclePoDetail(purchaseOrderDetailParam);
        return AjaxResult.success(ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.domainsToOptionals(purchaseOrderDetailRespList));
    }

    @Operation(summary = "PO订单明细表分页查询")
    @ResourcePermission(value="web:po:purchaseOrderDetail:page",desc="PO订单明细表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportCbuVehiclePoDetailResp>> page(@RequestBody ExportCbuVehiclePoDetailParam purchaseOrderDetailParam) {
        return AjaxResult.success(purchaseOrderDetailService.pageExportCbuVehiclePoDetail(purchaseOrderDetailParam));
    }

    @Operation(summary = "PO订单明细表详情信息")
    @ResourcePermission(value="web:po:purchaseOrderDetail:get",desc="PO订单明细表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportCbuVehiclePoDetailResp> getExportCbuVehiclePoDetailById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderDetailService.getExportCbuVehiclePoDetailById(id));
    }

    @Operation(summary = "PO订单明细表新增/编辑")
    @ResourcePermission(value="web:po:purchaseOrderDetail:addOrUpdate",desc="PO订单明细表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated ExportCbuVehiclePoDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq) {
        return AjaxResult.success(purchaseOrderDetailService.addOrUpdateExportCbuVehiclePoDetail(purchaseOrderDetailAddOrUpdateReq));
    }

    @Operation(summary = "PO订单明细表删除")
    @ResourcePermission(value="web:po:purchaseOrderDetail:delete",desc="PO订单明细表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        purchaseOrderDetailService.deleteExportCbuVehiclePoDetailByIds(ids);
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
        purchaseOrderDetailService.enableOrDisableExportCbuVehiclePoDetailByIds(param);
        return AjaxResult.success();
    }
}
