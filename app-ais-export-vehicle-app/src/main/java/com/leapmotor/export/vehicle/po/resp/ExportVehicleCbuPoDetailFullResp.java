package com.leapmotor.export.vehicle.po.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * PO订单详情（主表 + 明细列表 + 保函关联列表）。
 *
 * @author leapmotor
 * @since 2026-07-13
 */
@Data
public class ExportVehicleCbuPoDetailFullResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO订单主表信息")
    private ExportVehicleCbuPoResp order;

    @Schema(description = "明细列表")
    private List<ExportVehicleCbuPoDetailResp> details;

    @Schema(description = "保函关联列表")
    private List<ExportVehicleCbuPoGuaranteeResp> guarantees;
}
