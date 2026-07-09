package com.leapmotor.export.vehicle.infrastructure.po.entity;

import java.io.Serializable;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * <p>
 * PO订单主表领域对象
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_cbu_vehicle_po",autoResultMap = true)
public class ExportCbuVehiclePo extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号(取自红旗CBU接口),业务唯一键
     */
    @TableField("po_no")
    private String poNo;

    /**
     * 订单状态,枚举见评审§2.1(草稿/销售已提交/销售已驳回/供需审核通过/供需审核驳回/产销平衡通过/产销平衡驳回)
     */
    @TableField("order_status")
    private String orderStatus;

    /**
     * 销售公司,下拉选,提交时必填
     */
    @TableField("sales_company")
    private String salesCompany;

    /**
     * 客户编码,按 external_customer_code 匹配客户主数据,匹配不到则空;提交时必填
     */
    @TableField("customer_code")
    private String customerCode;

    /**
     * 客户名称,按客户编码带出
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 外部客户编码,取自红旗CBU接口
     */
    @TableField("external_customer_code")
    private String externalCustomerCode;

    /**
     * 总金额,= SUM(明细 total_amount)
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 提交时间,销售提交时记录
     */
    @TableField("submit_time")
    private LocalDateTime submitTime;

    /**
     * 驳回时间,各类驳回时记录
     */
    @TableField("reject_time")
    private LocalDateTime rejectTime;

    /**
     * 驳回人,界面不显示
     */
    @TableField("reject_user")
    private String rejectUser;

    /**
     * 供需审核通过时间
     */
    @TableField("supply_demand_approve_time")
    private LocalDateTime supplyDemandApproveTime;

    /**
     * 供需审核人
     */
    @TableField("supply_demand_approver")
    private String supplyDemandApprover;

    /**
     * 产销平衡通过时间
     */
    @TableField("production_sales_balance_approve_time")
    private LocalDateTime productionSalesBalanceApproveTime;

    /**
     * 产销平衡审核人
     */
    @TableField("production_sales_balance_approver")
    private String productionSalesBalanceApprover;

    /**
     * 驳回说明,各类驳回时必填
     */
    @TableField("reject_reason")
    private String rejectReason;

    /**
     * 数据来源,枚举:销服/红旗
     */
    @TableField("data_source")
    private String dataSource;

    /**
     * 订单类型,红旗接口同步
     */
    @TableField("order_type")
    private String orderType;

    /**
     * 计划版本号,取自红旗CBU接口
     */
    @TableField("plan_version")
    private String planVersion;

    /**
     * 审结结果同步时间,反馈红旗接口的发送时间
     */
    @TableField("audit_result_sync_time")
    private LocalDateTime auditResultSyncTime;

    /**
     * 审结结果同步结果,= 成功/失败
     */
    @TableField("audit_result_sync_result")
    private String auditResultSyncResult;

    /**
     * 客户系统返回信息,红旗接口对方返回
     */
    @TableField("customer_system_return_info")
    private String customerSystemReturnInfo;

    /**
     * 状态(1:启用,0:停用),平台规范,业务流转用 order_status
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
