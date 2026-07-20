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
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailFullResp;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBatchReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRefreshPriceReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoBatchResp;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoRefreshPriceResp;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoService;
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
public class ExportVehicleCbuPoController {

    private ExportVehicleCbuPoService purchaseOrderService;

    @Operation(summary = "PO订单主表列表查询")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:list",desc="PO订单主表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody ExportVehicleCbuPoParam purchaseOrderParam) {
        List<ExportVehicleCbuPoResp> purchaseOrderRespList =purchaseOrderService.listExportVehicleCbuPo(purchaseOrderParam);
        return AjaxResult.success(ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainsToOptionals(purchaseOrderRespList));
    }

    @Operation(summary = "PO订单主表分页查询")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:page",desc="PO订单主表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<ExportVehicleCbuPoResp>> page(@RequestBody ExportVehicleCbuPoParam purchaseOrderParam) {
        return AjaxResult.success(purchaseOrderService.pageExportVehicleCbuPo(purchaseOrderParam));
    }

    @Operation(summary = "PO订单主表详情信息")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:get",desc="PO订单主表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<ExportVehicleCbuPoResp> getExportVehicleCbuPoById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderService.getExportVehicleCbuPoById(id));
    }

    @Operation(summary = "PO订单主子表联查详情（主表 + 明细 + 保函关联）")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:detail",desc="PO订单主子表联查详情")
    @PostMapping(value = "/detail")
    public AjaxResult<ExportVehicleCbuPoDetailFullResp> detail(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderService.getExportVehicleCbuPoDetailById(id));
    }

    @Operation(summary = "PO订单主表删除")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:delete",desc="PO订单主表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        purchaseOrderService.deleteExportVehicleCbuPoByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "PO订单主表启用/禁用")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:enableOrDisable",desc="PO订单主表启用/禁用")
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
        purchaseOrderService.enableOrDisableExportVehicleCbuPoByIds(param);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单编辑保存(仅草稿态,改销售公司+明细生产备注)")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:save",desc="PO订单编辑保存")
    @PostMapping("/save")
    public AjaxResult<Void> save(@RequestBody @Validated ExportVehicleCbuPoSaveReq req) {
        purchaseOrderService.saveExportVehicleCbuPo(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单销售提交")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:submit",desc="PO订单销售提交")
    @PostMapping("/submit")
    public AjaxResult<Void> submit(@RequestBody @Validated ExportVehicleCbuPoSubmitReq req) {
        purchaseOrderService.submitExportVehicleCbuPo(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单销售驳回")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:reject",desc="PO订单销售驳回")
    @PostMapping("/reject")
    public AjaxResult<Void> reject(@RequestBody @Validated ExportVehicleCbuPoRejectReq req) {
        purchaseOrderService.rejectExportVehicleCbuPo(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单供需审核通过")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:supplyApprove",desc="PO订单供需审核通过")
    @PostMapping("/supplyApprove")
    public AjaxResult<Void> supplyApprove(@RequestBody @Validated ExportVehicleCbuPoSupplyApproveReq req) {
        purchaseOrderService.supplyApprove(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单供需审核驳回")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:supplyReject",desc="PO订单供需审核驳回")
    @PostMapping("/supplyReject")
    public AjaxResult<Void> supplyReject(@RequestBody @Validated ExportVehicleCbuPoSupplyRejectReq req) {
        purchaseOrderService.supplyReject(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单产销平衡审核通过")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:balanceApprove",desc="PO订单产销平衡审核通过")
    @PostMapping("/balanceApprove")
    public AjaxResult<Void> balanceApprove(@RequestBody @Validated ExportVehicleCbuPoBalanceApproveReq req) {
        purchaseOrderService.balanceApprove(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单产销平衡审核驳回")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:balanceReject",desc="PO订单产销平衡审核驳回")
    @PostMapping("/balanceReject")
    public AjaxResult<Void> balanceReject(@RequestBody @Validated ExportVehicleCbuPoBalanceRejectReq req) {
        purchaseOrderService.balanceReject(req);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单刷新价格(联动保函/SO)")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:refreshPrice",desc="PO订单刷新价格")
    @PostMapping("/refreshPrice")
    public AjaxResult<ExportVehicleCbuPoRefreshPriceResp> refreshPrice(@RequestBody @Validated ExportVehicleCbuPoRefreshPriceReq req) {
        return AjaxResult.success(purchaseOrderService.refreshPrice(req));
    }

    // ==================== 批量操作 ====================

    @Operation(summary = "批量提交")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:batchSubmit",desc="批量提交")
    @PostMapping("/batchSubmit")
    public AjaxResult<ExportVehicleCbuPoBatchResp> batchSubmit(@RequestBody @Validated ExportVehicleCbuPoBatchReq req) {
        return AjaxResult.success(purchaseOrderService.batchSubmit(req));
    }

    @Operation(summary = "批量销售驳回")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:batchReject",desc="批量销售驳回")
    @PostMapping("/batchReject")
    public AjaxResult<ExportVehicleCbuPoBatchResp> batchReject(@RequestBody @Validated ExportVehicleCbuPoBatchReq req) {
        return AjaxResult.success(purchaseOrderService.batchReject(req));
    }

    @Operation(summary = "批量供需审核通过")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:batchSupplyApprove",desc="批量供需审核通过")
    @PostMapping("/batchSupplyApprove")
    public AjaxResult<ExportVehicleCbuPoBatchResp> batchSupplyApprove(@RequestBody @Validated ExportVehicleCbuPoBatchReq req) {
        return AjaxResult.success(purchaseOrderService.batchSupplyApprove(req));
    }

    @Operation(summary = "批量供需审核驳回")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:batchSupplyReject",desc="批量供需审核驳回")
    @PostMapping("/batchSupplyReject")
    public AjaxResult<ExportVehicleCbuPoBatchResp> batchSupplyReject(@RequestBody @Validated ExportVehicleCbuPoBatchReq req) {
        return AjaxResult.success(purchaseOrderService.batchSupplyReject(req));
    }

    @Operation(summary = "批量产销平衡审核通过")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:batchBalanceApprove",desc="批量产销平衡审核通过")
    @PostMapping("/batchBalanceApprove")
    public AjaxResult<ExportVehicleCbuPoBatchResp> batchBalanceApprove(@RequestBody @Validated ExportVehicleCbuPoBatchReq req) {
        return AjaxResult.success(purchaseOrderService.batchBalanceApprove(req));
    }

    @Operation(summary = "批量产销平衡审核驳回")
    @ResourcePermission(value="web:po:exportVehicleCbuPo:batchBalanceReject",desc="批量产销平衡审核驳回")
    @PostMapping("/batchBalanceReject")
    public AjaxResult<ExportVehicleCbuPoBatchResp> batchBalanceReject(@RequestBody @Validated ExportVehicleCbuPoBatchReq req) {
        return AjaxResult.success(purchaseOrderService.batchBalanceReject(req));
    }
}
