package com.leapmotor.export.vehicle.infrastructure.po.entity;

import java.io.Serializable;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * <p>
 * PO订单明细表领域对象
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "purchase_order_detail",autoResultMap = true)
public class PurchaseOrderDetail extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号,关联主表 purchase_order.po_number
     */
    @TableField("po_number")
    private String poNumber;

    /**
     * PO明细编号,自动生成 POD+年月日+时分+5位流水
     */
    @TableField("po_detail_number")
    private String poDetailNumber;

    /**
     * 订单明细编号,取自红旗CBU接口
     */
    @TableField("order_detail_number")
    private String orderDetailNumber;

    /**
     * 物料编码,按 sync_material_code 匹配整编主数据;提交时必填
     */
    @TableField("material_code")
    private String materialCode;

    /**
     * 同步物料编码,红旗18位物料编码
     */
    @TableField("sync_material_code")
    private String syncMaterialCode;

    /**
     * 物料描述,关联整编主数据
     */
    @TableField("material_description")
    private String materialDescription;

    /**
     * 销售市场,关联整编主数据
     */
    @TableField("sales_market")
    private String salesMarket;

    /**
     * 年款,关联整编主数据
     */
    @TableField("year_model")
    private String yearModel;

    /**
     * 选装包,关联整编主数据
     */
    @TableField("optional_package")
    private String optionalPackage;

    /**
     * 需求数量,取自红旗CBU接口
     */
    @TableField("demand_quantity")
    private BigDecimal demandQuantity;

    /**
     * 期望下线日期,取自红旗CBU接口
     */
    @TableField("expected_offline_date")
    private LocalDate expectedOfflineDate;

    /**
     * VIN码年份,取自红旗CBU接口
     */
    @TableField("vin_year")
    private String vinYear;

    /**
     * 生产备注,可编辑,非必填,最多1000字
     */
    @TableField("production_remark")
    private String productionRemark;

    /**
     * 单价,按 物料编码+创建时间+订单类型 匹配价格主数据(取最后审核通过价);提交时必填
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 金额合计,= 单价 × 需求数量
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 状态(1:启用,0:停用),平台规范
     */
    @TableField("status")
    private Integer status;

    /**
     * 状态变更描述
     */
    @TableField("status_desc")
    private String statusDesc;

    /**
     * 归属用户ID
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

    /**
     * 归属门店ID(组织ID)
     */
    @TableField("owner_org_id")
    private Long ownerOrgId;


}
