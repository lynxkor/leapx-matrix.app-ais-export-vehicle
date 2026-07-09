package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.ExportCbuVehiclePoGuaranteeApi;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoGuaranteeResponse;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoGuaranteeResp;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoGuaranteeService;
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
public class ExportCbuVehiclePoGuaranteeApiImpl implements ExportCbuVehiclePoGuaranteeApi {

    private ExportCbuVehiclePoGuaranteeService purchaseOrderGuaranteeService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<ExportCbuVehiclePoGuaranteeResponse>> list(@RequestBody ExportCbuVehiclePoGuaranteeRequest purchaseOrderGuaranteeRequest) {
        ExportCbuVehiclePoGuaranteeParam param = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.requestToParam(purchaseOrderGuaranteeRequest);
        List<ExportCbuVehiclePoGuaranteeResp> respList = purchaseOrderGuaranteeService.listExportCbuVehiclePoGuarantee(param);
        List<ExportCbuVehiclePoGuaranteeResponse> dtoList = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<ExportCbuVehiclePoGuaranteeResponse>> page(@RequestBody ExportCbuVehiclePoGuaranteeRequest purchaseOrderGuaranteeRequest) {
        ExportCbuVehiclePoGuaranteeParam param = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.requestToParam(purchaseOrderGuaranteeRequest);
        PageResult<ExportCbuVehiclePoGuaranteeResp> pageResult = purchaseOrderGuaranteeService.pageExportCbuVehiclePoGuarantee(param);
        PageResult<ExportCbuVehiclePoGuaranteeResponse> dtoPage = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<ExportCbuVehiclePoGuaranteeResponse> getExportCbuVehiclePoGuaranteeById(@RequestBody String id) {
        ExportCbuVehiclePoGuaranteeResp resp = purchaseOrderGuaranteeService.getExportCbuVehiclePoGuaranteeById(id);
        ExportCbuVehiclePoGuaranteeResponse dto = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody ExportCbuVehiclePoGuaranteeRequest purchaseOrderGuaranteeRequest) {
        ExportCbuVehiclePoGuaranteeAddOrUpdateReq req = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderGuaranteeRequest);
        return AjaxResult.success(purchaseOrderGuaranteeService.addOrUpdateExportCbuVehiclePoGuarantee(req));
    }

}