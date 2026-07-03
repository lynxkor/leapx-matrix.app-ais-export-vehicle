package com.leapmotor.export.vehicle.basic.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 作废(草稿/驳回 → 作废)
 *
 * <p>单条/批量统一入口:
 * <ul>
 *   <li>单条 — 传 {@code id}</li>
 *   <li>批量 — 传 {@code ids}</li>
 *   <li>两个字段二选一,不能同时提供</li>
 * </ul>
 *
 * <p>操作人无需 Req 传入:作废主表无操作人字段,LpBaseModel.modifier 由 MyBatis-Plus 自动填充。
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Data
public class ExportVehicleLetterGuaranteeCancelReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函ID(单条场景),与 ids 互斥")
    private String id;

    @Schema(description = "保函ID列表(批量场景),与 id 互斥")
    private List<String> ids;

    @Schema(description = "作废原因(适用于全部 ids)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;
}
