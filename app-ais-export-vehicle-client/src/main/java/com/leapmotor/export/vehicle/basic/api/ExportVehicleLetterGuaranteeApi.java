package com.leapmotor.export.vehicle.basic.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRequest;
import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeResponse;
import com.leapmotor.export.vehicle.basic.api.fallback.ExportVehicleLetterGuaranteeApiFallbackFactory;

import java.util.List;

/**
 * 保函主表 Feign客户端
 * @author wangkai2527148
 * @since 2026-07-02
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="ExportVehicleLetterGuaranteeApi",path = "app-ais-export-vehicle/api/basic/exportVehicleLetterGuarantee", fallbackFactory = ExportVehicleLetterGuaranteeApiFallbackFactory.class)
public interface ExportVehicleLetterGuaranteeApi {

    /**
     * 保函主表列表查询
     *
     * @param exportVehicleLetterGuaranteeRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<ExportVehicleLetterGuaranteeResponse>> list(@RequestBody ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest);

    /**
     * 保函主表分页查询
     *
     * @param exportVehicleLetterGuaranteeRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<ExportVehicleLetterGuaranteeResponse>> page(@RequestBody ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest);

    /**
     * 保函主表详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<ExportVehicleLetterGuaranteeResponse> getExportVehicleLetterGuaranteeById(@RequestBody String id);

    /**
     * 保函主表新增/编辑
     *
     * @param exportVehicleLetterGuaranteeRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleLetterGuaranteeRequest exportVehicleLetterGuaranteeRequest);
}