package com.leapmotor.export.vehicle.po.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 刷新价格结果（供前端弹窗展示变更摘要）。
 */
@Data
@Schema(description = "刷新价格结果")
public class ExportVehicleCbuPoRefreshPriceResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "原总金额")
    private BigDecimal oldTotal;

    @Schema(description = "新总金额")
    private BigDecimal newTotal;

    @Schema(description = "更新的明细行数")
    private int updatedDetailCount;

    @Schema(description = "未匹配到价格的明细行数")
    private int unmatchedDetailCount;

    @Schema(description = "保函变动说明（无调整/解冻xxx/补冻xxx）")
    private String guaranteeAction;

    @Schema(description = "提示信息")
    private String message;
}
