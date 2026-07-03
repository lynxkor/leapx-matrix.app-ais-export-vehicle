package com.leapmotor.export.vehicle.controller.app.basic;

import com.leapmotor.export.vehicle.basic.req.*;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeBatchFreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeValidateResp;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.platform.ais.common.permission.annotation.ResourcePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 保函管理 内部 HTTP 控制器(供 PO/SO 内部调用,非 Feign)
 *
 * <p>路径前缀 /app/basic/exportVehicleLetterGuarantee,所有接口由 PO/SO 模块经
 * {@code RestTemplate} 或内部 HTTP 调用(不走 Feign,降低服务发现耦合)。
 *
 * <p>主要职责:
 * <ul>
 *   <li>{@code /validate} — PO 提交步骤 3:校验客户可用保函是否覆盖所需金额(§8.5.1)</li>
 *   <li>{@code /batchFreeze} — PO 提交步骤 4:批量冻结(整体事务,任一失败回滚)(§8.5.3)</li>
 *   <li>{@code /unfreeze} — PO 驳回:解冻占用金额(§8.5.1)</li>
 *   <li>{@code /transferToReceivable} — 质检通过(§8.5.1)</li>
 *   <li>{@code /releaseReceivable} — SAP 清账(§8.5.1)</li>
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
    @ResourcePermission(value = "app:basic:exportVehicleLetterGuarantee:validate", desc = "PO 资金校验")
    @PostMapping("/validate")
    public AjaxResult<ExportVehicleLetterGuaranteeValidateResp> validate(@RequestBody ExportVehicleLetterGuaranteeValidateReq req) {
        ExportVehicleLetterGuaranteeValidateResp resp = exportVehicleLetterGuaranteeService.validateForPo(req);
        return AjaxResult.success(resp);
    }

    @Operation(summary = "批量冻结 — PO 提交步骤 4,任一失败整体回滚")
    @ResourcePermission(value = "app:basic:exportVehicleLetterGuarantee:batchFreeze", desc = "PO 批量冻结")
    @PostMapping("/batchFreeze")
    public AjaxResult<ExportVehicleLetterGuaranteeBatchFreezeResp> batchFreeze(@RequestBody ExportVehicleLetterGuaranteeBatchFreezeReq req) {
        ExportVehicleLetterGuaranteeBatchFreezeResp resp = exportVehicleLetterGuaranteeService.batchFreeze(req);
        return AjaxResult.success(resp);
    }

    @Operation(summary = "解冻 — PO 驳回")
    @ResourcePermission(value = "app:basic:exportVehicleLetterGuarantee:unfreeze", desc = "PO 解冻")
    @PostMapping("/unfreeze")
    public AjaxResult<Void> unfreeze(@RequestBody ExportVehicleLetterGuaranteeUnfreezeReq req) {
        exportVehicleLetterGuaranteeService.unfreeze(req);
        return AjaxResult.success();
    }

    @Operation(summary = "转应收 — 质检通过")
    @ResourcePermission(value = "app:basic:exportVehicleLetterGuarantee:transferToReceivable", desc = "质检转应收")
    @PostMapping("/transferToReceivable")
    public AjaxResult<Void> transferToReceivable(@RequestBody ExportVehicleLetterGuaranteeTransferReq req) {
        exportVehicleLetterGuaranteeService.transferToReceivable(req);
        return AjaxResult.success();
    }

    @Operation(summary = "释放应收 — SAP 清账")
    @ResourcePermission(value = "app:basic:exportVehicleLetterGuarantee:releaseReceivable", desc = "SAP 清账")
    @PostMapping("/releaseReceivable")
    public AjaxResult<Void> releaseReceivable(@RequestBody ExportVehicleLetterGuaranteeReleaseReq req) {
        exportVehicleLetterGuaranteeService.releaseReceivable(req);
        return AjaxResult.success();
    }
}
