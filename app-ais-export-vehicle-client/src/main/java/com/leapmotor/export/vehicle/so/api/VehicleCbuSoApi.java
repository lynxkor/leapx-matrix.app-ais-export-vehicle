package com.leapmotor.export.vehicle.so.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoResponse;
import com.leapmotor.export.vehicle.so.api.fallback.VehicleCbuSoApiFallbackFactory;

import java.util.List;

/**
 * CBU整车销售订单主表 Feign客户端
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@FeignClient(name = "app-ais-export-vehicle", contextId = "VehicleCbuSoApi", path = "/vehicle/api/so/vehicleCbuSo", fallbackFactory = VehicleCbuSoApiFallbackFactory.class)
public interface VehicleCbuSoApi {

    /**
     * CBU整车销售订单主表列表查询
     *
     * @param vehicleCbuSoRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<VehicleCbuSoResponse>> list(@RequestBody VehicleCbuSoRequest vehicleCbuSoRequest);

    /**
     * CBU整车销售订单主表分页查询
     *
     * @param vehicleCbuSoRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<VehicleCbuSoResponse>> page(@RequestBody VehicleCbuSoRequest vehicleCbuSoRequest);

    /**
     * CBU整车销售订单主表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<VehicleCbuSoResponse> getVehicleCbuSoById(@RequestBody String id);

    /**
     * CBU整车销售订单主表新增/编辑
     *
     * @param vehicleCbuSoRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody VehicleCbuSoRequest vehicleCbuSoRequest);
}