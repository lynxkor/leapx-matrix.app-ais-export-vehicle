package com.leapmotor.export.vehicle.basic.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量状态机操作响应 — 提交/审核/作废
 *
 * <p>与 {@link ExportVehicleLetterGuaranteeFreezeResp} 不同:
 * 状态机批量每条独立事务,1 条失败不影响其他条,故响应需逐条返回结果。
 * 资金批量(batchFreeze)整体回滚,任一失败统一 failCode/failMessage。
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Data
public class ExportVehicleLetterGuaranteeBatchStateMachineResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "提交总数")
    private Integer total;

    @Schema(description = "成功条数")
    private Integer successCount;

    @Schema(description = "失败条数")
    private Integer failureCount;

    @Schema(description = "全部成功(用于前端快速判断)")
    private Boolean allSuccess;

    @Schema(description = "每条明细结果(顺序与请求 ids 一致)")
    private List<ItemResult> items;

    @Data
    public static class ItemResult implements Serializable {

        @Schema(description = "保函ID")
        private String id;

        @Schema(description = "保函编号")
        private String lgCode;

        @Schema(description = "本条是否成功")
        private Boolean success;

        @Schema(description = "失败代码(成功时为空)")
        private Integer errorCode;

        @Schema(description = "失败文案(i18n key 或直接文案)")
        private String errorMessage;
    }
}
