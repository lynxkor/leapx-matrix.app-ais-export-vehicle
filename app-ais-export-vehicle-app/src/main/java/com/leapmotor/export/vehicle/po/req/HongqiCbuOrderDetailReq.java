package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 红旗 CBU 订单明细入参。
 */
@Data
@Schema(description = "红旗CBU订单明细")
public class HongqiCbuOrderDetailReq {

    @Schema(description = "订单明细编号(同一PO内唯一)")
    @NotBlank(message = "订单明细编号不能为空")
    @Size(max = 30)
    private String detailNumber;

    @Schema(description = "18位零跑物料编码(同步物料编码)")
    @NotBlank(message = "物料编码不能为空")
    @Size(max = 18)
    private String materialCode;

    @Schema(description = "需求数量")
    @NotBlank(message = "需求数量不能为空")
    private BigDecimal demandQuantity;

    @Schema(description = "VIN码年份(YYYY)")
    @Size(max = 10)
    private String vinYear;

    @Schema(description = "期望下线日期(YYYYMMDD)")
    @NotBlank(message = "期望下线日期不能为空")
    @Size(max = 10)
    private String expectedOfflineDate;
}
