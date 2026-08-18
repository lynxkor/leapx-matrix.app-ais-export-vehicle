package com.leapmotor.export.vehicle.payablelist.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 导入价格失败项
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class ImportPriceFailItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "行号")
    private Integer rowNum;

    @Schema(description = "VIN号")
    private String vin;

    @Schema(description = "车辆价格")
    private BigDecimal price;

    @Schema(description = "失败原因")
    private String failReason;
}