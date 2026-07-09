package com.leapmotor.export.vehicle.po.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import java.io.Serializable;


/**
 *
 * PO订单明细表数据传输对象
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
public class ExportCbuVehiclePoDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号,关联主表 export_cbu_vehicle_po.po_no
     */
    private String poNo;

    /**
     * PO明细编号,自动生成 POD+年月日+时分+5位流水
     */
    private String poDetailNumber;

    /**
     * 订单明细编号,取自红旗CBU接口
     */
    private String orderDetailNumber;

    /**
     * 物料编码,按 sync_material_code 匹配整编主数据;提交时必填
     */
    private String materialCode;

    /**
     * 同步物料编码,红旗18位物料编码
     */
    private String syncMaterialCode;

    /**
     * 物料描述,关联整编主数据
     */
    private String materialDescription;

    /**
     * 销售市场,关联整编主数据
     */
    private String salesMarket;

    /**
     * 年款,关联整编主数据
     */
    private String yearModel;

    /**
     * 选装包,关联整编主数据
     */
    private String optionalPackage;

    /**
     * 需求数量,取自红旗CBU接口
     */
    private BigDecimal demandQuantity;

    /**
     * 期望下线日期,取自红旗CBU接口
     */
    private LocalDate expectedOfflineDate;

    /**
     * VIN码年份,取自红旗CBU接口
     */
    private String vinYear;

    /**
     * 生产备注,可编辑,非必填,最多1000字
     */
    private String productionRemark;

    /**
     * 单价,按 物料编码+创建时间+订单类型 匹配价格主数据(取最后审核通过价);提交时必填
     */
    private BigDecimal unitPrice;

    /**
     * 金额合计,= 单价 × 需求数量
     */
    private BigDecimal totalAmount;

    /**
     * 状态(1:启用,0:停用),平台规范
     */
    private Integer status;

    /**
     * 状态变更描述
     */
    private String statusDesc;

    /**
     * 归属用户ID
     */
    private Long ownerUserId;

    /**
     * 归属门店ID(组织ID)
     */
    private Long ownerOrgId;


}