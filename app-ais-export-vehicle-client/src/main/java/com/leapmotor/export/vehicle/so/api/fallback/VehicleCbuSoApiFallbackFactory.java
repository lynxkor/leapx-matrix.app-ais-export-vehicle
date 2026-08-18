package com.leapmotor.export.vehicle.so.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.so.api.VehicleCbuSoApi;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * CBU整车销售订单主表 Feign客户端降级工厂
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Component("VehicleCbuSoApiFallbackFactory")
public class VehicleCbuSoApiFallbackFactory implements FallbackFactory<VehicleCbuSoApi> {

    private static final Logger log = LoggerFactory.getLogger(VehicleCbuSoApiFallbackFactory.class);

    @Override
    public VehicleCbuSoApi create(Throwable cause) {
        return new VehicleCbuSoApi() {

            @Override
            public AjaxResult<List<VehicleCbuSoResponse>> list(VehicleCbuSoRequest vehicleCbuSoRequest) {
                log.error("CBU整车销售订单主表列表查询服务降级，参数：{}，异常：{}", vehicleCbuSoRequest, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSo-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<VehicleCbuSoResponse>> page(VehicleCbuSoRequest vehicleCbuSoRequest) {
                log.error("CBU整车销售订单主表分页查询服务降级，参数：{}，异常：{}", vehicleCbuSoRequest, cause.getMessage(), cause);
                PageResult<VehicleCbuSoResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("vehicleCbuSo-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<VehicleCbuSoResponse> getVehicleCbuSoById(String id) {
                log.error("CBU整车销售订单主表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSo-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(VehicleCbuSoRequest vehicleCbuSoRequest) {
                log.error("CBU整车销售订单主表新增/编辑服务降级，参数：{}，异常：{}", vehicleCbuSoRequest, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSo-service服务异常：" + cause.getMessage());
            }
        };
    }
}