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
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeParam;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderGuaranteeService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
/**
 * PO-保函关联表(一个PO多保函) PC端接口
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "PC端 PO-保函关联表(一个PO多保函)管理" , description = "PO-保函关联表(一个PO多保函)的API接口文档")
@RestController
@AllArgsConstructor
@RequestMapping("/web/po/purchaseOrderGuarantee")
public class PurchaseOrderGuaranteeController {

    private PurchaseOrderGuaranteeService purchaseOrderGuaranteeService;

    @Operation(summary = "PO-保函关联表(一个PO多保函)列表查询")
    @ResourcePermission(value="web:po:purchaseOrderGuarantee:list",desc="PO-保函关联表(一个PO多保函)列表查询")
    @PostMapping("/list")
    public AjaxResult<List<LpOptionalResult>> list(@RequestBody PurchaseOrderGuaranteeParam purchaseOrderGuaranteeParam) {
        List<PurchaseOrderGuaranteeResp> purchaseOrderGuaranteeRespList =purchaseOrderGuaranteeService.listPurchaseOrderGuarantee(purchaseOrderGuaranteeParam);
        return AjaxResult.success(PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.domainsToOptionals(purchaseOrderGuaranteeRespList));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)分页查询")
    @ResourcePermission(value="web:po:purchaseOrderGuarantee:page",desc="PO-保函关联表(一个PO多保函)分页查询")
    @PostMapping("/page")
    public AjaxResult<PageResult<PurchaseOrderGuaranteeResp>> page(@RequestBody PurchaseOrderGuaranteeParam purchaseOrderGuaranteeParam) {
        return AjaxResult.success(purchaseOrderGuaranteeService.pagePurchaseOrderGuarantee(purchaseOrderGuaranteeParam));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)详情信息")
    @ResourcePermission(value="web:po:purchaseOrderGuarantee:get",desc="PO-保函关联表(一个PO多保函)详情信息")
    @PostMapping(value = "/get")
    public AjaxResult<PurchaseOrderGuaranteeResp> getPurchaseOrderGuaranteeById(@RequestBody String id) {
        return AjaxResult.success(purchaseOrderGuaranteeService.getPurchaseOrderGuaranteeById(id));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)新增/编辑")
    @ResourcePermission(value="web:po:purchaseOrderGuarantee:addOrUpdate",desc="PO-保函关联表(一个PO多保函)新增/编辑")
    @PostMapping("/addOrUpdate")
    public AjaxResult<String> addOrUpdate(@RequestBody @Validated PurchaseOrderGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq) {
        return AjaxResult.success(purchaseOrderGuaranteeService.addOrUpdatePurchaseOrderGuarantee(purchaseOrderGuaranteeAddOrUpdateReq));
    }

    @Operation(summary = "PO-保函关联表(一个PO多保函)删除")
    @ResourcePermission(value="web:po:purchaseOrderGuarantee:delete",desc="PO-保函关联表(一个PO多保函)删除")
    @PostMapping("/delete")
    public AjaxResult<Void> deleteByIds(@RequestBody List<String> ids) {
        purchaseOrderGuaranteeService.deletePurchaseOrderGuaranteeByIds(ids);
        return AjaxResult.success();
    }


    @Operation(summary = "PO-保函关联表(一个PO多保函)启用/禁用")
    @ResourcePermission(value="web:po:purchaseOrderGuarantee:enableOrDisable",desc="PO-保函关联表(一个PO多保函)启用/禁用")
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
        purchaseOrderGuaranteeService.enableOrDisablePurchaseOrderGuaranteeByIds(param);
        return AjaxResult.success();
    }
}
