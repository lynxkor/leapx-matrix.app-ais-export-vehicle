package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * PO 刷新价格请求。
 *
 * @author leapmotor
 * @since 2026-07-14
 */
@Data
@Schema(description = "刷新价格请求")
public class ExportVehicleCbuPoRefreshPriceReq {

    @Schema(description = "PO订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "PO订单ID不能为空")
    private String poId;
}
