package com.leapmotor.export.vehicle.po.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.api.ExportCbuVehiclePoApi;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * PO订单主表 Feign客户端降级工厂
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Component("ExportCbuVehiclePoApiFallbackFactory")
public class ExportCbuVehiclePoApiFallbackFactory implements FallbackFactory<ExportCbuVehiclePoApi> {

    private static final Logger log = LoggerFactory.getLogger(ExportCbuVehiclePoApiFallbackFactory.class);

    @Override
    public ExportCbuVehiclePoApi create(Throwable cause) {
        return new ExportCbuVehiclePoApi() {

            @Override
            public AjaxResult<List<ExportCbuVehiclePoResponse>> list(ExportCbuVehiclePoRequest purchaseOrderRequest) {
                log.error("PO订单主表列表查询服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<ExportCbuVehiclePoResponse>> page(ExportCbuVehiclePoRequest purchaseOrderRequest) {
                log.error("PO订单主表分页查询服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                PageResult<ExportCbuVehiclePoResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("purchaseOrder-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<ExportCbuVehiclePoResponse> getExportCbuVehiclePoById(String id) {
                log.error("PO订单主表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(ExportCbuVehiclePoRequest purchaseOrderRequest) {
                log.error("PO订单主表新增/编辑服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage());
            }
        };
    }
}