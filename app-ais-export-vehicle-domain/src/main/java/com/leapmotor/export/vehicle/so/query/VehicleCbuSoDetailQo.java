package com.leapmotor.export.vehicle.so.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * SO车辆明细表查询对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoDetailQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联SO主表ID
     */
    private String soId;

    /**
     * 内部车辆号，从APS获取
     */
    private String internalVehicleNo;

    /**
     * 产销识别码，从APS获取
     */
    private String productionIdentifyCode;

    /**
     * VIN号，从APS获取
     */
    private String vin;

    /**
     * 物料编码，取自PO单
     */
    private String materialCode;

    /**
     * 物料描述
     */
    private String materialDescription;

    /**
     * 销售市场
     */
    private String salesMarket;

    /**
     * 年款
     */
    private String modelYear;

    /**
     * 选装包
     */
    private String optionalPackage;

    /**
     * 排产时间，从APS获取
     */
    private LocalDateTime scheduleTime;

    /**
     * 打印条码时间，从APS获取
     */
    private LocalDateTime barcodePrintTime;

    /**
     * 焊装上线时间，从APS获取
     */
    private LocalDateTime bodyInTime;

    /**
     * 涂装上线时间，从APS获取
     */
    private LocalDateTime paintInTime;

    /**
     * 总装上线时间，从APS获取
     */
    private LocalDateTime assemblyInTime;

    /**
     * 总装下线时间，从APS获取
     */
    private LocalDateTime assemblyOutTime;

    /**
     * 备注
     */
    private String remark;


}
