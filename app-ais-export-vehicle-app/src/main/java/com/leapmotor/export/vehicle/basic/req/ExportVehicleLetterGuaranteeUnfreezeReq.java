package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * PO 单批量解冻(record_type=20)
 *
 * <p>按 PO 单号查询所有冻结记录(transaction_type=10)并逐张全额解冻,不解冻单张保函。
 * 解冻金额取自冻结明细的 transactionAmount,保证与冻结金额一致。
 *
 * <p>整体事务 all-or-nothing:任一张保函解冻失败整体回滚。
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Data
public class ExportVehicleLetterGuaranteeUnfreezeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @Schema(description = "解冻原因")
    private String reason;
}