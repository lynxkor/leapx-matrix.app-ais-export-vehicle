package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 转应收(占用 → 应收,record_type=30)
 */
@Data
public class ExportVehicleLetterGuaranteeTransferReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgId;

    @Schema(description = "转应收金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "SO单号")
    private String soNo;

    @Schema(description = "VIN")
    private String vin;

    @Schema(description = "原因")
    private String reason;
}
