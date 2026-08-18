package com.leapmotor.export.vehicle.basic.req;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;

/**
 *
 * 保函主表新增/编辑请求对象
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleLetterGuaranteeAddOrUpdateReq extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函编号" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "保函编号不能为空")
    @Size(max = 30, message = "保函编号长度不能超过30")
    private String lgCode;

    @Schema(description = "客户ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @Schema(description = "保函类型(字典 letter_guarantee_lg_type: PREPAYMENT 预付款保函;G117 红旗项目目前仅启用预付款;新增时必填,无默认值)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgType;

    @Schema(description = "订单类型(字典 letter_guarantee_order_type: CBU/SKD/CKD;新增时必填,无默认值;仅草稿/驳回可编辑)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderType;

    @Schema(description = "开户银行(开立银行+支行合并,如\"中国工商银行杭州武林支行\")" ,requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "开户银行不能为空")
    @Size(max = 100, message = "开户银行长度不能超过100")
    private String bankName;

    @Schema(description = "保函金额(人民币CNY)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalAmount;

    @Schema(description = "换算订单额度比例 0~1,保留 6 位小数" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal conversionRatio;

    @Schema(description = "可用订单金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal availableAmount;

    @Schema(description = "占用订单金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal occupiedAmount;

    @Schema(description = "应收订单金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal receivableAmount;

    @Schema(description = "生效日期" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate effectiveDate;

    @Schema(description = "失效日期" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate expiryDate;

    @Schema(description = "审核状态(字典 letter_guarantee_approval_status: 10草稿 20已提交 30审核通过 40驳回 50作废)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String approvalStatus;

    @Schema(description = "保函状态(字典 letter_guarantee_lg_status):10=未生效(草稿/提交/驳回/作废,默认值),20=有效(审核通过时置),30=失效(到期/手动失效时置)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgStatus;

    @Schema(description = "提交人ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long submitterId;

    @Schema(description = "提交人名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String submitterName;

    @Schema(description = "提交时间" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime submitTime;

    @Schema(description = "审核人ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long approverId;

    @Schema(description = "审核人名称" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String approverName;

    @Schema(description = "审核时间" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime approveTime;

    @Schema(description = "审核意见" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String approveRemark;

    @Schema(description = "业务备注" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;

    @Schema(description = "状态变更描述" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusDesc;

    @Schema(description = "归属用户ID,数据权限归属到用户时使用" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerUserId;

    @Schema(description = "归属门店ID(组织ID),数据权限归属到本组织以及本下级" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerOrgId;


}
