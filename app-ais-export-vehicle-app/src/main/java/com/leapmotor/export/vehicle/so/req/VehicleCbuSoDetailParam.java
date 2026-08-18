package com.leapmotor.export.vehicle.so.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * SO车辆明细表参数对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoDetailParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联SO单号")
    private String soNo;

    @Schema(description = "内部车辆号，从APS获取")
    private String internalVehicleNo;

    @Schema(description = "产销识别码，从APS获取")
    private String productionIdentifyCode;

    @Schema(description = "VIN号，从APS获取")
    private String vin;

    @Schema(description = "物料编码，取自PO单")
    private String materialCode;

    @Schema(description = "物料描述")
    private String materialDescription;

    @Schema(description = "销售市场")
    private String salesMarket;

    @Schema(description = "年款")
    private String modelYear;

    @Schema(description = "选装包")
    private String optionalPackage;

    @Schema(description = "排产时间，从APS获取")
    private LocalDateTime scheduleTime;

    @Schema(description = "打印条码时间，从APS获取")
    private LocalDateTime barcodePrintTime;

    @Schema(description = "焊装上线时间，从APS获取")
    private LocalDateTime bodyInTime;

    @Schema(description = "涂装上线时间，从APS获取")
    private LocalDateTime paintInTime;

    @Schema(description = "总装上线时间，从APS获取")
    private LocalDateTime assemblyInTime;

    @Schema(description = "总装下线时间，从APS获取")
    private LocalDateTime assemblyOutTime;

    @Schema(description = "备注")
    private String remark;


}
