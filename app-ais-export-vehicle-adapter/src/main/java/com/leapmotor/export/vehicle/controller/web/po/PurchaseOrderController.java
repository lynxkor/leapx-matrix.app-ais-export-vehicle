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
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderSaveReq;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderService;
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
public class PurchaseOrderController {

    private PurchaseOrderService purchaseOrderService;

    @Operation(summary = "PO订单主表列表查询")
    @ResourcePermission(value="web:po:purchaseOrder:list",desc="PO订单主表列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody PurchaseOrderParam purchaseOrderParam) {
        List<PurchaseOrderResp> purchaseOrderRespList =purchaseOrderService.listPurchaseOrder(purchaseOrderParam);
        return AjaxResult.success(PurchaseOrderDTOStructMapper.INSTANCE.domainsToOptionals(purchaseOrderRespList));
    }

    @Operation(summary = "PO订单主表分页查询")
    @ResourcePermission(value="web:po:purchaseOrder:page",desc="PO订单主表分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<PurchaseOrderResp>> page(@RequestBody PurchaseOrderParam purchaseOrderParam) {
        return AjaxResult.success(purchaseOrderService.pagePurchaseOrder(purchaseOrderParam));
    }

    @Operation(summary = "PO订单主表详情信息")
    @ResourcePermission(value="web:po:purchaseOrder:get",desc="PO订单主表详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<PurchaseOrderResp> getPurchaseOrderById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderService.getPurchaseOrderById(id));
    }

    @Operation(summary = "PO订单主表新增/编辑")
    @ResourcePermission(value="web:po:purchaseOrder:addOrUpdate",desc="PO订单主表新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated PurchaseOrderAddOrUpdateReq purchaseOrderAddOrUpdateReq) {
        return AjaxResult.success(purchaseOrderService.addOrUpdatePurchaseOrder(purchaseOrderAddOrUpdateReq));
    }

    @Operation(summary = "PO订单主表删除")
    @ResourcePermission(value="web:po:purchaseOrder:delete",desc="PO订单主表删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        purchaseOrderService.deletePurchaseOrderByIds(ids);
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
        purchaseOrderService.enableOrDisablePurchaseOrderByIds(param);
        return AjaxResult.success();
    }

    @Operation(summary = "PO订单编辑保存(仅草稿态,改销售公司+明细生产备注)")
    @ResourcePermission(value="web:po:purchaseOrder:save",desc="PO订单编辑保存")
    @PostMapping("/save")
    public AjaxResult<Void> save(@RequestBody @Validated PurchaseOrderSaveReq req) {
        purchaseOrderService.savePurchaseOrder(req);
        return AjaxResult.success();
    }
}
