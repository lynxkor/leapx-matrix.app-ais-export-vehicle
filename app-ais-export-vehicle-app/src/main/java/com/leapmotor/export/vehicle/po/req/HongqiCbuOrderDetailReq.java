package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 红旗 CBU 订单明细入参。
 *
 * <p>字段校验由 {@link com.leapmotor.export.vehicle.po.validator.HongqiCbuPurchaseOrderValidator}
 * 统一手写收集，不再依赖 jakarta validation 注解（以便一次性返回全部错误且 message 可控）。
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
@Schema(description = "红旗CBU订单明细")
public class HongqiCbuOrderDetailReq {

    @Schema(description = "订单明细编号，同一PO内唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detailNumber;

    @Schema(description = "18位零跑物料编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String materialCode;

    @Schema(description = "需求数量", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal demandQuantity;

    @Schema(description = "VIN码年份，格式YYYY")
    private String vinYear;

    @Schema(description = "期望下线日期，格式YYYYMMDD", requiredMode = Schema.RequiredMode.REQUIRED)
    private String expectedOfflineDate;
}