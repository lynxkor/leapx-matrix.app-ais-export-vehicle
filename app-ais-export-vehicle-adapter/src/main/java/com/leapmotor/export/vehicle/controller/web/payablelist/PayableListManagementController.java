package com.leapmotor.export.vehicle.controller.web.payablelist;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.req.*;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.export.vehicle.payablelist.service.PayableListService;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.excel.in.ExcelBaseResult;
import com.leapmotor.paltform.ais.common.model.excel.in.ImportExcelDTO;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 应付清单管理 PC端接口
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Tag(name = "PC端 应付清单管理", description = "应付清单管理的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/payableList/payableListManagement")
public class PayableListManagementController {

    private PayableListService payableListService;

    @Operation(summary = "应付清单分页查询")
    @ResourcePermission(value = "web:payableList:payableListManagement:page", desc = "应付清单分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<PayableListResp>> page(@RequestBody PayableListPageParam param) {
        return AjaxResult.success(payableListService.pagePayableList(param));
    }

    @Operation(summary = "应付清单详情查询")
    @ResourcePermission(value = "web:payableList:payableListManagement:get", desc = "应付清单详情查询")
    @PostMapping("/get")
    public AjaxResult<PayableListResp> get(@RequestBody LpBaseParam param) {
        if (Objects.isNull(param) || Objects.isNull(param.getId())) {
            return AjaxResult.fail("参数ID不能为空");
        }
        return AjaxResult.success(payableListService.getPayableListById(param.getId()));
    }

    @Operation(summary = "提交应付清单")
    @ResourcePermission(value = "web:payableList:payableListManagement:submit", desc = "提交应付清单")
    @PostMapping("/submit")
    public AjaxResult<String> submit(@RequestBody PayableListSubmitReq req) {
        return AjaxResult.success(payableListService.submitPayableList(req));
    }

    @Operation(summary = "更新应付清单基础信息（客户编码、客户名称）")
    @ResourcePermission(value = "web:payableList:payableListManagement:addOrUpdate", desc = "更新应付清单基础信息")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated PayableListUpdateReq req) {
        return AjaxResult.success(payableListService.updatePayableList(req));
    }

    @Operation(summary = "批量提交应付清单")
    @ResourcePermission(value = "web:payableList:payableListManagement:batchSubmit", desc = "批量提交应付清单")
    @PostMapping("/batchSubmit")
    public AjaxResult<List<String>> batchSubmit(@RequestBody PayableListBatchSubmitReq req) {
        return AjaxResult.success(payableListService.batchSubmitPayableList(req));
    }

    @Operation(summary = "查询可添加车辆")
    @ResourcePermission(value = "web:payableList:payableListManagement:queryAvailableVehicles", desc = "查询可添加车辆")
    @PostMapping("/queryAvailableVehicles")
    public AjaxResult<PageResult<SoOrderForPayableDto>> queryAvailableVehicles(@RequestBody PayableListQueryAvailableVehiclesReq req) {
        return AjaxResult.success(payableListService.queryAvailableVehicles(req));
    }

    @Operation(summary = "添加车辆")
    @ResourcePermission(value = "web:payableList:payableListManagement:addVehicles", desc = "添加车辆")
    @PostMapping("/addVehicles")
    public AjaxResult<String> addVehicles(@RequestBody PayableListAddVehiclesReq req) {
        return AjaxResult.success(payableListService.addVehicles(req));
    }

    @Operation(summary = "删除车辆")
    @ResourcePermission(value = "web:payableList:payableListManagement:deleteVehicles", desc = "删除车辆")
    @PostMapping("/deleteVehicles")
    public AjaxResult<String> deleteVehicles(@RequestBody PayableListDeleteVehicleReq req) {
        return AjaxResult.success(payableListService.deleteVehicles(req));
    }

    @Operation(summary = "导入最新价格")
    @ResourcePermission(value = "web:payableList:payableListManagement:importPrice", desc = "导入最新价格")
    @PostMapping("/importPrice")
    public AjaxResult<ExcelBaseResult> importPrice(@RequestBody @Validated ImportExcelDTO dto) {
        return AjaxResult.success(payableListService.importPrice(dto));
    }

}
