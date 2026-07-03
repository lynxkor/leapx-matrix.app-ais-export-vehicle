package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 单 PO 冻结(单个 PO 提交用,§8.5.3)
 *
 * <p>服务端自动按可用金额降序选取候选保函,逐步拆冻至覆盖 requiredAmount,
 * 任一保函冻结失败整体回滚。
 */
@Data
public class ExportVehicleLetterGuaranteeFreezeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @Schema(description = "订单类型(CBU/SKD/CKD)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderType;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNo;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "需要冻结的总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal requiredAmount;

    @Schema(description = "订单创建时间(可选;若填则只返失效日期 ≥ 订单创建时间 + coverDays 的保函,coverDays 取自 Nacos 配置 letterGuarantee.coverDays,默认 100)")
    private LocalDate orderCreateTime;

    @Schema(description = "备注")
    private String remark;
}