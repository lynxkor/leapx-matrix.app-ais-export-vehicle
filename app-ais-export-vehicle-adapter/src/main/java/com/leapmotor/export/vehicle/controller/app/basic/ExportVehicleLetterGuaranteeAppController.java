//package com.leapmotor.export.vehicle.controller.app.basic;
//
//import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
//import com.leapmotor.paltform.ais.common.model.response.PageResult;
//import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.RequestMapping;
//import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
//import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
//import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
//import org.springframework.web.bind.annotation.*;
//import lombok.AllArgsConstructor;
//
///**
// * 保函主表 移动端接口控制器
// *
// * @author wangkai2527148
// * @since 2026-07-02
// */
//@Tag(name = "移动端 保函主表管理" , description = "保函主表的API接口文档")
//@RestController
//@AllArgsConstructor
//@RequestMapping("/app/basic/exportVehicleLetterGuarantee")
//public class ExportVehicleLetterGuaranteeAppController {
//
//    private ExportVehicleLetterGuaranteeService exportVehicleLetterGuaranteeService;
//
//    @Operation(summary = "保函主表分页查询")
//    @ResourcePermission("app:basic:exportVehicleLetterGuarantee:page")
//    @PostMapping("/page")
//    public AjaxResult<PageResult<ExportVehicleLetterGuaranteeResp>> page(@RequestBody ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam) {
//        return AjaxResult.success(exportVehicleLetterGuaranteeService.pageExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeParam));
//    }
//
//    @Operation(summary = "保函主表详情信息")
//    @ResourcePermission("app:basic:exportVehicleLetterGuarantee:get")
//    @PostMapping(value = "/get")
//    public AjaxResult<ExportVehicleLetterGuaranteeResp> getExportVehicleLetterGuaranteeById(@RequestBody String id) {
//        return AjaxResult.success(exportVehicleLetterGuaranteeService.getExportVehicleLetterGuaranteeById(id));
//    }
//
//}
