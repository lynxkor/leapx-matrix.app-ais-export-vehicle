package com.leapmotor.export.vehicle.po.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.api.ExportVehicleCbuPoDetailApi;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoDetailRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PO订单明细表 Feign客户端降级工厂
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Component("ExportVehicleCbuPoDetailApiFallbackFactory")
public class ExportVehicleCbuPoDetailApiFallbackFactory implements FallbackFactory<ExportVehicleCbuPoDetailApi> {

    private static final Logger log = LoggerFactory.getLogger(ExportVehicleCbuPoDetailApiFallbackFactory.class);

    @Override
    public ExportVehicleCbuPoDetailApi create(Throwable cause) {
        return new ExportVehicleCbuPoDetailApi() {

            @Override
            public AjaxResult<List<ExportVehicleCbuPoDetailResponse>> list(ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表列表查询服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<ExportVehicleCbuPoDetailResponse>> page(ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表分页查询服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                PageResult<ExportVehicleCbuPoDetailResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("purchaseOrderDetail-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<ExportVehicleCbuPoDetailResponse> getExportVehicleCbuPoDetailById(String id) {
                log.error("PO订单明细表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(ExportVehicleCbuPoDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表新增/编辑服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage());
            }
        };
    }
}