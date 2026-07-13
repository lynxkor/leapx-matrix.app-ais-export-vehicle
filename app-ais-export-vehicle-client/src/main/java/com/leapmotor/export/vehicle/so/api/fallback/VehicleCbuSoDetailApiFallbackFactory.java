package com.leapmotor.export.vehicle.so.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.so.api.VehicleCbuSoDetailApi;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoDetailRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoDetailResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * SO车辆明细表 Feign客户端降级工厂
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Component("VehicleCbuSoDetailApiFallbackFactory")
public class VehicleCbuSoDetailApiFallbackFactory implements FallbackFactory<VehicleCbuSoDetailApi> {

    private static final Logger log = LoggerFactory.getLogger(VehicleCbuSoDetailApiFallbackFactory.class);

    @Override
    public VehicleCbuSoDetailApi create(Throwable cause) {
        return new VehicleCbuSoDetailApi() {

            @Override
            public AjaxResult<List<VehicleCbuSoDetailResponse>> list(VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest) {
                log.error("SO车辆明细表列表查询服务降级，参数：{}，异常：{}", vehicleCbuSoDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSoDetail-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<VehicleCbuSoDetailResponse>> page(VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest) {
                log.error("SO车辆明细表分页查询服务降级，参数：{}，异常：{}", vehicleCbuSoDetailRequest, cause.getMessage(), cause);
                PageResult<VehicleCbuSoDetailResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("vehicleCbuSoDetail-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<VehicleCbuSoDetailResponse> getVehicleCbuSoDetailById(String id) {
                log.error("SO车辆明细表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSoDetail-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(VehicleCbuSoDetailRequest vehicleCbuSoDetailRequest) {
                log.error("SO车辆明细表新增/编辑服务降级，参数：{}，异常：{}", vehicleCbuSoDetailRequest, cause.getMessage(), cause);
                return AjaxResult.fail("vehicleCbuSoDetail-service服务异常：" + cause.getMessage());
            }
        };
    }
}