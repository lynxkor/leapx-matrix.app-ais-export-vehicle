package com.leapmotor.export.vehicle.po.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.api.PurchaseOrderDetailApi;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderDetailRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PO订单明细表 Feign客户端降级工厂
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Component("PurchaseOrderDetailApiFallbackFactory")
public class PurchaseOrderDetailApiFallbackFactory implements FallbackFactory<PurchaseOrderDetailApi> {

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderDetailApiFallbackFactory.class);

    @Override
    public PurchaseOrderDetailApi create(Throwable cause) {
        return new PurchaseOrderDetailApi() {

            @Override
            public AjaxResult<List<PurchaseOrderDetailResponse>> list(PurchaseOrderDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表列表查询服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<PurchaseOrderDetailResponse>> page(PurchaseOrderDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表分页查询服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                PageResult<PurchaseOrderDetailResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("purchaseOrderDetail-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<PurchaseOrderDetailResponse> getPurchaseOrderDetailById(String id) {
                log.error("PO订单明细表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(PurchaseOrderDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表新增/编辑服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage());
            }
        };
    }
}