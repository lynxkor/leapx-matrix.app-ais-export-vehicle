package com.leapmotor.export.vehicle.po.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoDetailRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoDetailResponse;
import com.leapmotor.export.vehicle.po.api.fallback.ExportVehicleCbuPoDetailApiFallbackFactory;

import java.util.List;

/**
 * PO订单明细表 Feign客户端
 * @author leapmotor
 * @since 2026-07-08
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="ExportVehicleCbuPoDetailApi",path = "${spring.application.name}/api/po/purchaseOrderDetail", fallbackFactory = ExportVehicleCbuPoDetailApiFallbackFactory.class)
public interface ExportVehicleCbuPoDetailApi {

    /**
     * PO订单明细表列表查询
     *
     * @param purchaseOrderDetailRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<ExportVehicleCbuPoDetailResponse>> list(@RequestBody ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest);

    /**
     * PO订单明细表分页查询
     *
     * @param purchaseOrderDetailRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<ExportVehicleCbuPoDetailResponse>> page(@RequestBody ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest);

    /**
     * PO订单明细表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<ExportVehicleCbuPoDetailResponse> getExportVehicleCbuPoDetailById(@RequestBody String id);

    /**
     * PO订单明细表新增/编辑
     *
     * @param purchaseOrderDetailRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest);
}