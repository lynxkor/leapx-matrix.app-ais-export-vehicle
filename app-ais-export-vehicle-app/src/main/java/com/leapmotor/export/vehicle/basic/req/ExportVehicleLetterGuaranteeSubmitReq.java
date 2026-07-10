package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 提交(草稿 10 / 驳回 40 → 已提交 20)
 *
 * <p>单条/批量统一入口:
 * <ul>
 *   <li>单条 — 传 {@code id}</li>
 *   <li>批量 — 传 {@code ids}</li>
 *   <li>两个字段二选一,不能同时提供</li>
 * </ul>
 *
 * <p>操作人(提交人 ID/名称)由 Gateway 层通过 {@code LoginUserUtils.getCurrentUserOrSysDefault()} 获取,无需 Req 传入。
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Data
public class ExportVehicleLetterGuaranteeSubmitReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID(单条场景),与 ids 互斥")
    private String id;

    @Schema(description = "保函ID列表(批量场景),与 id 互斥")
    private List<String> ids;
}
