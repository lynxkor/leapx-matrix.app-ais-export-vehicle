package com.leapmotor.export.vehicle.so.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 *
 * SO车辆明细表领域对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoDetailDo extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联SO单号（冗余字段，业务关联走 m.id = d.id）
     */
    private String soNo;

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
     * 计划总装下线日期，从APS获取
     */
    private LocalDate planOfflineDate;

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
     * APS最新同步结果：0失败、1成功（下发排产或作废，取最近一次）
     */
    private Integer syncApsResult;

    /**
     * APS最新同步时间
     */
    private LocalDateTime syncApsTime;

    /**
     * APS最新返回信息
     */
    private String syncApsResponseInfo;

    /**
     * SAP最新同步结果：0失败、1成功
     */
    private Integer syncSapResult;

    /**
     * SAP最新同步时间
     */
    private LocalDateTime syncSapTime;

    /**
     * SAP最新返回信息
     */
    private String syncSapResponseInfo;

    /**
     * 客户系统最新同步结果：0失败、1成功
     */
    private Integer syncCustomerResult;

    /**
     * 客户系统最新同步时间
     */
    private LocalDateTime syncCustomerTime;

    /**
     * 客户系统最新返回信息
     */
    private String syncCustomerResponseInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态(1:启用,0:停用)
     */
    private Integer status;

    /**
     * 状态变更描述
     */
    private String statusDesc;

    /**
     * 归属用户ID，当数据权限归属到用户时
     */
    private Long ownerUserId;

    /**
     * 归属门店ID（组织ID），当数据权限归属到本组织以及本下级
     */
    private Long ownerOrgId;

}