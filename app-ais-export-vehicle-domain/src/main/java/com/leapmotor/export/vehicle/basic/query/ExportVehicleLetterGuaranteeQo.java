package com.leapmotor.export.vehicle.basic.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;


/**
 *
 * 保函主表查询对象
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleLetterGuaranteeQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保函编号(精确查询)
     */
    private String lgCode;

    /**
     * 保函编号(模糊查询)
     */
    private String lgCodeLike;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 保函类型(字典 letter_guarantee_lg_type: PREPAYMENT 预付款保函;G117 红旗项目目前仅启用预付款;新增时必填,无默认值)
     */
    private String lgType;

    /**
     * 订单类型(字典 letter_guarantee_order_type: CBU/SKD/CKD;新增时必填,无默认值;仅草稿/驳回可编辑)
     */
    private String orderType;

    /**
     * 开户银行(开立银行+支行合并,如\"中国工商银行杭州武林支行\")
     */
    private String bankName;

    /**
     * 保函金额(人民币CNY)
     */
    private BigDecimal totalAmount;

    /**
     * 换算订单额度比例 0~1,保留 6 位小数
     */
    private BigDecimal conversionRatio;

    /**
     * 可用订单金额
     */
    private BigDecimal availableAmount;

    /**
     * 占用订单金额
     */
    private BigDecimal occupiedAmount;

    /**
     * 应收订单金额
     */
    private BigDecimal receivableAmount;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 生效日期-起始(>= 生效日期)
     */
    private LocalDate effectiveDateStart;

    /**
     * 生效日期-截止(<= 生效日期)
     */
    private LocalDate effectiveDateEnd;

    /**
     * 生效日期上限(≤ 此日期);为空不过滤。
     * <p>用途:列出可用保函时,只筛已过生效起始日的保函(生效日期 ≤ 当前日期),避免提前使用尚未生效的保函。
     */
    private LocalDate effectiveDateLe;

    /**
     * 失效日期
     */
    private LocalDate expiryDate;

    /**
     * 失效日期-起始(>= 失效日期)
     */
    private LocalDate expiryDateStart;

    /**
     * 失效日期-截止(<= 失效日期)
     */
    private LocalDate expiryDateEnd;

    /**
     * 失效日期下限(≥ 此日期);为空不过滤。
     * <p>用途:PO 资金校验时,只筛失效日期 ≥ 订单创建时间 + coverDays 的保函,避免早期失效的保函被误用。
     */
    private LocalDate expiryDateGe;

    /**
     * 审核状态(字典 letter_guarantee_approval_status: 10草稿 20已提交 30审核通过 40驳回 50作废)
     */
    private String approvalStatus;

    /**
     * 审核状态-多值查询(字典 letter_guarantee_approval_status);非空时按 IN 过滤,与单值 approvalStatus 可同时生效
     */
    private List<String> approvalStatusList;

    /**
     * 保函状态(字典 letter_guarantee_lg_status):10=未生效(草稿/提交/驳回/作废,默认值),20=有效(审核通过时置),30=失效(到期/手动失效时置)
     */
    private String lgStatus;

    /**
     * 提交人ID
     */
    private Long submitterId;

    /**
     * 提交人名称
     */
    private String submitterName;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 提交时间-起始(>= 提交时间)
     */
    private LocalDateTime submitTimeStart;

    /**
     * 提交时间-截止(<= 提交时间)
     */
    private LocalDateTime submitTimeEnd;

    /**
     * 审核人ID
     */
    private Long approverId;

    /**
     * 审核人名称
     */
    private String approverName;

    /**
     * 审核时间
     */
    private LocalDateTime approveTime;

    /**
     * 审核时间-起始(>= 审核时间)
     */
    private LocalDateTime approveTimeStart;

    /**
     * 审核时间-截止(<= 审核时间)
     */
    private LocalDateTime approveTimeEnd;

    /**
     * 审核意见
     */
    private String approveRemark;

    /**
     * 业务备注
     */
    private String remark;

    /**
     * 创建时间-起始(>= 创建时间)
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间-截止(<= 创建时间)
     */
    private LocalDateTime createTimeEnd;

    /**
     * 状态变更描述
     */
    private String statusDesc;

    /**
     * 归属用户ID,数据权限归属到用户时使用
     */
    private Long ownerUserId;

    /**
     * 归属门店ID(组织ID),数据权限归属到本组织以及本下级
     */
    private Long ownerOrgId;


}
