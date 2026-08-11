package com.leapmotor.export.vehicle.controller.web.payablelist;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.req.*;
import com.leapmotor.export.vehicle.payablelist.resp.ExcelBaseResult;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.export.vehicle.payablelist.service.PayableListService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public AjaxResult<PayableListResp> get(@RequestBody String id) {
        return AjaxResult.success(payableListService.getPayableListById(id));
    }

    @Operation(summary = "提交应付清单")
    @ResourcePermission(value = "web:payableList:payableListManagement:submit", desc = "提交应付清单")
    @PostMapping("/submit")
    public AjaxResult<String> submit(@RequestBody PayableListSubmitReq req) {
        return AjaxResult.success(payableListService.submitPayableList(req));
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
    public AjaxResult<List<SoOrderForPayableDto>> queryAvailableVehicles(@RequestBody PayableListQueryAvailableVehiclesReq req) {
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
    public AjaxResult<ExcelBaseResult> importPrice(@RequestBody PayableListImportPriceReq req) {
        return AjaxResult.success(payableListService.importPrice(req));
    }

    @Operation(summary = "删除应付清单")
    @ResourcePermission(value = "web:payableList:payableListManagement:delete", desc = "删除应付清单")
    @PostMapping("/delete")
    public AjaxResult<Void> delete(@RequestBody List<String> ids) {
        payableListService.deletePayableListByIds(ids);
        return AjaxResult.success();
    }
}
