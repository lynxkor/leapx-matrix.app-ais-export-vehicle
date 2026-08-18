package com.leapmotor.export.vehicle.po.api;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoGuaranteeResponse;
import com.leapmotor.export.vehicle.po.api.fallback.ExportVehicleCbuPoGuaranteeApiFallbackFactory;

import java.util.List;

/**
 * PO-保函关联表(一个PO多保函) Feign客户端
 * @author leapmotor
 * @since 2026-07-08
 */
@FeignClient(name = "app-ais-export-vehicle",contextId="ExportVehicleCbuPoGuaranteeApi",path = "${spring.application.name}/api/po/purchaseOrderGuarantee", fallbackFactory = ExportVehicleCbuPoGuaranteeApiFallbackFactory.class)
public interface ExportVehicleCbuPoGuaranteeApi {

    /**
     * PO-保函关联表(一个PO多保函)列表查询
     *
     * @param purchaseOrderGuaranteeRequest 查询条件
     * @return 查询结果
     */
    @PostMapping("/list")
    AjaxResult<List<ExportVehicleCbuPoGuaranteeResponse>> list(@RequestBody ExportVehicleCbuPoGuaranteeRequest purchaseOrderGuaranteeRequest);

    /**
     * PO-保函关联表(一个PO多保函)分页查询
     *
     * @param purchaseOrderGuaranteeRequest 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    AjaxResult<PageResult<ExportVehicleCbuPoGuaranteeResponse>> page(@RequestBody ExportVehicleCbuPoGuaranteeRequest purchaseOrderGuaranteeRequest);

    /**
     * PO-保函关联表(一个PO多保函)详情信息
     *
     * @param id 主键ID
     * @return 详情信息
     */
    @PostMapping("/get")
    AjaxResult<ExportVehicleCbuPoGuaranteeResponse> getExportVehicleCbuPoGuaranteeById(@RequestBody String id);

    /**
     * PO-保函关联表(一个PO多保函)新增/编辑
     *
     * @param purchaseOrderGuaranteeRequest 新增/编辑请求参数
     * @return 操作结果
     */
    @PostMapping("/addOrUpdate")
    AjaxResult<String> addOrUpdate(@RequestBody ExportVehicleCbuPoGuaranteeRequest purchaseOrderGuaranteeRequest);
}