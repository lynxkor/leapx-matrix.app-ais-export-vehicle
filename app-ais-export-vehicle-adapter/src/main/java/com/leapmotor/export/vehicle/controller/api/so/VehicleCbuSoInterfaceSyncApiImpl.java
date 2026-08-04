package com.leapmotor.export.vehicle.controller.api.so;

import com.leapmotor.export.vehicle.so.api.VehicleCbuSoInterfaceSyncApi;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoInterfaceSyncRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoInterfaceSyncResponse;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoInterfaceSyncDTOStructMapper;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoInterfaceSyncService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.AllArgsConstructor;

/**
 * SO接口同步记录表 Feign Api 实现类
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Tag(name = "FeignAPI SO接口同步记录表实现类", description = "SO接口同步记录表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/api/so/vehicleCbuSoInterfaceSync")
public class VehicleCbuSoInterfaceSyncApiImpl implements VehicleCbuSoInterfaceSyncApi {

    private VehicleCbuSoInterfaceSyncService vehicleCbuSoInterfaceSyncService;

    @PostMapping("/list")
    @Override
    public AjaxResult<List<VehicleCbuSoInterfaceSyncResponse>> list(@RequestBody VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest) {
        VehicleCbuSoInterfaceSyncParam param = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.requestToParam(vehicleCbuSoInterfaceSyncRequest);
        List<VehicleCbuSoInterfaceSyncResp> respList = vehicleCbuSoInterfaceSyncService.listVehicleCbuSoInterfaceSync(param);
        List<VehicleCbuSoInterfaceSyncResponse> dtoList = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.domainsToResponses(respList);
        return AjaxResult.success(dtoList);
    }

    @PostMapping("/page")
    @Override
    public AjaxResult<PageResult<VehicleCbuSoInterfaceSyncResponse>> page(@RequestBody VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest) {
        VehicleCbuSoInterfaceSyncParam param = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.requestToParam(vehicleCbuSoInterfaceSyncRequest);
        PageResult<VehicleCbuSoInterfaceSyncResp> pageResult = vehicleCbuSoInterfaceSyncService.pageVehicleCbuSoInterfaceSync(param);
        PageResult<VehicleCbuSoInterfaceSyncResponse> dtoPage = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.domainsToResponsePage(pageResult);
        return AjaxResult.success(dtoPage);
    }

    @PostMapping("/get")
    @Override
    public AjaxResult<VehicleCbuSoInterfaceSyncResponse> getVehicleCbuSoInterfaceSyncById(@RequestBody String id) {
        VehicleCbuSoInterfaceSyncResp resp = vehicleCbuSoInterfaceSyncService.getVehicleCbuSoInterfaceSyncById(id);
        VehicleCbuSoInterfaceSyncResponse dto = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.domainToResponse(resp);
        return AjaxResult.success(dto);
    }

    @PostMapping("/addOrUpdate")
    @Override
    public AjaxResult<String> addOrUpdate(@RequestBody VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest) {
        VehicleCbuSoInterfaceSyncAddOrUpdateReq req = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.dtoToReq(vehicleCbuSoInterfaceSyncRequest);
        return AjaxResult.success(vehicleCbuSoInterfaceSyncService.addOrUpdateVehicleCbuSoInterfaceSync(req));
    }

}