package com.leapmotor.export.vehicle.so.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoInterfaceSyncRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoInterfaceSyncResponse;
import com.leapmotor.export.vehicle.so.api.fallback.VehicleCbuSoInterfaceSyncApiFallbackFactory;

import java.util.List;

/**
 * SO接口同步记录表 Feign客户端
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@FeignClient(name = "app-ais-export-vehicle", contextId = "VehicleCbuSoInterfaceSyncApi", path = "/vehicle/api/so/vehicleCbuSoInterfaceSync", fallbackFactory = VehicleCbuSoInterfaceSyncApiFallbackFactory.class)
public interface VehicleCbuSoInterfaceSyncApi {

    /**
     * SO接口同步记录表列表查询
     *
     * @param vehicleCbuSoInterfaceSyncRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<VehicleCbuSoInterfaceSyncResponse>> list(@RequestBody VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest);

    /**
     * SO接口同步记录表分页查询
     *
     * @param vehicleCbuSoInterfaceSyncRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<VehicleCbuSoInterfaceSyncResponse>> page(@RequestBody VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest);

    /**
     * SO接口同步记录表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<VehicleCbuSoInterfaceSyncResponse> getVehicleCbuSoInterfaceSyncById(@RequestBody String id);

    /**
     * SO接口同步记录表新增/编辑
     *
     * @param vehicleCbuSoInterfaceSyncRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest);
}