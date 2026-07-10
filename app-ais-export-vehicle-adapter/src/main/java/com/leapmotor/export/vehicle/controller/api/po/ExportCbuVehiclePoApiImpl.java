package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.ExportCbuVehiclePoApi;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoResponse;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoResp;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoService;
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
public class ExportCbuVehiclePoApiImpl implements ExportCbuVehiclePoApi {

    private ExportCbuVehiclePoService purchaseOrderService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<ExportCbuVehiclePoResponse>> list(@RequestBody ExportCbuVehiclePoRequest purchaseOrderRequest) {
        ExportCbuVehiclePoParam param = ExportCbuVehiclePoDTOStructMapper.INSTANCE.requestToParam(purchaseOrderRequest);
        List<ExportCbuVehiclePoResp> respList = purchaseOrderService.listExportCbuVehiclePo(param);
        List<ExportCbuVehiclePoResponse> dtoList = ExportCbuVehiclePoDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<ExportCbuVehiclePoResponse>> page(@RequestBody ExportCbuVehiclePoRequest purchaseOrderRequest) {
        ExportCbuVehiclePoParam param = ExportCbuVehiclePoDTOStructMapper.INSTANCE.requestToParam(purchaseOrderRequest);
        PageResult<ExportCbuVehiclePoResp> pageResult = purchaseOrderService.pageExportCbuVehiclePo(param);
        PageResult<ExportCbuVehiclePoResponse> dtoPage = ExportCbuVehiclePoDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<ExportCbuVehiclePoResponse> getExportCbuVehiclePoById(@RequestBody String id) {
        ExportCbuVehiclePoResp resp = purchaseOrderService.getExportCbuVehiclePoById(id);
        ExportCbuVehiclePoResponse dto = ExportCbuVehiclePoDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody ExportCbuVehiclePoRequest purchaseOrderRequest) {
        ExportCbuVehiclePoAddOrUpdateReq req = ExportCbuVehiclePoDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderRequest);
        return AjaxResult.success(purchaseOrderService.addOrUpdateExportCbuVehiclePo(req));
    }

}