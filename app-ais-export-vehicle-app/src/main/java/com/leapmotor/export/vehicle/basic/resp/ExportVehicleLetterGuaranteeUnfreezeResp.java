package com.leapmotor.export.vehicle.basic.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 批量 PO 解冻响应 — 单 PO 解冻结果
 *
 * <p>解冻按 PO 单批量执行,每 PO 独立事务,响应中逐条返回成功/失败。
 * 仅含 success/failCode/failMessage/poNo 字段(不含 totalFrozenAmount/frozenItems 等冻结相关字段)。
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Data
public class ExportVehicleLetterGuaranteeUnfreezeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否成功(任一保函解冻失败为 false,该 PO 整体回滚)")
    private Boolean success;

    @Schema(description = "失败原因代码")
    private String failCode;

    @Schema(description = "失败原因文案(i18n)")
    private String failMessage;

    @Schema(description = "PO 单号")
    private String poNo;
}