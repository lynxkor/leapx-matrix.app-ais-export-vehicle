package com.leapmotor.export.vehicle.controller.open.hongqi;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import com.leapmotor.export.vehicle.payablelist.dto.HqConfirmReq;
import com.leapmotor.export.vehicle.payablelist.executor.integration.HqConfirmProcessor;
import com.leapmotor.export.vehicle.payablelist.service.HongqiEpOpenService;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoService;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 红旗系统开放接口（红旗 → PO/SO，入站）。
 * 路径前缀 /openApi/hongqi，不走 @ResourcePermission（系统间调用）。
 * 鉴权/签名由上层网关统一处理，本接口不配签名拦截器。*
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Tag(name = "红旗系统开放接口", description = "红旗系统入站接口")
@RestController
@AllArgsConstructor
@RequestMapping("/openApi/hongqi")
public class HongqiOpenController {

    private ExportVehicleCbuPoService purchaseOrderService;
    private VehicleCbuSoService vehicleCbuSoService;
    private HqConfirmProcessor hqConfirmProcessor;
    private HongqiEpOpenService hongqiEpOpenService;

    /**
     * 红旗CBU订单采购申请 → 创建草稿PO订单（主表+明细联保存）
     */
    @Operation(summary = "红旗CBU订单采购申请-创建草稿订单")
    @PostMapping("/purchaseOrder/sync")
    public AjaxResult<String> createFromHongqi(@RequestBody @Valid HongqiCbuPurchaseOrderReq req) {
        return AjaxResult.success(purchaseOrderService.createFromHongqi(req));
    }

    /**
     * 接收红旗质检结果回传
     */
    @Operation(summary = "接收红旗质检结果回传")
    @PostMapping("/vehicleCbuSo/receiveQcResult")
    public AjaxResult<Void> receiveQcResult(@RequestBody VehicleCbuSoReceiveQcReq req) {
        vehicleCbuSoService.receiveQcResult(req);
        return AjaxResult.success();
    }

    /**
     * 红旗应付清单确认结果反馈
     */
    @Operation(summary = "应付清单红旗确认结果反馈")
    @PostMapping("/payableList/confirmCallback")
    public AjaxResult<Void> confirmCallback(@RequestBody HqConfirmReq req) {
        hqConfirmProcessor.process(req.getPayableListNo(), req.getStatus(), req.getOperateTime(), req.getRemark());
        return AjaxResult.success();
    }


    /**
     * 获取红旗 EP 访问令牌（供排查问题、联调使用）
     */
    @Operation(summary = "获取红旗EP token")
    @GetMapping("/getToken")
    public AjaxResult<Object> getToken() {
        return AjaxResult.success(hongqiEpOpenService.getToken());
    }

    /**
     * 同步应付清单到红旗 EP（先取 token 再调接收接口，供补推数据/排查问题使用）
     */
    @Operation(summary = "同步应付清单到红旗EP")
    @PostMapping("/payableList/sync")
    public AjaxResult<Object> syncPayableList(@RequestBody PayableListSyncHqRecordReq req) {
        return AjaxResult.success(hongqiEpOpenService.syncPayableList(req));
    }
}
