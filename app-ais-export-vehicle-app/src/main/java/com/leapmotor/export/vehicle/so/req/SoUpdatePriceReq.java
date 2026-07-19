package com.leapmotor.export.vehicle.so.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * SO 价格更新入参（PO 刷新价格联动 SO 车辆价/冻结金额）。
 */
@Data
@Schema(description = "SO 价格更新入参")
public class SoUpdatePriceReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号")
    @NotBlank(message = "PO单号不能为空")
    private String poNo;

    @Schema(description = "明细新价格(按PO明细ID匹配SO)")
    @NotEmpty(message = "明细价格不能为空")
    private List<DetailPrice> details;

    @Data
    @Schema(description = "明细新价格")
    public static class DetailPrice implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "PO明细ID")
        @NotBlank(message = "PO明细ID不能为空")
        private String poDetailId;

        @Schema(description = "新单价")
        private BigDecimal unitPrice;
    }
}
