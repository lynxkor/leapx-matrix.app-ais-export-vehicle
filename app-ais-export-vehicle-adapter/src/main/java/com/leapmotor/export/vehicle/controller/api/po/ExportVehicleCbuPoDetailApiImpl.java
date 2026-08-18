package com.leapmotor.export.vehicle.controller.api.po;

import com.leapmotor.export.vehicle.po.api.ExportVehicleCbuPoDetailApi;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoDetailRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoDetailResponse;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoDetailService;
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
public class ExportVehicleCbuPoDetailApiImpl implements ExportVehicleCbuPoDetailApi {

    private ExportVehicleCbuPoDetailService purchaseOrderDetailService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<ExportVehicleCbuPoDetailResponse>> list(@RequestBody ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest) {
        ExportVehicleCbuPoDetailParam param = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.requestToParam(purchaseOrderDetailRequest);
        List<ExportVehicleCbuPoDetailResp> respList = purchaseOrderDetailService.listExportVehicleCbuPoDetail(param);
        List<ExportVehicleCbuPoDetailResponse> dtoList = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<ExportVehicleCbuPoDetailResponse>> page(@RequestBody ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest) {
        ExportVehicleCbuPoDetailParam param = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.requestToParam(purchaseOrderDetailRequest);
        PageResult<ExportVehicleCbuPoDetailResp> pageResult = purchaseOrderDetailService.pageExportVehicleCbuPoDetail(param);
        PageResult<ExportVehicleCbuPoDetailResponse> dtoPage = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<ExportVehicleCbuPoDetailResponse> getExportVehicleCbuPoDetailById(@RequestBody String id) {
        ExportVehicleCbuPoDetailResp resp = purchaseOrderDetailService.getExportVehicleCbuPoDetailById(id);
        ExportVehicleCbuPoDetailResponse dto = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest) {
        ExportVehicleCbuPoDetailAddOrUpdateReq req = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.dtoToReq(purchaseOrderDetailRequest);
        return AjaxResult.success(purchaseOrderDetailService.addOrUpdateExportVehicleCbuPoDetail(req));
    }

}