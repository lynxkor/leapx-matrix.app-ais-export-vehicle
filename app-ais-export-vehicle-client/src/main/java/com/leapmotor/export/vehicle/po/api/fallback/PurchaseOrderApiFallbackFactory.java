package com.leapmotor.export.vehicle.po.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.api.PurchaseOrderApi;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PO订单主表 Feign客户端降级工厂
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Component("PurchaseOrderApiFallbackFactory")
public class PurchaseOrderApiFallbackFactory implements FallbackFactory<PurchaseOrderApi> {

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderApiFallbackFactory.class);

    @Override
    public PurchaseOrderApi create(Throwable cause) {
        return new PurchaseOrderApi() {

            @Override
            public AjaxResult<List<PurchaseOrderResponse>> list(PurchaseOrderRequest purchaseOrderRequest) {
                log.error("PO订单主表列表查询服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<PurchaseOrderResponse>> page(PurchaseOrderRequest purchaseOrderRequest) {
                log.error("PO订单主表分页查询服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                PageResult<PurchaseOrderResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("purchaseOrder-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<PurchaseOrderResponse> getPurchaseOrderById(String id) {
                log.error("PO订单主表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(PurchaseOrderRequest purchaseOrderRequest) {
                log.error("PO订单主表新增/编辑服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage());
            }
        };
    }
}