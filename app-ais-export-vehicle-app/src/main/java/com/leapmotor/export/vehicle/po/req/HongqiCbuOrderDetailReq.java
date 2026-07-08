package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 红旗 CBU 订单明细入参。
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
@Schema(description = "红旗CBU订单明细")
public class HongqiCbuOrderDetailReq {

    @Schema(description = "订单明细编号，同一PO内唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "订单明细编号不能为空")
    @Size(max = 30)
    private String detailNumber;

    @Schema(description = "18位零跑物料编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "物料编码不能为空")
    @Size(min = 18, max = 18, message = "物料编码必须为18位")
    private String materialCode;

    @Schema(description = "需求数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "需求数量不能为空")
    @Positive(message = "需求数量必须大于0")
    private BigDecimal demandQuantity;

    @Schema(description = "VIN码年份，格式YYYY")
    @Size(max = 10)
    @Pattern(regexp = "^(|\\d{4})$", message = "VIN码年份格式错误，应为YYYY")
    private String vinYear;

    @Schema(description = "期望下线日期，格式YYYYMMDD", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "期望下线日期不能为空")
    @Size(max = 10)
    @Pattern(regexp = "^\\d{8}$", message = "期望下线日期格式错误，应为YYYYMMDD")
    private String expectedOfflineDate;
}
