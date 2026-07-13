package com.leapmotor.export.vehicle.infrastructure.so.entity;

import java.io.Serializable;

import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * <p>
 * SO车辆明细表领域对象
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_vehicle_cbu_so_detail", autoResultMap = true)
public class VehicleCbuSoDetail extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态(1:启用,0=停用)
     */
    @TableField("status")
    private Integer status;

    /**
     * 关联SO主表ID
     */
    @TableField("so_id")
    private String soId;

    /**
     * 内部车辆号，从APS获取
     */
    @TableField("internal_vehicle_no")
    private String internalVehicleNo;

    /**
     * 产销识别码，从APS获取
     */
    @TableField("production_identify_code")
    private String productionIdentifyCode;

    /**
     * VIN号，从APS获取
     */
    @TableField("vin")
    private String vin;

    /**
     * 物料编码，取自PO单
     */
    @TableField("material_code")
    private String materialCode;

    /**
     * 物料描述
     */
    @TableField("material_description")
    private String materialDescription;

    /**
     * 销售市场
     */
    @TableField("sales_market")
    private String salesMarket;

    /**
     * 年款
     */
    @TableField("model_year")
    private String modelYear;

    /**
     * 选装包
     */
    @TableField("optional_package")
    private String optionalPackage;

    /**
     * 排产时间，从APS获取
     */
    @TableField("schedule_time")
    private LocalDateTime scheduleTime;

    /**
     * 打印条码时间，从APS获取
     */
    @TableField("barcode_print_time")
    private LocalDateTime barcodePrintTime;

    /**
     * 焊装上线时间，从APS获取
     */
    @TableField("body_in_time")
    private LocalDateTime bodyInTime;

    /**
     * 涂装上线时间，从APS获取
     */
    @TableField("paint_in_time")
    private LocalDateTime paintInTime;

    /**
     * 总装上线时间，从APS获取
     */
    @TableField("assembly_in_time")
    private LocalDateTime assemblyInTime;

    /**
     * 总装下线时间，从APS获取
     */
    @TableField("assembly_out_time")
    private LocalDateTime assemblyOutTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
