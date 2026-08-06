package com.leapmotor.export.vehicle.payablelist.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量添加车辆请求
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class PayableListAddVehiclesReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应付清单ID")
    private String payableListId;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "SO订单ID列表")
    private List<String> soIds;
}