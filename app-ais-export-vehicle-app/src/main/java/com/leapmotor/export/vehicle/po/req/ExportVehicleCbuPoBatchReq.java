package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * PO 批量操作请求（批量提交/驳回/审核，整体事务全成功或全回滚）。
 *
 * @author leapmotor
 * @since 2026-07-14
 */
@Data
@Schema(description = "PO批量操作请求")
public class ExportVehicleCbuPoBatchReq {

    @Schema(description = "PO订单ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "请选择要操作的订单")
    private List<String> poIds;

    @Schema(description = "驳回说明（驳回类操作必填）")
    @Size(max = 100)
    private String rejectReason;
}
