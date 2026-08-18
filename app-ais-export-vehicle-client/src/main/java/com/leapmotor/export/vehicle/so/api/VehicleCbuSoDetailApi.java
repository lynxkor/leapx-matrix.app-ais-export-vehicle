package com.leapmotor.export.vehicle.so.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoDetailRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoDetailResponse;
import com.leapmotor.export.vehicle.so.api.fallback.VehicleCbuSoDetailApiFallbackFactory;

import java.util.List;

/**
 * SO车辆明细表 Feign客户端
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@FeignClient(name = "app-ais-export-vehicle", contextId = "VehicleCbuSoDetailApi", path = "/vehicle/api/so/vehicleCbuSoDetail", fallbackFactory = VehicleCbuSoDetailApiFallbackFactory.class)
public interface VehicleCbuSoDetailApi {

    /**
     * SO车辆明细表列表查询
     *
     * @param vehicleCbuSoDetailRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<VehicleCbuSoDetailResponse>> list(@RequestBody VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest);

    /**
     * SO车辆明细表分页查询
     *
     * @param vehicleCbuSoDetailRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<VehicleCbuSoDetailResponse>> page(@RequestBody VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest);

    /**
     * SO车辆明细表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<VehicleCbuSoDetailResponse> getVehicleCbuSoDetailById(@RequestBody String id);

    /**
     * SO车辆明细表新增/编辑
     *
     * @param vehicleCbuSoDetailRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest);
}