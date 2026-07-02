//package com.leapmotor.export.vehicle.controller.app.basic;
//
//import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
//import com.leapmotor.paltform.ais.common.model.response.PageResult;
//import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.RequestMapping;
//import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
//import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
//import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeRecordService;
//import org.springframework.web.bind.annotation.*;
//import lombok.AllArgsConstructor;
//
///**
// * 保函交易明细(追加写) 移动端接口控制器
// *
// * @author wangkai2527148
// * @since 2026-07-02
// */
//@Tag(name = "移动端 保函交易明细(追加写)管理" , description = "保函交易明细(追加写)的API接口文档")
//@RestController
//@AllArgsConstructor
//@RequestMapping("/app/basic/exportVehicleLetterGuaranteeRecord")
//public class ExportVehicleLetterGuaranteeRecordAppController {
//
//    private ExportVehicleLetterGuaranteeRecordService exportVehicleLetterGuaranteeRecordService;
//
//    @Operation(summary = "保函交易明细(追加写)分页查询")
//    @ResourcePermission("app:basic:exportVehicleLetterGuaranteeRecord:page")
//    @PostMapping("/page")
//    public AjaxResult<PageResult<ExportVehicleLetterGuaranteeRecordResp>> page(@RequestBody ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
//        return AjaxResult.success(exportVehicleLetterGuaranteeRecordService.pageExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordParam));
//    }
//
//    @Operation(summary = "保函交易明细(追加写)详情信息")
//    @ResourcePermission("app:basic:exportVehicleLetterGuaranteeRecord:get")
//    @PostMapping(value = "/get")
//    public AjaxResult<ExportVehicleLetterGuaranteeRecordResp> getExportVehicleLetterGuaranteeRecordById(@RequestBody String id) {
//        return AjaxResult.success(exportVehicleLetterGuaranteeRecordService.getExportVehicleLetterGuaranteeRecordById(id));
//    }
//
//}
