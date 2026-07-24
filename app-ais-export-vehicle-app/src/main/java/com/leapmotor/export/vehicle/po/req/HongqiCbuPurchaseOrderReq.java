package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 红旗 CBU 订单采购申请入参（入站接口：红旗 → PO）。
 *
 * <ul>
 *   <li>PO单号规则：HQ + YYYYMMDD + 6位流水号，全局唯一</li>
 *   <li>客户编码：HQ01=红旗股份 / HQ02=红旗进出口公司</li>
 * </ul>
 *
 * <p>字段校验由 {@link com.leapmotor.export.vehicle.po.validator.HongqiCbuPurchaseOrderValidator}
 * 统一手写收集，不再依赖 jakarta validation 注解。
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
@Schema(description = "红旗CBU订单采购申请")
public class HongqiCbuPurchaseOrderReq {

    @Schema(description = "PO单号，规则 HQ+YYYYMMDD+6位流水号，全局唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "客户编码：HQ01=红旗股份 / HQ02=红旗进出口公司", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerCode;

    @Schema(description = "计划版本号，例 20260305V1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String planVersion;

    @Schema(description = "订单明细列表")
    private List<HongqiCbuOrderDetailReq> orderDetailList;
}