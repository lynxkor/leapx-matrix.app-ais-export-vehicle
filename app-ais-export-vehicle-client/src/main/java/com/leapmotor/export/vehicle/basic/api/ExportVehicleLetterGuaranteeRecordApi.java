package com.leapmotor.export.vehicle.basic.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRecordRequest;
import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeRecordResponse;
import com.leapmotor.export.vehicle.basic.api.fallback.ExportVehicleLetterGuaranteeRecordApiFallbackFactory;

import java.util.List;

/**
 * 保函交易明细 Feign客户端
 * @author wangkai2527148
 * @since 2026-07-02
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="ExportVehicleLetterGuaranteeRecordApi",path = "app-ais-export-vehicle/api/basic/exportVehicleLetterGuaranteeRecord", fallbackFactory = ExportVehicleLetterGuaranteeRecordApiFallbackFactory.class)
public interface ExportVehicleLetterGuaranteeRecordApi {

    /**
     * 保函交易明细列表查询
     *
     * @param exportVehicleLetterGuaranteeRecordRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<ExportVehicleLetterGuaranteeRecordResponse>> list(@RequestBody ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest);

    /**
     * 保函交易明细分页查询
     *
     * @param exportVehicleLetterGuaranteeRecordRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<ExportVehicleLetterGuaranteeRecordResponse>> page(@RequestBody ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest);

    /**
     * 保函交易明细详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<ExportVehicleLetterGuaranteeRecordResponse> getExportVehicleLetterGuaranteeRecordById(@RequestBody String id);

    /**
     * 保函交易明细新增/编辑
     *
     * @param exportVehicleLetterGuaranteeRecordRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleLetterGuaranteeRecordRequest exportVehicleLetterGuaranteeRecordRequest);
}