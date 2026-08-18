package com.leapmotor.export.vehicle.basic.resp;

import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 单 PO 冻结响应(§8.5.3)
 *
 * <p>语义:all-or-nothing — 任一保函冻结失败立即整体回滚,只有全部保函冻结成功才返回 success=true。
 * 因此本响应不携带「失败明细」字段,失败信息由异常处理器以 failCode/failMessage 返回。
 */
@Data
public class ExportVehicleLetterGuaranteeFreezeResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否成功(任一保函冻结失败为 false,整体回滚)")
    private Boolean success;

    @Schema(description = "失败原因代码(批量 PO 场景中,仅当 success=false 时填充;单 PO 场景由异常处理器返回)")
    private String failCode;

    @Schema(description = "失败原因文案(i18n)")
    private String failMessage;

    @Schema(description = "PO 单号")
    private String poNo;

    @Schema(description = "实际冻结总金额(全成功时)")
    @BigDecimalFormat(pattern = "#.##")
    private BigDecimal totalFrozenAmount;

    @Schema(description = "成功冻结的明细(lgId, code, frozenAmount, poNo)")
    private List<FrozenItem> frozenItems;

    @Data
    public static class FrozenItem implements Serializable {
        private String lgId;
        private String lgCode;
        @BigDecimalFormat(pattern = "#.##")
        private BigDecimal frozenAmount;
        private String poNo;
    }
}