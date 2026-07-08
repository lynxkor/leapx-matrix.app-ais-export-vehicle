package com.leapmotor.export.vehicle.po.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderDetailRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderDetailResponse;
import com.leapmotor.export.vehicle.po.api.fallback.PurchaseOrderDetailApiFallbackFactory;

import java.util.List;

/**
 * PO订单明细表 Feign客户端
 * @author leapmotor
 * @since 2026-07-08
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="PurchaseOrderDetailApi",path = "${appPath}/api/po/purchaseOrderDetail", fallbackFactory = PurchaseOrderDetailApiFallbackFactory.class)
public interface PurchaseOrderDetailApi {

    /**
     * PO订单明细表列表查询
     *
     * @param purchaseOrderDetailRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<PurchaseOrderDetailResponse>> list(@RequestBody PurchaseOrderDetailRequest purchaseOrderDetailRequest);

    /**
     * PO订单明细表分页查询
     *
     * @param purchaseOrderDetailRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<PurchaseOrderDetailResponse>> page(@RequestBody PurchaseOrderDetailRequest purchaseOrderDetailRequest);

    /**
     * PO订单明细表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<PurchaseOrderDetailResponse> getPurchaseOrderDetailById(@RequestBody String id);

    /**
     * PO订单明细表新增/编辑
     *
     * @param purchaseOrderDetailRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody PurchaseOrderDetailRequest purchaseOrderDetailRequest);
}