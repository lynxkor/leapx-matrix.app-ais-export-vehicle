package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量冻结(PO 提交用)
 *
 * <p>服务端按优先级排序 + 逐步拆冻,任一失败整体回滚。
 * <p>候选保函可由调用方(校验通过后)显式传入;若不传则服务端自动按可用金额降序选 LGs。
 */
@Data
public class ExportVehicleLetterGuaranteeBatchFreezeReq implements Serializable {

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

    @Schema(description = "候选保函ID列表(校验通过后调用方传入;为空则自动按可用金额降序选)")
    private List<String> candidateLgIds;

    @Schema(description = "备注")
    private String remark;
}
