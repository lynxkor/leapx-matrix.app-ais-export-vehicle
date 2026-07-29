package com.leapmotor.export.vehicle.basic.resp;

import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 保函转应收变动明细（质检通过触发，占用 -> 应收）
 *
 * <p>transferToReceivable 按失效日期/占用金额排序逐张扣减后返回，供调用方回写 PO 关联保函表：
 * poCurrentOccupiedAmount -= transferAmount，poCurrentReceivableAmount += transferAmount。
 */
@Data
public class ExportVehicleLetterGuaranteeTransferItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID")
    private String lgId;

    @Schema(description = "保函编号")
    private String lgCode;

    @Schema(description = "本次转应收变动金额")
    @BigDecimalFormat(pattern = "#.##")
    private BigDecimal transferAmount;

    @Schema(description = "PO 单号")
    private String poNo;
}
