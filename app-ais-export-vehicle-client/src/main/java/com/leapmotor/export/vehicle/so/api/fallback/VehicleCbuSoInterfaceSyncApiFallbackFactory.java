package com.leapmotor.export.vehicle.so.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.so.api.VehicleCbuSoInterfaceSyncApi;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoInterfaceSyncRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoInterfaceSyncResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * SO接口同步记录表 Feign客户端降级工厂
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Component("VehicleCbuSoInterfaceSyncApiFallbackFactory")
public class VehicleCbuSoInterfaceSyncApiFallbackFactory implements FallbackFactory<VehicleCbuSoInterfaceSyncApi> {

    private static final Logger log = LoggerFactory.getLogger(VehicleCbuSoInterfaceSyncApiFallbackFactory.class);

    @Override
    public VehicleCbuSoInterfaceSyncApi create(Throwable cause) {
        return new VehicleCbuSoInterfaceSyncApi() {

            @Override
            public AjaxResult<List<VehicleCbuSoInterfaceSyncResponse>> list(VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest) {
                log.error("SO接口同步记录表列表查询服务降级，参数：{}，异常：{}", vehicleCbuSoInterfaceSyncRequest, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSoInterfaceSync-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<VehicleCbuSoInterfaceSyncResponse>> page(VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest) {
                log.error("SO接口同步记录表分页查询服务降级，参数：{}，异常：{}", vehicleCbuSoInterfaceSyncRequest, cause.getMessage(), cause);
                PageResult<VehicleCbuSoInterfaceSyncResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("vehicleCbuSoInterfaceSync-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<VehicleCbuSoInterfaceSyncResponse> getVehicleCbuSoInterfaceSyncById(String id) {
                log.error("SO接口同步记录表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSoInterfaceSync-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(VehicleCbuSoInterfaceSyncRequest vehicleCbuSoInterfaceSyncRequest) {
                log.error("SO接口同步记录表新增/编辑服务降级，参数：{}，异常：{}", vehicleCbuSoInterfaceSyncRequest, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSoInterfaceSync-service服务异常：" + cause.getMessage());
            }
        };
    }
}