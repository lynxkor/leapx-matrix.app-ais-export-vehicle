package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PO 刷新价格-调增请求(§3.5.2)
 *
 * <p>价格上升后,补冻新旧总金额差 amountDiff。算法与 /freeze 相同(失效日期 ASC → 可用 DESC → 随机),
 * 仅入参语义差异(此处是要补的金额,不是 PO 总金额)。
 *
 * @author wangkai2527148
 * @since 2026-07-04
 */
@Data
public class ExportVehicleLetterGuaranteeAdjustFreezeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @Schema(description = "订单类型(CBU/SKD/CKD)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderType;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNo;

    @Schema(description = "PO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "新旧总金额差(>0,需补冻的金额)", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amountDiff;

    @Schema(description = "订单创建时间(可选;若填则只返失效日期 ≥ 订单创建时间 + coverDays 的保函,coverDays 取自 Nacos 配置 letterGuarantee.coverDays,默认 100)")
    private LocalDate orderCreateTime;

    @Schema(description = "备注")
    private String reason;
}
