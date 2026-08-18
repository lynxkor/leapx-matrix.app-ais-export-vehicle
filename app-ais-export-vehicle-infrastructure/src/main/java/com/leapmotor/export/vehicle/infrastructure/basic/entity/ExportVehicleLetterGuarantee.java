package com.leapmotor.export.vehicle.infrastructure.basic.entity;

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
 * 保函主表领域对象
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_vehicle_letter_guarantee",autoResultMap = true)
public class ExportVehicleLetterGuarantee extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保函编号
     */
    @TableField("lg_code")
    private String lgCode;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private String customerId;

    /**
     * 保函类型(字典 letter_guarantee_lg_type: PREPAYMENT 预付款保函;G117 红旗项目目前仅启用预付款;新增时必填,无默认值)
     */
    @TableField("lg_type")
    private String lgType;

    /**
     * 订单类型(字典 letter_guarantee_order_type: CBU/SKD/CKD;新增时必填,无默认值;仅草稿/驳回可编辑)
     */
    @TableField("order_type")
    private String orderType;

    /**
     * 开户银行(开立银行+支行合并,如\"中国工商银行杭州武林支行\")
     */
    @TableField("bank_name")
    private String bankName;

    /**
     * 保函金额(人民币CNY)
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 换算订单额度比例 0~1,保留 6 位小数
     */
    @TableField("conversion_ratio")
    private BigDecimal conversionRatio;

    /**
     * 可用订单金额
     */
    @TableField("available_amount")
    private BigDecimal availableAmount;

    /**
     * 占用订单金额
     */
    @TableField("occupied_amount")
    private BigDecimal occupiedAmount;

    /**
     * 应收订单金额
     */
    @TableField("receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 生效日期
     */
    @TableField("effective_date")
    private LocalDate effectiveDate;

    /**
     * 失效日期
     */
    @TableField("expiry_date")
    private LocalDate expiryDate;

    /**
     * 审核状态(字典 letter_guarantee_approval_status: 10草稿 20已提交 30审核通过 40驳回 50作废)
     */
    @TableField("approval_status")
    private String approvalStatus;

    /**
     * 保函状态(字典 letter_guarantee_lg_status):10=未生效(草稿/提交/驳回/作废,默认值),20=有效(审核通过时置),30=失效(到期/手动失效时置)
     */
    @TableField("lg_status")
    private String lgStatus;

    /**
     * 提交人ID
     */
    @TableField("submitter_id")
    private Long submitterId;

    /**
     * 提交人名称
     */
    @TableField("submitter_name")
    private String submitterName;

    /**
     * 提交时间
     */
    @TableField("submit_time")
    private LocalDateTime submitTime;

    /**
     * 审核人ID
     */
    @TableField("approver_id")
    private Long approverId;

    /**
     * 审核人名称
     */
    @TableField("approver_name")
    private String approverName;

    /**
     * 审核时间
     */
    @TableField("approve_time")
    private LocalDateTime approveTime;

    /**
     * 审核意见
     */
    @TableField("approve_remark")
    private String approveRemark;

    /**
     * 业务备注
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
     * 归属用户ID,数据权限归属到用户时使用
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

    /**
     * 归属门店ID(组织ID),数据权限归属到本组织以及本下级
     */
    @TableField("owner_org_id")
    private Long ownerOrgId;


}
