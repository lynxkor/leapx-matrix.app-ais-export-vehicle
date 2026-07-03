package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * PO 单批量转应收(record_type=30,占用 → 应收)
 *
 * <p>质检通过触发,按 PO 单批量处理:服务端从冻结明细自动汇总该 PO 在各保函上的累计占用金额,
 * 按业务规则排序后逐张转应收,任一保函转应收失败整体回滚。
 *
 * <p>输入是 SO 单粒度(soNo + frozenAmount),系统基于 SO 对应 PO 订单的【关联保函信息】完成处理,
 * 因此不需要 lgId 和单张金额 — 保函选取与单张金额由服务端自动计算。
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Data
public class ExportVehicleLetterGuaranteeTransferReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "SO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String soNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderId;

    @Schema(description = "SO单冻结金额(本 SO 单需转应收的总金额)", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal frozenAmount;

    @Schema(description = "VIN(车辆识别码)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vin;

    @Schema(description = "原因/说明")
    private String reason;
}