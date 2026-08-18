package com.leapmotor.export.vehicle.controller.app.basic;

import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAdjustFreezeReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAdjustUnfreezeReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeFreezeReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeReleaseReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeTransferReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeUnfreezeReq;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeAdjustUnfreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeFreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeUnfreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeValidateResp;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 保函管理 内部 HTTP 控制器(供 PO/SO 内部调用,非 Feign)
 *
 * <p>路径前缀 /app/basic/exportVehicleLetterGuarantee,所有接口由 PO/SO 模块经
 * {@code RestTemplate} 或内部 HTTP 调用(不走 Feign,降低服务发现耦合)。
 *
 * <p>主要职责:
 * <ul>
 *   <li>{@code /validate} — PO 提交步骤 3:校验客户可用保函是否覆盖所需金额(§8.5.1)</li>
 *   <li>{@code /freeze} — 单 PO 冻结:单个 PO 提交,服务端自动选保函逐步拆冻(§8.5.3)</li>
 *   <li>{@code /batchFreeze} — 批量 PO 冻结:多个 PO 提交,每个 PO 独立事务(§8.5.3)</li>
 *   <li>{@code /unfreeze} — PO 驳回:解冻占用金额(§8.5.1)</li>
 *   <li>{@code /transferToReceivable} — 质检通过(§8.5.1)</li>
 *   <li>{@code /releaseReceivable} — SAP 清账(§8.5.1)</li>
 *   <li>{@code /adjustFreeze} — PO 刷新价格-调增:补冻金额差(§3.5.2)</li>
 *   <li>{@code /adjustUnfreeze} — PO 刷新价格-调减:退差金额差(§3.5.3)</li>
 * </ul>
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Tag(name = "保函管理 内部接口", description = "供 PO/SO 模块内部 HTTP 调用的保函接口")
@RestController
@AllArgsConstructor
@RequestMapping("/app/basic/exportVehicleLetterGuarantee")
public class ExportVehicleLetterGuaranteeAppController {

    private ExportVehicleLetterGuaranteeService exportVehicleLetterGuaranteeService;

    @Operation(summary = "资金校验 — PO 提交前置步骤")
    @PostMapping("/validate")
    public AjaxResult<ExportVehicleLetterGuaranteeValidateResp> validate(@RequestBody ExportVehicleLetterGuaranteeFreezeReq req) {
        ExportVehicleLetterGuaranteeValidateResp resp = exportVehicleLetterGuaranteeService.validateForPo(req);
        return AjaxResult.success(resp);
    }

    @Operation(summary = "单 PO 冻结 — 单个 PO 提交,服务端自动选保函逐步拆冻")
    @PostMapping("/freeze")
    public AjaxResult<ExportVehicleLetterGuaranteeFreezeResp> freeze(@RequestBody ExportVehicleLetterGuaranteeFreezeReq req) {
        ExportVehicleLetterGuaranteeFreezeResp resp = exportVehicleLetterGuaranteeService.freeze(req);
        return AjaxResult.success(resp);
    }

    @Operation(summary = "批量 PO 冻结 — 多个 PO 提交,每个 PO 独立事务")
    @PostMapping("/batchFreeze")
    public AjaxResult<List<ExportVehicleLetterGuaranteeFreezeResp>> batchFreeze(@RequestBody List<ExportVehicleLetterGuaranteeFreezeReq> list) {
        List<ExportVehicleLetterGuaranteeFreezeResp> resp = exportVehicleLetterGuaranteeService.batchFreeze(list);
        return AjaxResult.success(resp);
    }


    @Operation(summary = "解冻 — PO 驳回")
    @PostMapping("/unfreeze")
    public AjaxResult<Void> unfreeze(@RequestBody ExportVehicleLetterGuaranteeUnfreezeReq req) {
        exportVehicleLetterGuaranteeService.unfreeze(req);
        return AjaxResult.success();
    }

    @Operation(summary = "批量 PO 解冻 — 多个 PO 驳回,每个 PO 独立事务")
    @PostMapping("/batchUnfreeze")
    public AjaxResult<List<ExportVehicleLetterGuaranteeUnfreezeResp>> batchUnfreeze(@RequestBody List<ExportVehicleLetterGuaranteeUnfreezeReq> list) {
        List<ExportVehicleLetterGuaranteeUnfreezeResp> resp = exportVehicleLetterGuaranteeService.batchUnfreeze(list);
        return AjaxResult.success(resp);
    }

    @Operation(summary = "转应收 — 质检通过")
    @PostMapping("/transferToReceivable")
    public AjaxResult<Void> transferToReceivable(@RequestBody ExportVehicleLetterGuaranteeTransferReq req) {
        exportVehicleLetterGuaranteeService.transferToReceivable(req);
        return AjaxResult.success();
    }

    @Operation(summary = "应收释放 — SAP 清账")
    @PostMapping("/releaseReceivable")
    public AjaxResult<Void> releaseReceivable(@RequestBody ExportVehicleLetterGuaranteeReleaseReq req) {
        exportVehicleLetterGuaranteeService.releaseReceivable(req);
        return AjaxResult.success();
    }

    @Operation(summary = "调增补冻 — PO 刷新价格,新总金额上升")
    @PostMapping("/adjustFreeze")
    public AjaxResult<ExportVehicleLetterGuaranteeFreezeResp> adjustFreeze(@RequestBody ExportVehicleLetterGuaranteeAdjustFreezeReq req) {
        ExportVehicleLetterGuaranteeFreezeResp resp = exportVehicleLetterGuaranteeService.adjustFreeze(req);
        return AjaxResult.success(resp);
    }

    @Operation(summary = "调减退差 — PO 刷新价格,新总金额下降")
    @PostMapping("/adjustUnfreeze")
    public AjaxResult<ExportVehicleLetterGuaranteeAdjustUnfreezeResp> adjustUnfreeze(@RequestBody ExportVehicleLetterGuaranteeAdjustUnfreezeReq req) {
        ExportVehicleLetterGuaranteeAdjustUnfreezeResp resp = exportVehicleLetterGuaranteeService.adjustUnfreeze(req);
        return AjaxResult.success(resp);
    }
}
