package com.leapmotor.export.vehicle.so.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * SO 聚合详情响应对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
@Schema(description = "SO聚合详情：订单信息 + 车辆明细")
public class VehicleCbuSoDetailAggregateResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单信息")
    private VehicleCbuSoResp orderInfo;

    @Schema(description = "车辆明细信息（含同步快照）")
    private VehicleCbuSoDetailResp detailInfo;

}
