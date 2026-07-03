package com.leapmotor.export.vehicle.basic.resp;

import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 批量冻结响应(§8.5.3)
 */
@Data
public class ExportVehicleLetterGuaranteeBatchFreezeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否成功(false 时任何一笔失败,整体回滚)")
    private Boolean success;

    @Schema(description = "失败原因代码")
    private String failCode;

    @Schema(description = "失败原因文案(i18n)")
    private String failMessage;

    @Schema(description = "实际冻结总金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal totalFrozenAmount;

    @Schema(description = "成功冻结的明细 (lgId, code, frozenAmount, poNo)")
    private List<FrozenItem> frozenItems;

    @Schema(description = "失败的明细(失败时填充)")
    private List<FailedItem> failedItems;

    @Data
    public static class FrozenItem implements Serializable {
        private String lgId;
        private String lgCode;
        @BigDecimalFormat(pattern = "#.########")
        private BigDecimal frozenAmount;
        private String poNo;
    }

    @Data
    public static class FailedItem implements Serializable {
        private String lgId;
        private String lgCode;
        @BigDecimalFormat(pattern = "#.########")
        private BigDecimal requiredAmount;
        @BigDecimalFormat(pattern = "#.########")
        private BigDecimal availableAmount;
        private String reason;
    }
}
