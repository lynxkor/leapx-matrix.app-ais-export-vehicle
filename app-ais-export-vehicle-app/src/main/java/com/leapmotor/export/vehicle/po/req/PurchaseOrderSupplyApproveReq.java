package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 4.4 供需审核通过入参
 */
@Data
@Schema(description = "PO订单供需审核通过")
public class PurchaseOrderSupplyApproveReq {

    @Schema(description = "PO订单主键ID")
    @NotBlank(message = "PO订单ID不能为空")
    private String poId;
}
