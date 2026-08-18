package com.leapmotor.export.vehicle.po.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.api.ExportVehicleCbuPoApi;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PO订单主表 Feign客户端降级工厂
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Component("ExportVehicleCbuPoApiFallbackFactory")
public class ExportVehicleCbuPoApiFallbackFactory implements FallbackFactory<ExportVehicleCbuPoApi> {

    private static final Logger log = LoggerFactory.getLogger(ExportVehicleCbuPoApiFallbackFactory.class);

    @Override
    public ExportVehicleCbuPoApi create(Throwable cause) {
        return new ExportVehicleCbuPoApi() {

            @Override
            public AjaxResult<List<ExportVehicleCbuPoResponse>> list(ExportVehicleCbuPoRequest purchaseOrderRequest) {
                log.error("PO订单主表列表查询服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                return AjaxResult.fail("PO订单服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<ExportVehicleCbuPoResponse>> page(ExportVehicleCbuPoRequest purchaseOrderRequest) {
                log.error("PO订单主表分页查询服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                return AjaxResult.fail("PO订单服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<ExportVehicleCbuPoResponse> getExportVehicleCbuPoById(String id) {
                log.error("PO订单主表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("PO订单服务异常：" + cause.getMessage());
            }
        };
    }
}