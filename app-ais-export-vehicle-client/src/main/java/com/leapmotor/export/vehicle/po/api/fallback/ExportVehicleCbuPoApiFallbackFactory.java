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
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<ExportVehicleCbuPoResponse>> page(ExportVehicleCbuPoRequest purchaseOrderRequest) {
                log.error("PO订单主表分页查询服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                PageResult<ExportVehicleCbuPoResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("purchaseOrder-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<ExportVehicleCbuPoResponse> getExportVehicleCbuPoById(String id) {
                log.error("PO订单主表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(ExportVehicleCbuPoRequest purchaseOrderRequest) {
                log.error("PO订单主表新增/编辑服务降级，参数：{}，异常：{}", purchaseOrderRequest, cause.getMessage(), cause);
                return AjaxResult.fail("purchaseOrder-service服务异常：" + cause.getMessage());
            }
        };
    }
}