package com.leapmotor.export.vehicle.basic.api.fallback;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.basic.api.ExportVehicleLetterGuaranteeRecordApi;
import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRecordRequest;
import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeRecordResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 保函交易明细(追加写) Feign客户端降级工厂
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Component("ExportVehicleLetterGuaranteeRecordApiFallbackFactory")
public class ExportVehicleLetterGuaranteeRecordApiFallbackFactory implements FallbackFactory<ExportVehicleLetterGuaranteeRecordApi> {

    private static final Logger log = LoggerFactory.getLogger(ExportVehicleLetterGuaranteeRecordApiFallbackFactory.class);

    @Override
    public ExportVehicleLetterGuaranteeRecordApi create(Throwable cause) {
        return new ExportVehicleLetterGuaranteeRecordApi() {

            @Override
            public AjaxResult<List<ExportVehicleLetterGuaranteeRecordResponse>> list(ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest) {
                log.error("保函交易明细(追加写)列表查询服务降级，参数：{}，异常：{}", exportVehicleLetterGuaranteeRecordRequest, cause.getMessage(), cause);
                return AjaxResult.fail("exportVehicleLetterGuaranteeRecord-service服务异常：" + cause.getMessage(), new ArrayList<>());
            }

            @Override
            public AjaxResult<PageResult<ExportVehicleLetterGuaranteeRecordResponse>> page(ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest) {
                log.error("保函交易明细(追加写)分页查询服务降级，参数：{}，异常：{}", exportVehicleLetterGuaranteeRecordRequest, cause.getMessage(), cause);
                PageResult<ExportVehicleLetterGuaranteeRecordResponse> pageResult = new PageResult<>();
                pageResult.setCode(500);
                pageResult.setMsg("exportVehicleLetterGuaranteeRecord-service服务异常：" + cause.getMessage());
                pageResult.setRecords(new ArrayList<>());
                pageResult.setTotal(0L);
                pageResult.setPageNum(1);
                pageResult.setPageSize(10);
                return AjaxResult.success(pageResult);
            }

            @Override
            public AjaxResult<ExportVehicleLetterGuaranteeRecordResponse> getExportVehicleLetterGuaranteeRecordById(String id) {
                log.error("保函交易明细(追加写)详情查询服务降级，ID：{}，异常：{}", id, cause.getMessage(), cause);
                return AjaxResult.fail("exportVehicleLetterGuaranteeRecord-service服务异常：" + cause.getMessage());
            }

            @Override
            public AjaxResult<String> addOrUpdate(ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest) {
                log.error("保函交易明细(追加写)新增/编辑服务降级，参数：{}，异常：{}", exportVehicleLetterGuaranteeRecordRequest, cause.getMessage(), cause);
                return AjaxResult.fail("exportVehicleLetterGuaranteeRecord-service服务异常：" + cause.getMessage());
            }
        };
    }
}