package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * PO 刷新价格-调减请求(§3.5.3)
 *
 * <p>价格下降后,部分解冻已冻结保函单张,amountDiff 为正数(代表需解冻的金额)。
 * 选保函规则:失效日期最晚 + PO 当前累计占用 > amountDiff(均不满足则降级)。
 *
 * <p>与现有 /unfreeze 区别:/unfreeze 是 PO 全解冻(逐张全额释放),本接口是部分解冻(只退差额)。
 *
 * @author wangkai2527148
 * @since 2026-07-04
 */
@Data
public class ExportVehicleLetterGuaranteeAdjustUnfreezeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @Schema(description = "需解冻的金额(>0 正数,代表新旧总金额差的绝对值)", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amountDiff;

    @Schema(description = "备注")
    private String reason;
}
