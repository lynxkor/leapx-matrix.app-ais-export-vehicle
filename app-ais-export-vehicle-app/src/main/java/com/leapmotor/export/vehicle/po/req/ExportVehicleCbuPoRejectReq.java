package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 4.3 销售驳回入参
 */
@Data
@Schema(description = "PO订单销售驳回")
public class ExportVehicleCbuPoRejectReq {

    @Schema(description = "PO订单主键ID")
    @NotBlank(message = "PO订单ID不能为空")
    private String poId;

    @Schema(description = "驳回说明(≤100字)")
    @NotBlank(message = "驳回说明不能为空")
    @Size(max = 100, message = "驳回说明长度不能超过100")
    private String rejectReason;
}
