package com.leapmotor.export.vehicle.po.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoResponse;
import com.leapmotor.export.vehicle.po.api.fallback.ExportVehicleCbuPoApiFallbackFactory;

import java.util.List;

/**
 * PO订单主表 Feign客户端
 * @author leapmotor
 * @since 2026-07-08
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="ExportVehicleCbuPoApi",path = "${spring.application.name}/api/po/purchaseOrder", fallbackFactory = ExportVehicleCbuPoApiFallbackFactory.class)
public interface ExportVehicleCbuPoApi {

    /**
     * PO订单主表列表查询
     *
     * @param purchaseOrderRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<ExportVehicleCbuPoResponse>> list(@RequestBody ExportVehicleCbuPoRequest purchaseOrderRequest);

    /**
     * PO订单主表分页查询
     *
     * @param purchaseOrderRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<ExportVehicleCbuPoResponse>> page(@RequestBody ExportVehicleCbuPoRequest purchaseOrderRequest);

    /**
     * PO订单主表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<ExportVehicleCbuPoResponse> getExportVehicleCbuPoById(@RequestBody String id);

    /**
     * PO订单主表新增/编辑
     *
     * @param purchaseOrderRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleCbuPoRequest purchaseOrderRequest);
}