package com.leapmotor.export.vehicle.controller.api.so;

import com.leapmotor.export.vehicle.so.api.VehicleCbuSoDetailApi;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoDetailRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoDetailResponse;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoDetailService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.AllArgsConstructor;

/**
 * SO车辆明细表 Feign Api 实现类
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "FeignAPI SO车辆明细表实现类", description = "SO车辆明细表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/so/vehicleCbuSoDetail")
public class VehicleCbuSoDetailApiImpl implements VehicleCbuSoDetailApi {

    private VehicleCbuSoDetailService vehicleCbuSoDetailService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<VehicleCbuSoDetailResponse>> list(@RequestBody VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest) {
        VehicleCbuSoDetailParam param = VehicleCbuSoDetailDTOStructMapper.INSTANCE.requestToParam(vehicleCbuSoDetailRequest);
        List<VehicleCbuSoDetailResp> respList = vehicleCbuSoDetailService.listVehicleCbuSoDetail(param);
        List<VehicleCbuSoDetailResponse> dtoList = VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<VehicleCbuSoDetailResponse>> page(@RequestBody VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest) {
        VehicleCbuSoDetailParam param = VehicleCbuSoDetailDTOStructMapper.INSTANCE.requestToParam(vehicleCbuSoDetailRequest);
        PageResult<VehicleCbuSoDetailResp> pageResult = vehicleCbuSoDetailService.pageVehicleCbuSoDetail(param);
        PageResult<VehicleCbuSoDetailResponse> dtoPage = VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<VehicleCbuSoDetailResponse> getVehicleCbuSoDetailById(@RequestBody String id) {
        VehicleCbuSoDetailResp resp = vehicleCbuSoDetailService.getVehicleCbuSoDetailById(id);
        VehicleCbuSoDetailResponse dto = VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest) {
        VehicleCbuSoDetailAddOrUpdateReq req = VehicleCbuSoDetailDTOStructMapper.INSTANCE.dtoToReq(vehicleCbuSoDetailRequest);
        return AjaxResult.success(vehicleCbuSoDetailService.addOrUpdateVehicleCbuSoDetail(req));
    }

}