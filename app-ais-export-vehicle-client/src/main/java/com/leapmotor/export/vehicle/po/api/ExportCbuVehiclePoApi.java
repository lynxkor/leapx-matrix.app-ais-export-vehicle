package com.leapmotor.export.vehicle.po.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoResponse;
import com.leapmotor.export.vehicle.po.api.fallback.ExportCbuVehiclePoApiFallbackFactory;

import java.util.List;

/**
 * PO订单主表 Feign客户端
 * @author leapmotor
 * @since 2026-07-08
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="ExportCbuVehiclePoApi",path = "${spring.application.name}/api/po/purchaseOrder", fallbackFactory = ExportCbuVehiclePoApiFallbackFactory.class)
public interface ExportCbuVehiclePoApi {

    /**
     * PO订单主表列表查询
     *
     * @param purchaseOrderRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<ExportCbuVehiclePoResponse>> list(@RequestBody ExportCbuVehiclePoRequest purchaseOrderRequest);

    /**
     * PO订单主表分页查询
     *
     * @param purchaseOrderRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<ExportCbuVehiclePoResponse>> page(@RequestBody ExportCbuVehiclePoRequest purchaseOrderRequest);

    /**
     * PO订单主表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<ExportCbuVehiclePoResponse> getExportCbuVehiclePoById(@RequestBody String id);

    /**
     * PO订单主表新增/编辑
     *
     * @param purchaseOrderRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody ExportCbuVehiclePoRequest purchaseOrderRequest);
}