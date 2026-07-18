package com.leapmotor.export.vehicle.so.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SO 批量操作结果（下发生产 / 作废）：成功项不回滚，失败项汇总返回。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
@Schema(description = "SO 批量操作结果")
public class VehicleCbuSoOperateResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "成功数量")
    private int successCount;

    @Schema(description = "失败明细列表")
    private List<FailItem> failItems = new ArrayList<>();

    public void addFail(String soNo, String reason) {
        FailItem item = new FailItem();
        item.setSoNo(soNo);
        item.setReason(reason);
        failItems.add(item);
    }

    @Data
    @Schema(description = "失败明细")
    public static class FailItem implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "SO 单号")
        private String soNo;

        @Schema(description = "失败原因")
        private String reason;
    }
}
