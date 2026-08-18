package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.ExportVehicleCbuPoGuaranteeApi;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoGuaranteeResponse;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoGuaranteeService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * PO-保函关联表(一个PO多保函) Feign Api 实现类
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "FeignAPI PO-保函关联表(一个PO多保函)实现类", description = "PO-保函关联表(一个PO多保函)的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/po/purchaseOrderGuarantee")
public class ExportVehicleCbuPoGuaranteeApiImpl implements ExportVehicleCbuPoGuaranteeApi {

    private ExportVehicleCbuPoGuaranteeService purchaseOrderGuaranteeService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<ExportVehicleCbuPoGuaranteeResponse>> list(@RequestBody ExportVehicleCbuPoGuaranteeRequest purchaseOrderGuaranteeRequest) {
        ExportVehicleCbuPoGuaranteeParam param = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.requestToParam(purchaseOrderGuaranteeRequest);
        List<ExportVehicleCbuPoGuaranteeResp> respList = purchaseOrderGuaranteeService.listExportVehicleCbuPoGuarantee(param);
        List<ExportVehicleCbuPoGuaranteeResponse> dtoList = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<ExportVehicleCbuPoGuaranteeResponse>> page(@RequestBody ExportVehicleCbuPoGuaranteeRequest purchaseOrderGuaranteeRequest) {
        ExportVehicleCbuPoGuaranteeParam param = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.requestToParam(purchaseOrderGuaranteeRequest);
        PageResult<ExportVehicleCbuPoGuaranteeResp> pageResult = purchaseOrderGuaranteeService.pageExportVehicleCbuPoGuarantee(param);
        PageResult<ExportVehicleCbuPoGuaranteeResponse> dtoPage = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<ExportVehicleCbuPoGuaranteeResponse> getExportVehicleCbuPoGuaranteeById(@RequestBody String id) {
        ExportVehicleCbuPoGuaranteeResp resp = purchaseOrderGuaranteeService.getExportVehicleCbuPoGuaranteeById(id);
        ExportVehicleCbuPoGuaranteeResponse dto = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleCbuPoGuaranteeRequest purchaseOrderGuaranteeRequest) {
        ExportVehicleCbuPoGuaranteeAddOrUpdateReq req = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderGuaranteeRequest);
        return AjaxResult.success(purchaseOrderGuaranteeService.addOrUpdateExportVehicleCbuPoGuarantee(req));
    }

}