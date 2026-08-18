package com.leapmotor.export.vehicle.controller.app.po;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

/**
 * PO订单主表 移动端接口控制器
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "移动端 PO订单主表管理" , description = "PO订单主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/app/po/purchaseOrder")
public class ExportVehicleCbuPoAppController {

    private ExportVehicleCbuPoService purchaseOrderService;

    @Operation(summary = "PO订单主表分页查询")
    @ResourcePermission("app:po:purchaseOrder:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleCbuPoResp>> page(@RequestBody ExportVehicleCbuPoParam purchaseOrderParam) {
        return AjaxResult.success(purchaseOrderService.pageExportVehicleCbuPo(purchaseOrderParam));
    }

    @Operation(summary = "PO订单主表详情信息")
    @ResourcePermission("app:po:purchaseOrder:get")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleCbuPoResp> getExportVehicleCbuPoById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderService.getExportVehicleCbuPoById(id));
    }

}
