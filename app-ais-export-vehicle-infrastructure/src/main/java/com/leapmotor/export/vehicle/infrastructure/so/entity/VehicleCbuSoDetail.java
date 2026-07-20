package com.leapmotor.export.vehicle.infrastructure.so.entity;

import java.io.Serializable;

import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
     * 主键ID，1:1 复用主表 export_vehicle_cbu_so.id（手工写入，关闭自动生成）
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 显式重写 setId：父类 {@link LpBaseEntity#setId(String)} 返回 {@code LpBaseEntity}（链式），
     * 而 Lombok @Data 默认生成 void 返回类型，签名冲突会编译失败。
     * 此处返回 LpBaseEntity 保持与父类一致，调用方需要链式时可继续使用。
     */
    @Override
    public LpBaseEntity setId(String id) {
        super.setId(id);
        return this;
    }

    /**
     * 关联SO单号
     */
    @TableField("so_no")
    private String soNo;

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
     * APS最新同步结果：0失败、1成功（下发排产或作废，取最近一次）
     */
    @TableField("sync_aps_result")
    private Integer syncApsResult;

    /**
     * APS最新同步时间
     */
    @TableField("sync_aps_time")
    private LocalDateTime syncApsTime;

    /**
     * APS最新返回信息
     */
    @TableField("sync_aps_response_info")
    private String syncApsResponseInfo;

    /**
     * SAP最新同步结果：0失败、1成功
     */
    @TableField("sync_sap_result")
    private Integer syncSapResult;

    /**
     * SAP最新同步时间
     */
    @TableField("sync_sap_time")
    private LocalDateTime syncSapTime;

    /**
     * SAP最新返回信息
     */
    @TableField("sync_sap_response_info")
    private String syncSapResponseInfo;

    /**
     * 客户系统最新同步结果：0失败、1成功
     */
    @TableField("sync_customer_result")
    private Integer syncCustomerResult;

    /**
     * 客户系统最新同步时间
     */
    @TableField("sync_customer_time")
    private LocalDateTime syncCustomerTime;

    /**
     * 客户系统最新返回信息
     */
    @TableField("sync_customer_response_info")
    private String syncCustomerResponseInfo;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态(1:启用,0:停用)
     */
    @TableField("status")
    private Integer status;

    /**
     * 状态变更描述
     */
    @TableField("status_desc")
    private String statusDesc;

    /**
     * 归属用户ID，当数据权限归属到用户时
     */
    @TableField(value = "owner_user_id", insertStrategy = FieldStrategy.IGNORED)
    private Long ownerUserId = 0L;

    /**
     * 归属门店ID（组织ID），当数据权限归属到本组织以及本下级
     */
    @TableField(value = "owner_org_id", insertStrategy = FieldStrategy.IGNORED)
    private Long ownerOrgId = 0L;

}
