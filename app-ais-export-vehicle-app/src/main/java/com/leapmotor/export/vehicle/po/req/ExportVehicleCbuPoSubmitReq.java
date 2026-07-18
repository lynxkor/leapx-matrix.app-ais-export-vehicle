package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 销售提交入参
 */
@Data
@Schema(description = "PO订单销售提交")
public class ExportVehicleCbuPoSubmitReq {

    @Schema(description = "PO订单主键ID")
    @NotBlank(message = "PO订单ID不能为空")
    private String poId;
}
