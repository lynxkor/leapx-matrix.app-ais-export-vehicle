package com.leapmotor.export.vehicle.so.resp;

import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * SO车辆明细表值对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoDetailResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态(1:启用,0=停用)")
    private Integer status;

    @Schema(description = "关联SO主表ID")
    private String soId;

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
