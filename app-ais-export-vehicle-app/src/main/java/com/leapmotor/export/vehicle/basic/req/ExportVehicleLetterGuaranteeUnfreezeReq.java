package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 解冻(占用 → 可用,record_type=20)
 */
@Data
public class ExportVehicleLetterGuaranteeUnfreezeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgId;

    @Schema(description = "解冻金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "原因")
    private String reason;
}
