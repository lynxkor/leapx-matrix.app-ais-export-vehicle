package com.leapmotor.export.vehicle.po.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import java.io.Serializable;


/**
 *
 * PO订单主表请求对象
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
public class ExportVehicleCbuPoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号(取自红旗CBU接口),业务唯一键
     */
    private String poNo;

    /**
     * 订单状态,枚举见评审§2.1(草稿/销售已提交/销售已驳回/供需审核通过/供需审核驳回/产销平衡通过/产销平衡驳回)
     */
    private String orderStatus;

    /**
     * 销售公司,下拉选,提交时必填
     */
    private String salesCompanyCode;

    /**
     * 客户编码,按 external_customer_code 匹配客户主数据,匹配不到则空;提交时必填
     */
    private String customerCode;

    /**
     * 客户名称,按客户编码带出
     */
    private String customerName;

    /**
     * 外部客户编码,取自红旗CBU接口
     */
    private String externalCustomerCode;

    /**
     * 总金额,= SUM(明细 total_amount)
     */
    private BigDecimal totalAmount;

    /**
     * 提交时间,销售提交时记录
     */
    private LocalDateTime submitTime;

    /**
     * 驳回时间,各类驳回时记录
     */
    private LocalDateTime rejectTime;

    /**
     * 驳回人,界面不显示
     */
    private String rejectUser;

    /**
     * 供需审核通过时间
     */
    private LocalDateTime supplyDemandApproveTime;

    /**
     * 供需审核人
     */
    private String supplyDemandApprover;

    /**
     * 产销平衡通过时间
     */
    private LocalDateTime productionSalesBalanceApproveTime;

    /**
     * 产销平衡审核人
     */
    private String productionSalesBalanceApprover;

    /**
     * 驳回说明,各类驳回时必填
     */
    private String rejectReason;

    /**
     * 数据来源,枚举:销服/红旗
     */
    private String dataSource;

    /**
     * 订单类型,红旗接口同步
     */
    private Integer orderType;

    /**
     * 计划版本号,取自红旗CBU接口
     */
    private String planVersion;

    /**
     * 审结结果同步时间,反馈红旗接口的发送时间
     */
    private LocalDateTime auditResultSyncTime;

    /**
     * 审结结果同步结果,= 成功/失败
     */
    private String auditResultSyncResult;

    /**
     * 客户系统返回信息,红旗接口对方返回
     */
    private String customerSystemReturnInfo;

    /**
     * 状态(1:启用,0:停用),平台规范,业务流转用 order_status
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