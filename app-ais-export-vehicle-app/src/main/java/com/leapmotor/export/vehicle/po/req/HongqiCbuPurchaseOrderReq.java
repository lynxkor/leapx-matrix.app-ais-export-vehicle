package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
@Schema(description = "红旗CBU订单采购申请")
public class HongqiCbuPurchaseOrderReq {

    @Schema(description = "PO单号，规则 HQ+YYYYMMDD+6位流水号，全局唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "PO单号不能为空")
    @Size(max = 30, message = "PO单号长度不能超过30")
    @Pattern(regexp = "^HQ\\d{8}\\d{6}$", message = "PO单号格式错误，规则 HQ+YYYYMMDD+6位流水号")
    private String poNo;

    @Schema(description = "客户编码：HQ01=红旗股份 / HQ02=红旗进出口公司", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户编码不能为空")
    @Size(max = 30)
    @Pattern(regexp = "^HQ0[12]$", message = "客户编码仅支持 HQ01(红旗股份) 或 HQ02(红旗进出口公司)")
    private String customerCode;

    @Schema(description = "计划版本号，例 20260305V1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "计划版本号不能为空")
    @Size(max = 30)
    @Pattern(regexp = "^\\d{8}V\\d+$", message = "计划版本号格式错误，例 20260305V1")
    private String planVersion;

    @Schema(description = "订单明细列表")
    @NotEmpty(message = "订单明细不能为空")
    @Valid
    private List<HongqiCbuOrderDetailReq> orderDetailList;
}
