package com.leapmotor.export.vehicle.basic.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.basic.api.ExportVehicleLetterGuaranteeApi;
import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRequest;
import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 保函主表 Feign客户端降级工厂
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Component("ExportVehicleLetterGuaranteeApiFallbackFactory")
public class ExportVehicleLetterGuaranteeApiFallbackFactory implements FallbackFactory<ExportVehicleLetterGuaranteeApi> {

    private static final Logger log = LoggerFactory.getLogger(ExportVehicleLetterGuaranteeApiFallbackFactory.class);

    @Override
    public ExportVehicleLetterGuaranteeApi create(Throwable cause) {
        return new ExportVehicleLetterGuaranteeApi() {

            @Override
            public AjaxResult<List<ExportVehicleLetterGuaranteeResponse>> list(ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest) {
                log.error("保函主表列表查询服务降级，参数：{}，异常：{}", exportVehicleLetterGuaranteeRequest, cause.getMessage(), cause);
                return AjaxResult.fail("exportVehicleLetterGuarantee-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<ExportVehicleLetterGuaranteeResponse>> page(ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest) {
                log.error("保函主表分页查询服务降级，参数：{}，异常：{}", exportVehicleLetterGuaranteeRequest, cause.getMessage(), cause);
                PageResult<ExportVehicleLetterGuaranteeResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("exportVehicleLetterGuarantee-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<ExportVehicleLetterGuaranteeResponse> getExportVehicleLetterGuaranteeById(String id) {
                log.error("保函主表详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("exportVehicleLetterGuarantee-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest) {
                log.error("保函主表新增/编辑服务降级，参数：{}，异常：{}", exportVehicleLetterGuaranteeRequest, cause.getMessage(), cause);
                return AjaxResult.fail("exportVehicleLetterGuarantee-service服务异常：" + cause.getMessage());
            }
        };
    }
}