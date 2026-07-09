package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 红旗 CBU 订单采购申请入参（入站接口：红旗 → PO）。
 * 契约待红旗提供，字段先按技术评审 01 节点 §三 接口字段定义。
 */
@Data
@Schema(description = "红旗CBU订单采购申请")
public class HongqiCbuPurchaseOrderReq {

    @Schema(description = "PO单号(全局唯一)")
    @NotBlank(message = "PO单号不能为空")
    @Size(max = 30, message = "PO单号长度不能超过30")
    private String poNo;

    @Schema(description = "客户编码(HQ01红旗股份/HQ02红旗进出口),即外部客户编码")
    @NotBlank(message = "客户编码不能为空")
    @Size(max = 30)
    private String customerCode;

    @Schema(description = "计划版本号,例 20260305V1")
    @NotBlank(message = "计划版本号不能为空")
    @Size(max = 30)
    private String planVersion;

    @Schema(description = "订单明细列表")
    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<HongqiCbuOrderDetailReq> orderDetailList;
}
