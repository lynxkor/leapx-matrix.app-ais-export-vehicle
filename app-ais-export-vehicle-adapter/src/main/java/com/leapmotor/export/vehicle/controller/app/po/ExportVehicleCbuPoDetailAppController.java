package com.leapmotor.export.vehicle.controller.app.po;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoDetailService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

/**
 * PO订单明细表 移动端接口控制器
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "移动端 PO订单明细表管理" , description = "PO订单明细表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/app/po/purchaseOrderDetail")
public class ExportVehicleCbuPoDetailAppController {

    private ExportVehicleCbuPoDetailService purchaseOrderDetailService;

    @Operation(summary = "PO订单明细表分页查询")
    @ResourcePermission("app:po:purchaseOrderDetail:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleCbuPoDetailResp>> page(@RequestBody ExportVehicleCbuPoDetailParam purchaseOrderDetailParam) {
        return AjaxResult.success(purchaseOrderDetailService.pageExportVehicleCbuPoDetail(purchaseOrderDetailParam));
    }

    @Operation(summary = "PO订单明细表详情信息")
    @ResourcePermission("app:po:purchaseOrderDetail:get")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleCbuPoDetailResp> getExportVehicleCbuPoDetailById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderDetailService.getExportVehicleCbuPoDetailById(id));
    }

}
