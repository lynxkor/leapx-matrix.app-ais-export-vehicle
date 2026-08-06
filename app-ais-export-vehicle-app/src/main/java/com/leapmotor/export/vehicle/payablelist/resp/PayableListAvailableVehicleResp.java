package com.leapmotor.export.vehicle.payablelist.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 可添加车辆响应对象
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class PayableListAvailableVehicleResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "SO订单ID")
    private String soId;

    @Schema(description = "SO订单号")
    private String soNo;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "VIN号")
    private String vin;

    @Schema(description = "整车物料编码")
    private String materialCode;

    @Schema(description = "物料描述")
    private String materialDesc;

    @Schema(description = "车辆价格")
    private BigDecimal vehiclePrice;

    @Schema(description = "质检通过时间")
    private LocalDateTime qcPassTime;
}