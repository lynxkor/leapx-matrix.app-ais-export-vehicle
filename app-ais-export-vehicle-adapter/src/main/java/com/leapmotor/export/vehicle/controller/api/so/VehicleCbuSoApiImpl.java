package com.leapmotor.export.vehicle.controller.api.so;

import com.leapmotor.export.vehicle.so.api.VehicleCbuSoApi;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoResponse;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDTOStructMapper;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.AllArgsConstructor;

/**
 * CBU整车销售订单主表 Feign Api 实现类
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "FeignAPI CBU整车销售订单主表实现类", description = "CBU整车销售订单主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/so/vehicleCbuSo")
public class VehicleCbuSoApiImpl implements VehicleCbuSoApi {

    private VehicleCbuSoService vehicleCbuSoService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<VehicleCbuSoResponse>> list(@RequestBody VehicleCbuSoRequest vehicleCbuSoRequest) {
        VehicleCbuSoParam param = VehicleCbuSoDTOStructMapper.INSTANCE.requestToParam(vehicleCbuSoRequest);
        List<VehicleCbuSoResp> respList = vehicleCbuSoService.listVehicleCbuSo(param);
        List<VehicleCbuSoResponse> dtoList = VehicleCbuSoDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<VehicleCbuSoResponse>> page(@RequestBody VehicleCbuSoRequest vehicleCbuSoRequest) {
        VehicleCbuSoParam param = VehicleCbuSoDTOStructMapper.INSTANCE.requestToParam(vehicleCbuSoRequest);
        PageResult<VehicleCbuSoResp> pageResult = vehicleCbuSoService.pageVehicleCbuSo(param);
        PageResult<VehicleCbuSoResponse> dtoPage = VehicleCbuSoDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<VehicleCbuSoResponse> getVehicleCbuSoById(@RequestBody String id) {
        VehicleCbuSoResp resp = vehicleCbuSoService.getVehicleCbuSoById(id);
        VehicleCbuSoResponse dto = VehicleCbuSoDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody VehicleCbuSoRequest vehicleCbuSoRequest) {
        VehicleCbuSoAddOrUpdateReq req = VehicleCbuSoDTOStructMapper.INSTANCE.dtoToReq(vehicleCbuSoRequest);
        return AjaxResult.success(vehicleCbuSoService.addOrUpdateVehicleCbuSo(req));
    }

}