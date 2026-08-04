//package com.leapmotor.export.vehicle.controller.api.basic;
//
//import com.leapmotor.export.vehicle.basic.api.ExportVehicleLetterGuaranteeApi;
//import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRequest;
//import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeResponse;
//import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
//import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAddOrUpdateReq;
//import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
//import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeDTOStructMapper;
//import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
//import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
//import com.leapmotor.paltform.ais.common.model.response.PageResult;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import lombok.AllArgsConstructor;
//
///**
// * 保函主表 Feign Api 实现类
// * @author wangkai2527148
// * @since 2026-07-02
// */
//@Tag(name = "FeignAPI 保函主表实现类", description = "保函主表的API接口文档")
//@RestController
//@AllArgsConstructor
//@RequestMapping("/api/basic/exportVehicleLetterGuarantee")
//public class ExportVehicleLetterGuaranteeApiImpl implements ExportVehicleLetterGuaranteeApi {
//
//    private ExportVehicleLetterGuaranteeService exportVehicleLetterGuaranteeService;
//
//    @PostMapping("/list")
//    @Override
//    public AjaxResult<List<ExportVehicleLetterGuaranteeResponse>> list(@RequestBody ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest) {
//        ExportVehicleLetterGuaranteeParam param = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.requestToParam(exportVehicleLetterGuaranteeRequest);
//        List<ExportVehicleLetterGuaranteeResp> respList = exportVehicleLetterGuaranteeService.listExportVehicleLetterGuarantee(param);
//        List<ExportVehicleLetterGuaranteeResponse> dtoList = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToResponses(respList);
//        return AjaxResult.success(dtoList);
//    }
//
//    @PostMapping("/page")
//    @Override
//    public AjaxResult<PageResult<ExportVehicleLetterGuaranteeResponse>> page(@RequestBody ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest) {
//        ExportVehicleLetterGuaranteeParam param = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.requestToParam(exportVehicleLetterGuaranteeRequest);
//        PageResult<ExportVehicleLetterGuaranteeResp> pageResult = exportVehicleLetterGuaranteeService.pageExportVehicleLetterGuarantee(param);
//        PageResult<ExportVehicleLetterGuaranteeResponse> dtoPage = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
//        return AjaxResult.success(dtoPage);
//    }
//
//    @PostMapping("/get")
//    @Override
//    public AjaxResult<ExportVehicleLetterGuaranteeResponse> getExportVehicleLetterGuaranteeById(@RequestBody String id) {
//        ExportVehicleLetterGuaranteeResp resp = exportVehicleLetterGuaranteeService.getExportVehicleLetterGuaranteeById(id);
//        ExportVehicleLetterGuaranteeResponse dto = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainToResponse(resp);
//        return AjaxResult.success(dto);
//    }
//
//    @PostMapping("/addOrUpdate")
//    @Override
//    public AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest) {
//        ExportVehicleLetterGuaranteeAddOrUpdateReq req = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.dtoToReq(exportVehicleLetterGuaranteeRequest);
//        return AjaxResult.success(exportVehicleLetterGuaranteeService.addOrUpdateExportVehicleLetterGuarantee(req));
//    }
//
//}