package com.leapmotor.export.vehicle.basic.resp;

import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可用保函响应
 */
@Data
public class ExportVehicleLetterGuaranteeListAvailableResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID")
    private String lgId;

    @Schema(description = "保函编号")
    private String lgCode;

    @Schema(description = "保函类型")
    private String lgType;

    @Schema(description = "保函总金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal totalAmount;

    @Schema(description = "换算订单额度比例")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal conversionRatio;

    @Schema(description = "可用订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal availableAmount;

    @Schema(description = "生效日期")
    private java.time.LocalDate effectiveDate;

    @Schema(description = "失效日期")
    private java.time.LocalDate expiryDate;
}
