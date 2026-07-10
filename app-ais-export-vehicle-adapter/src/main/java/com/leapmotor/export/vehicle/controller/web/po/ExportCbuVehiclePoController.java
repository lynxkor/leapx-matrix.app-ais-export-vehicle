package com.leapmotor.export.vehicle.controller.web.po;

import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import com.leapmotor.paltform.ais.common.model.enums.BooleanEnum;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoParam;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
/**
 * PO订单主表 PC端接口
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "PC端 PO订单主表管理" , description = "PO订单主表的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/po/purchaseOrder")
public class ExportCbuVehiclePoController {

    private ExportCbuVehiclePoService purchaseOrderService;

    @Operation(summary = "PO订单主表列表查询")
    @ResourcePermission(value="web:po:purchaseOrder:list",desc="PO订单主表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportCbuVehiclePoParam purchaseOrderParam) {
        List<ExportCbuVehiclePoResp> purchaseOrderRespList =purchaseOrderService.listExportCbuVehiclePo(purchaseOrderParam);
        return AjaxResult.success(ExportCbuVehiclePoDTOStructMapper.INSTANCE.domainsToOptionals(purchaseOrderRespList));
    }

    @Operation(summary = "PO订单主表分页查询")
    @ResourcePermission(value="web:po:purchaseOrder:page",desc="PO订单主表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportCbuVehiclePoResp>> page(@RequestBody ExportCbuVehiclePoParam purchaseOrderParam) {
        return AjaxResult.success(purchaseOrderService.pageExportCbuVehiclePo(purchaseOrderParam));
    }

    @Operation(summary = "PO订单主表详情信息")
    @ResourcePermission(value="web:po:purchaseOrder:get",desc="PO订单主表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportCbuVehiclePoResp> getExportCbuVehiclePoById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderService.getExportCbuVehiclePoById(id));
    }

    @Operation(summary = "PO订单主表新增/编辑")
    @ResourcePermission(value="web:po:purchaseOrder:addOrUpdate",desc="PO订单主表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated ExportCbuVehiclePoAddOrUpdateReq purchaseOrderAddOrUpdateReq) {
        return AjaxResult.success(purchaseOrderService.addOrUpdateExportCbuVehiclePo(purchaseOrderAddOrUpdateReq));
    }

    @Operation(summary = "PO订单主表删除")
    @ResourcePermission(value="web:po:purchaseOrder:delete",desc="PO订单主表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        purchaseOrderService.deleteExportCbuVehiclePoByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "PO订单主表启用/禁用")
    @ResourcePermission(value="web:po:purchaseOrder:enableOrDisable",desc="PO订单主表启用/禁用")
    @PostMapping("/enableOrDisable")
    public AjaxResult<Void> enableOrDisable(@RequestBody LpBaseParam param) {
        if (CollectionUtils.isEmpty(param.getIdSet())) {
            return AjaxResult.fail("请选择要操作的记录");
        }
        if (Objects.isNull(BooleanEnum.getByCode(param.getStatus()))) {
            return AjaxResult.fail("请选择要操作的状态");
        }

        StatusEnum statusEnum = StatusEnum.matchByStatus(param.getStatus());
        if (statusEnum == null) {
            return AjaxResult.fail("状态参数错误");
        }
        purchaseOrderService.enableOrDisableExportCbuVehiclePoByIds(param);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单编辑保存(仅草稿态,改销售公司+明细生产备注)")
    @ResourcePermission(value="web:po:purchaseOrder:save",desc="PO订单编辑保存")
    @PostMapping("/save")
    public AjaxResult<Void> save(@RequestBody @Validated ExportCbuVehiclePoSaveReq req) {
        purchaseOrderService.saveExportCbuVehiclePo(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单销售提交")
    @ResourcePermission(value="web:po:purchaseOrder:submit",desc="PO订单销售提交")
    @PostMapping("/submit")
    public AjaxResult<Void> submit(@RequestBody @Validated ExportCbuVehiclePoSubmitReq req) {
        purchaseOrderService.submitExportCbuVehiclePo(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单销售驳回")
    @ResourcePermission(value="web:po:purchaseOrder:reject",desc="PO订单销售驳回")
    @PostMapping("/reject")
    public AjaxResult<Void> reject(@RequestBody @Validated ExportCbuVehiclePoRejectReq req) {
        purchaseOrderService.rejectExportCbuVehiclePo(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单供需审核通过")
    @ResourcePermission(value="web:po:purchaseOrder:supplyApprove",desc="PO订单供需审核通过")
    @PostMapping("/supplyApprove")
    public AjaxResult<Void> supplyApprove(@RequestBody @Validated ExportCbuVehiclePoSupplyApproveReq req) {
        purchaseOrderService.supplyApprove(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单供需审核驳回")
    @ResourcePermission(value="web:po:purchaseOrder:supplyReject",desc="PO订单供需审核驳回")
    @PostMapping("/supplyReject")
    public AjaxResult<Void> supplyReject(@RequestBody @Validated ExportCbuVehiclePoSupplyRejectReq req) {
        purchaseOrderService.supplyReject(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单产销平衡审核通过")
    @ResourcePermission(value="web:po:purchaseOrder:balanceApprove",desc="PO订单产销平衡审核通过")
    @PostMapping("/balanceApprove")
    public AjaxResult<Void> balanceApprove(@RequestBody @Validated ExportCbuVehiclePoBalanceApproveReq req) {
        purchaseOrderService.balanceApprove(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单产销平衡审核驳回")
    @ResourcePermission(value="web:po:purchaseOrder:balanceReject",desc="PO订单产销平衡审核驳回")
    @PostMapping("/balanceReject")
    public AjaxResult<Void> balanceReject(@RequestBody @Validated ExportCbuVehiclePoBalanceRejectReq req) {
        purchaseOrderService.balanceReject(req);
        return AjaxResult.success();
    }
}
