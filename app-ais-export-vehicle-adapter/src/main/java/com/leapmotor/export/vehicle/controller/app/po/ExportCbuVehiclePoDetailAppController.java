package com.leapmotor.export.vehicle.controller.app.po;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailParam;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoDetailResp;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoDetailService;
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
public class ExportCbuVehiclePoDetailAppController {

    private ExportCbuVehiclePoDetailService purchaseOrderDetailService;

    @Operation(summary = "PO订单明细表分页查询")
    @ResourcePermission("app:po:purchaseOrderDetail:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportCbuVehiclePoDetailResp>> page(@RequestBody ExportCbuVehiclePoDetailParam purchaseOrderDetailParam) {
        return AjaxResult.success(purchaseOrderDetailService.pageExportCbuVehiclePoDetail(purchaseOrderDetailParam));
    }

    @Operation(summary = "PO订单明细表详情信息")
    @ResourcePermission("app:po:purchaseOrderDetail:get")
    @PostMapping(value = "/get")
    public AjaxResult<ExportCbuVehiclePoDetailResp> getExportCbuVehiclePoDetailById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderDetailService.getExportCbuVehiclePoDetailById(id));
    }

}
