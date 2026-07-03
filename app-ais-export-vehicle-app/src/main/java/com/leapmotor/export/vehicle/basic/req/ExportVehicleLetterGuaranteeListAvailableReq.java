package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 列出指定客户可用保函(供 PO/资金校验阶段选取)
 */
@Data
public class ExportVehicleLetterGuaranteeListAvailableReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @Schema(description = "订单类型(CBU/SKD/CKD)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderType;

    @Schema(description = "所需金额(可选;若填则只返可用金额 ≥ 此值的保函)")
    private BigDecimal requiredAmount;

    @Schema(description = "订单创建时间(可选;若填则只返失效日期 ≥ 订单创建时间 + coverDays 的保函,coverDays 取自 Nacos 配置 letterGuarantee.coverDays,默认 100)")
    private LocalDate orderCreateTime;
}
