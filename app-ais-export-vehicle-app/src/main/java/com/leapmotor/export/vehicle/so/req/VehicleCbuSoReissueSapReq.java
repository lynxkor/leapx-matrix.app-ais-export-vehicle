package com.leapmotor.export.vehicle.so.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * SO SAP 手动补推请求（支持单条 / 批量）。
 *
 * @author leapmotor
 * @since 2026-07-28
 */
@Data
@Schema(description = "SO SAP 手动补推请求")
public class VehicleCbuSoReissueSapReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "单条 SO 主键 ID")
    private String id;

    @Schema(description = "批量 SO 主键 ID 列表")
    private List<String> ids;
}
