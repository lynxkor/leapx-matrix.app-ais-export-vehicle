package com.leapmotor.export.vehicle.so.req;

import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * SO车辆明细表新增/编辑请求对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoDetailAddOrUpdateReq extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联SO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String soNo;

    @Schema(description = "内部车辆号，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private String internalVehicleNo;

    @Schema(description = "产销识别码，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productionIdentifyCode;

    @Schema(description = "VIN号，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vin;

    @Schema(description = "物料编码，取自PO单", requiredMode = Schema.RequiredMode.REQUIRED)
    private String materialCode;

    @Schema(description = "物料描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String materialDescription;

    @Schema(description = "销售市场", requiredMode = Schema.RequiredMode.REQUIRED)
    private String salesMarket;

    @Schema(description = "年款", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modelYear;

    @Schema(description = "选装包", requiredMode = Schema.RequiredMode.REQUIRED)
    private String optionalPackage;

    @Schema(description = "排产时间，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime scheduleTime;

    @Schema(description = "打印条码时间，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime barcodePrintTime;

    @Schema(description = "焊装上线时间，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime bodyInTime;

    @Schema(description = "涂装上线时间，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime paintInTime;

    @Schema(description = "总装上线时间，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime assemblyInTime;

    @Schema(description = "总装下线时间，从APS获取", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime assemblyOutTime;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;


}
