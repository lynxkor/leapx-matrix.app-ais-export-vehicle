package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.ExportCbuVehiclePoDetailApi;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoDetailRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoDetailResponse;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoDetailResp;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoDetailService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.AllArgsConstructor;

/**
 * PO订单明细表 Feign Api 实现类
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "FeignAPI PO订单明细表实现类", description = "PO订单明细表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/po/purchaseOrderDetail")
public class ExportCbuVehiclePoDetailApiImpl implements ExportCbuVehiclePoDetailApi {

    private ExportCbuVehiclePoDetailService purchaseOrderDetailService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<ExportCbuVehiclePoDetailResponse>> list(@RequestBody ExportCbuVehiclePoDetailRequest purchaseOrderDetailRequest) {
        ExportCbuVehiclePoDetailParam param = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.requestToParam(purchaseOrderDetailRequest);
        List<ExportCbuVehiclePoDetailResp> respList = purchaseOrderDetailService.listExportCbuVehiclePoDetail(param);
        List<ExportCbuVehiclePoDetailResponse> dtoList = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<ExportCbuVehiclePoDetailResponse>> page(@RequestBody ExportCbuVehiclePoDetailRequest purchaseOrderDetailRequest) {
        ExportCbuVehiclePoDetailParam param = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.requestToParam(purchaseOrderDetailRequest);
        PageResult<ExportCbuVehiclePoDetailResp> pageResult = purchaseOrderDetailService.pageExportCbuVehiclePoDetail(param);
        PageResult<ExportCbuVehiclePoDetailResponse> dtoPage = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<ExportCbuVehiclePoDetailResponse> getExportCbuVehiclePoDetailById(@RequestBody String id) {
        ExportCbuVehiclePoDetailResp resp = purchaseOrderDetailService.getExportCbuVehiclePoDetailById(id);
        ExportCbuVehiclePoDetailResponse dto = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody ExportCbuVehiclePoDetailRequest purchaseOrderDetailRequest) {
        ExportCbuVehiclePoDetailAddOrUpdateReq req = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderDetailRequest);
        return AjaxResult.success(purchaseOrderDetailService.addOrUpdateExportCbuVehiclePoDetail(req));
    }

}