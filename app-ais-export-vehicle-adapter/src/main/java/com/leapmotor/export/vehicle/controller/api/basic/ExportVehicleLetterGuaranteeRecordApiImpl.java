//package com.leapmotor.export.vehicle.controller.api.basic;
//
//import com.leapmotor.export.vehicle.basic.api.ExportVehicleLetterGuaranteeRecordApi;
//import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRecordRequest;
//import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeRecordResponse;
//import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
//import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;
//import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
//import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeRecordDTOStructMapper;
//import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeRecordService;
//import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
//import com.leapmotor.paltform.ais.common.model.response.PageResult;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import lombok.AllArgsConstructor;
//
///**
// * 保函交易明细 Feign Api 实现类
// * @author wangkai2527148
// * @since 2026-07-02
// */
//@Tag(name = "FeignAPI 保函交易明细实现类", description = "保函交易明细的API接口文档")
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/basic/exportVehicleLetterGuaranteeRecord")
//public class ExportVehicleLetterGuaranteeRecordApiImpl implements ExportVehicleLetterGuaranteeRecordApi {
//
//    private ExportVehicleLetterGuaranteeRecordService exportVehicleLetterGuaranteeRecordService;
//
//    @PostMapping("/list")
//    @Override
//    public AjaxResult<List<ExportVehicleLetterGuaranteeRecordResponse>> list(@RequestBody ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest) {
//        ExportVehicleLetterGuaranteeRecordParam param = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.requestToParam(exportVehicleLetterGuaranteeRecordRequest);
//        List<ExportVehicleLetterGuaranteeRecordResp> respList = exportVehicleLetterGuaranteeRecordService.listExportVehicleLetterGuaranteeRecord(param);
//        List<ExportVehicleLetterGuaranteeRecordResponse> dtoList = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToResponses(respList);
//        return AjaxResult.success(dtoList);
//    }
//
//    @PostMapping("/page")
//    @Override
//    public AjaxResult<PageResult<ExportVehicleLetterGuaranteeRecordResponse>> page(@RequestBody ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest) {
//        ExportVehicleLetterGuaranteeRecordParam param = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.requestToParam(exportVehicleLetterGuaranteeRecordRequest);
//        PageResult<ExportVehicleLetterGuaranteeRecordResp> pageResult = exportVehicleLetterGuaranteeRecordService.pageExportVehicleLetterGuaranteeRecord(param);
//        PageResult<ExportVehicleLetterGuaranteeRecordResponse> dtoPage = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
//        return AjaxResult.success(dtoPage);
//    }
//
//    @PostMapping("/get")
//    @Override
//    public AjaxResult<ExportVehicleLetterGuaranteeRecordResponse> getExportVehicleLetterGuaranteeRecordById(@RequestBody String id) {
//        ExportVehicleLetterGuaranteeRecordResp resp = exportVehicleLetterGuaranteeRecordService.getExportVehicleLetterGuaranteeRecordById(id);
//        ExportVehicleLetterGuaranteeRecordResponse dto = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainToResponse(resp);
//        return AjaxResult.success(dto);
//    }
//
//    @PostMapping("/addOrUpdate")
//    @Override
//    public AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest) {
//        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.dtoToReq(exportVehicleLetterGuaranteeRecordRequest);
//        return AjaxResult.success(exportVehicleLetterGuaranteeRecordService.addOrUpdateExportVehicleLetterGuaranteeRecord(req));
//    }
//
//}