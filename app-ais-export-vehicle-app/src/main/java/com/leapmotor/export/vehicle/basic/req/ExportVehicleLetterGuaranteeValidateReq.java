package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PO 资金校验入参(§8.5.1)
 */
@Data
public class ExportVehicleLetterGuaranteeValidateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @Schema(description = "订单类型(CBU/SKD/CKD)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderType;

    @Schema(description = "订单总额(申请冻结金额)", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal requiredAmount;

    @Schema(description = "订单创建时间(用于校验失效日期 coverDays)", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate orderCreateTime;
}
