package com.leapmotor.export.vehicle.po.req;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;

/**
 *
 * PO订单主表新增/编辑请求对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportCbuVehiclePoAddOrUpdateReq extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号(取自红旗CBU接口),业务唯一键" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "订单状态,枚举见评审§2.1(草稿/销售已提交/销售已驳回/供需审核通过/供需审核驳回/产销平衡通过/产销平衡驳回)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderStatus;

    @Schema(description = "销售公司,下拉选,提交时必填" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String salesCompanyCode;

    @Schema(description = "客户编码,按 external_customer_code 匹配客户主数据,匹配不到则空;提交时必填" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerCode;

    @Schema(description = "客户名称,按客户编码带出" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerName;

    @Schema(description = "外部客户编码,取自红旗CBU接口" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String externalCustomerCode;

    @Schema(description = "总金额,= SUM(明细 total_amount)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalAmount;

    @Schema(description = "提交时间,销售提交时记录" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime submitTime;

    @Schema(description = "驳回时间,各类驳回时记录" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime rejectTime;

    @Schema(description = "驳回人,界面不显示" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String rejectUser;

    @Schema(description = "供需审核通过时间" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime supplyDemandApproveTime;

    @Schema(description = "供需审核人" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String supplyDemandApprover;

    @Schema(description = "产销平衡通过时间" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime productionSalesBalanceApproveTime;

    @Schema(description = "产销平衡审核人" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String productionSalesBalanceApprover;

    @Schema(description = "驳回说明,各类驳回时必填" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String rejectReason;

    @Schema(description = "数据来源,枚举:销服/红旗" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataSource;

    @Schema(description = "订单类型,红旗接口同步" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderType;

    @Schema(description = "计划版本号,取自红旗CBU接口" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String planVersion;

    @Schema(description = "审结结果同步时间,反馈红旗接口的发送时间" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime auditResultSyncTime;

    @Schema(description = "审结结果同步结果,= 成功/失败" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String auditResultSyncResult;

    @Schema(description = "客户系统返回信息,红旗接口对方返回" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerSystemReturnInfo;

    @Schema(description = "状态变更描述" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusDesc;

    @Schema(description = "归属用户ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerUserId;

    @Schema(description = "归属门店ID(组织ID)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerOrgId;


}
