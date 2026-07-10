package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.ExportVehicleCbuPoApi;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoResponse;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * PO订单主表 Feign Api 实现类
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "FeignAPI PO订单主表实现类", description = "PO订单主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/po/purchaseOrder")
public class ExportVehicleCbuPoApiImpl implements ExportVehicleCbuPoApi {

    private ExportVehicleCbuPoService purchaseOrderService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<ExportVehicleCbuPoResponse>> list(@RequestBody ExportVehicleCbuPoRequest purchaseOrderRequest) {
        ExportVehicleCbuPoParam param = ExportVehicleCbuPoDTOStructMapper.INSTANCE.requestToParam(purchaseOrderRequest);
        List<ExportVehicleCbuPoResp> respList = purchaseOrderService.listExportVehicleCbuPo(param);
        List<ExportVehicleCbuPoResponse> dtoList = ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<ExportVehicleCbuPoResponse>> page(@RequestBody ExportVehicleCbuPoRequest purchaseOrderRequest) {
        ExportVehicleCbuPoParam param = ExportVehicleCbuPoDTOStructMapper.INSTANCE.requestToParam(purchaseOrderRequest);
        PageResult<ExportVehicleCbuPoResp> pageResult = purchaseOrderService.pageExportVehicleCbuPo(param);
        PageResult<ExportVehicleCbuPoResponse> dtoPage = ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<ExportVehicleCbuPoResponse> getExportVehicleCbuPoById(@RequestBody String id) {
        ExportVehicleCbuPoResp resp = purchaseOrderService.getExportVehicleCbuPoById(id);
        ExportVehicleCbuPoResponse dto = ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleCbuPoRequest purchaseOrderRequest) {
        ExportVehicleCbuPoAddOrUpdateReq req = ExportVehicleCbuPoDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderRequest);
        return AjaxResult.success(purchaseOrderService.addOrUpdateExportVehicleCbuPo(req));
    }

}