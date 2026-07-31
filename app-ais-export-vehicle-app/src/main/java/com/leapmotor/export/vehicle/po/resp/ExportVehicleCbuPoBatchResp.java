package com.leapmotor.export.vehicle.po.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * PO 批量操作响应（每条独立事务，部分成功部分失败）。
 *
 * @author leapmotor
 * @since 2026-07-14
 */
@Data
@Schema(description = "批量操作响应")
public class ExportVehicleCbuPoBatchResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "总条数")
    private int total;

    @Schema(description = "成功条数")
    private int successCount;

    @Schema(description = "失败条数")
    private int failCount;

    @Schema(description = "是否被前置拦截（未执行任何操作）")
    private boolean blocked;

    @Schema(description = "失败明细")
    private List<FailItem> failItems = new ArrayList<>();

    @Data
    public static class FailItem implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(description = "PO订单ID")
        private String poId;

        @Schema(description = "失败原因")
        private String reason;
    }
}
