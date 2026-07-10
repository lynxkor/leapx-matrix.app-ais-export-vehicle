package com.leapmotor.export.vehicle.po.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.api.ExportCbuVehiclePoDetailApi;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoDetailRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PO订单明细表 Feign客户端降级工厂
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Component("ExportCbuVehiclePoDetailApiFallbackFactory")
public class ExportCbuVehiclePoDetailApiFallbackFactory implements FallbackFactory<ExportCbuVehiclePoDetailApi> {

    private static final Logger log = LoggerFactory.getLogger(ExportCbuVehiclePoDetailApiFallbackFactory.class);

    @Override
    public ExportCbuVehiclePoDetailApi create(Throwable cause) {
        return new ExportCbuVehiclePoDetailApi() {

            @Override
            public AjaxResult<List<ExportCbuVehiclePoDetailResponse>> list(ExportCbuVehiclePoDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表列表查询服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<ExportCbuVehiclePoDetailResponse>> page(ExportCbuVehiclePoDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表分页查询服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                PageResult<ExportCbuVehiclePoDetailResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("purchaseOrderDetail-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<ExportCbuVehiclePoDetailResponse> getExportCbuVehiclePoDetailById(String id) {
                log.error("PO订单明细表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(ExportCbuVehiclePoDetailRequest purchaseOrderDetailRequest) {
                log.error("PO订单明细表新增/编辑服务降级，参数：{}，异常：{}", purchaseOrderDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrderDetail-service服务异常：" + cause.getMessage());
            }
        };
    }
}