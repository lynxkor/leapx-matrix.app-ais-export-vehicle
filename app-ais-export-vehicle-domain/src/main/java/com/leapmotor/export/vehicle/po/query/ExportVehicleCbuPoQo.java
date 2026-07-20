package com.leapmotor.export.vehicle.po.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;


/**
 *
 * PO订单主表查询对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleCbuPoQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号(精确查询)
     */
    private String poNo;

    /**
     * PO单号(模糊查询,列表搜索用)
     */
    private String poNoLike;

    /**
     * PO单号列表(批量精确查询)
     */
    private List<String> poNos;

    /**
     * 订单状态(单个查询)
     */
    private String orderStatus;

    /**
     * 订单状态列表(多选查询)
     */
    private List<String> orderStatuses;

    /**
     * 销售公司,下拉选,提交时必填
     */
    private String salesCompanyCode;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户名称(模糊查询,列表搜索用)
     */
    private String customerNameLike;

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
     * 提交时间起止（列表筛选用）
     */
    private LocalDateTime submitTimeStart;
    private LocalDateTime submitTimeEnd;

    /**
     * 创建时间起止（列表筛选用）
     */
    private LocalDateTime createTimeStart;
    private LocalDateTime createTimeEnd;

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
     * 供需审核通过时间起止（列表筛选用）
     */
    private LocalDateTime supplyDemandApproveTimeStart;
    private LocalDateTime supplyDemandApproveTimeEnd;

    /**
     * 供需审核人
     */
    private String supplyDemandApprover;

    /**
     * 产销平衡通过时间
     */
    private LocalDateTime productionSalesBalanceApproveTime;

    /**
     * 产销平衡通过时间起止（列表筛选用）
     */
    private LocalDateTime productionSalesBalanceApproveTimeStart;
    private LocalDateTime productionSalesBalanceApproveTimeEnd;

    /**
     * 产销平衡审核人
     */
    private String productionSalesBalanceApprover;

    /**
     * 驳回说明,各类驳回时必填
     */
    private String rejectReason;

    /**
     * 数据来源,枚举:1销服/2红旗
     */
    private Integer dataSource;

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
