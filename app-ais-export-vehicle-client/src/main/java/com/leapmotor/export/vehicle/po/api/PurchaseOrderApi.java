package com.leapmotor.export.vehicle.po.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderResponse;
import com.leapmotor.export.vehicle.po.api.fallback.PurchaseOrderApiFallbackFactory;

import java.util.List;

/**
 * PO订单主表 Feign客户端
 * @author leapmotor
 * @since 2026-07-08
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="PurchaseOrderApi",path = "${appPath}/api/po/purchaseOrder", fallbackFactory = PurchaseOrderApiFallbackFactory.class)
public interface PurchaseOrderApi {

    /**
     * PO订单主表列表查询
     *
     * @param purchaseOrderRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<PurchaseOrderResponse>> list(@RequestBody PurchaseOrderRequest purchaseOrderRequest);

    /**
     * PO订单主表分页查询
     *
     * @param purchaseOrderRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<PurchaseOrderResponse>> page(@RequestBody PurchaseOrderRequest purchaseOrderRequest);

    /**
     * PO订单主表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<PurchaseOrderResponse> getPurchaseOrderById(@RequestBody String id);

    /**
     * PO订单主表新增/编辑
     *
     * @param purchaseOrderRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody PurchaseOrderRequest purchaseOrderRequest);
}