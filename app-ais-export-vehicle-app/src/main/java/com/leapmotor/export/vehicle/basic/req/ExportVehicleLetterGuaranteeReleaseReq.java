package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 释放应收(record_type=40,SAP 清账)
 */
@Data
public class ExportVehicleLetterGuaranteeReleaseReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgId;

    @Schema(description = "释放应收金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "SO单号")
    private String soNo;

    @Schema(description = "原因")
    private String reason;
}
