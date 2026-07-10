package com.leapmotor.export.vehicle.controller.app.po;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeParam;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoGuaranteeResp;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoGuaranteeService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;

/**
 * PO-保函关联表(一个PO多保函) 移动端接口控制器
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "移动端 PO-保函关联表(一个PO多保函)管理" , description = "PO-保函关联表(一个PO多保函)的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/app/po/purchaseOrderGuarantee")
public class ExportCbuVehiclePoGuaranteeAppController {

    private ExportCbuVehiclePoGuaranteeService purchaseOrderGuaranteeService;

    @Operation(summary = "PO-保函关联表(一个PO多保函)分页查询")
    @ResourcePermission("app:po:purchaseOrderGuarantee:page")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportCbuVehiclePoGuaranteeResp>> page(@RequestBody ExportCbuVehiclePoGuaranteeParam purchaseOrderGuaranteeParam) {
        return AjaxResult.success(purchaseOrderGuaranteeService.pageExportCbuVehiclePoGuarantee(purchaseOrderGuaranteeParam));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)详情信息")
    @ResourcePermission("app:po:purchaseOrderGuarantee:get")
    @PostMapping(value = "/get")
    public AjaxResult<ExportCbuVehiclePoGuaranteeResp> getExportCbuVehiclePoGuaranteeById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderGuaranteeService.getExportCbuVehiclePoGuaranteeById(id));
    }

}
