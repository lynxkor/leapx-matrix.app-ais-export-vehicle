package com.leapmotor.export.vehicle.basic.resp;

import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * PO 刷新价格-调减响应(§3.5.3)
 *
 * <p>部分解冻的响应:按失效日期倒序逐张解冻,直到累计解冻金额 = amountDiff;响应含所有被解冻保函的明细(可多张)。
 *
 * @author wangkai2527148
 * @since 2026-07-04
 */
@Data
public class ExportVehicleLetterGuaranteeAdjustUnfreezeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否成功(false=累计可用不足,已整体回滚)")
    private Boolean success;

    @Schema(description = "失败原因代码(失败时填充,如 LG_NO_ADJUST_LG)")
    private String failCode;

    @Schema(description = "失败原因文案(i18n)")
    private String failMessage;

    @Schema(description = "PO 单号")
    private String poNo;

    @Schema(description = "累计解冻金额(成功时 = amountDiff)")
    private BigDecimal totalUnfrozenAmount;

    @Schema(description = "成功解冻的明细(按失效日期倒序,可多张)")
    private List<UnfreezeItem> unfreezeItems;

    @Data
    public static class UnfreezeItem implements Serializable {
        private String lgId;
        private String lgCode;
        @BigDecimalFormat(pattern = "#.########")
        private BigDecimal unfrozenAmount;
        private String poNo;
    }
}
