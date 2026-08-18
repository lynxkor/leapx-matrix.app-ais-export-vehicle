package com.leapmotor.export.vehicle.so.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * SO 作废请求（支持单条 / 批量 / 按 PO 三种模式）。
 *
 * <p>管理端手动作废传 id / ids；PO 侧按 PO 批量作废传 poNo；reason 作废原因必填。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
@Schema(description = "SO 作废请求")
public class VehicleCbuSoCancelReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "单条 SO 主键 ID")
    private String id;

    @Schema(description = "批量 SO 主键 ID 列表")
    private List<String> ids;

    @Schema(description = "PO 单号（PO 侧按 PO 批量作废时传入）")
    private String poNo;

    @NotBlank(message = "作废原因不能为空")
    @Schema(description = "作废原因（必填）")
    private String reason;
}
