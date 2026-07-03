package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 单张冻结(可用 → 占用,record_type=10)
 */
@Data
public class ExportVehicleLetterGuaranteeFreezeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgId;

    @Schema(description = "冻结金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "SO单号(冻结时可不填)")
    private String soNo;

    @Schema(description = "VIN(冻结时可不填)")
    private String vin;

    @Schema(description = "备注")
    private String remark;
}
