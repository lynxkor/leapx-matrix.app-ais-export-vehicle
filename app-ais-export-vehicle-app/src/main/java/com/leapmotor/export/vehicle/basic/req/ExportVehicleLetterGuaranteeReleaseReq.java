package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * PO 单批量释放应收(record_type=40,SAP 清账)
 *
 * <p>SAP 完成清账后回调,按 PO 单批量处理:服务端从转应收明细(transaction_type=30)自动汇总
 * 该 PO 在各保函上的应收金额,按保函逐张释放并各写一笔释放应收流水,任一保函释放失败整体回滚。
 *
 * <p>输入是 SO 单粒度(soNo)— 但保函侧只需要 PO 单号即可定位转应收明细,因此保函接口只接收 poNo。
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Data
public class ExportVehicleLetterGuaranteeReleaseReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "SO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String soNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @Schema(description = "原因/SAP清账单号等说明")
    private String reason;
}