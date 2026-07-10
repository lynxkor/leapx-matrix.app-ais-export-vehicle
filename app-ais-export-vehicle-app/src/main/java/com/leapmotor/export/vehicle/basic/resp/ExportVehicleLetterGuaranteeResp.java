package com.leapmotor.export.vehicle.basic.resp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.ApprovalStatusEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.LgStatusEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.LgTypeEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.OrderTypeEnum;
import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import com.leapmotor.platform.ais.common.web.serializer.DictEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;

/**
 *
 * 保函主表值对象
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleLetterGuaranteeResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函编号")
    private String lgCode;

    @Schema(description = "客户ID")
    private String customerId;
    private String customerCode;
    private String customerName;

    @Schema(description = "保函类型(字典 letter_guarantee_lg_type: PREPAYMENT 预付款保函;G117 红旗项目目前仅启用预付款;新增时必填,无默认值)")
    @DictEnumType(type = LgTypeEnum.class)
    private String lgType;

    @Schema(description = "订单类型(字典 letter_guarantee_order_type: CBU/SKD/CKD;新增时必填,无默认值;仅草稿/驳回可编辑)")
    @DictEnumType(type = OrderTypeEnum.class)
    private String orderType;

    @Schema(description = "开户银行(开立银行+支行合并,如\"中国工商银行杭州武林支行\")")
    private String bankName;

    @Schema(description = "保函金额(人民币CNY)")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal totalAmount;

    @Schema(description = "换算订单额度比例 0~1,保留 6 位小数")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal conversionRatio;

    @Schema(description = "可用订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal availableAmount;

    @Schema(description = "占用订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal occupiedAmount;

    @Schema(description = "应收订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal receivableAmount;

    @Schema(description = "生效日期")
    private LocalDate effectiveDate;

    @Schema(description = "失效日期")
    private LocalDate expiryDate;

    @Schema(description = "审核状态(字典 letter_guarantee_approval_status: 10草稿 20已提交 30审核通过 40驳回 50作废)")
    @DictEnumType(type = ApprovalStatusEnum.class)
    private String approvalStatus;

    @Schema(description = "保函状态(字典 letter_guarantee_lg_status):10=未生效(草稿/提交/驳回/作废,默认值),20=有效(审核通过时置),30=失效(到期/手动失效时置)")
    @DictEnumType(type = LgStatusEnum.class)
    private String lgStatus;

    @Schema(description = "提交人ID")
    private Long submitterId;

    @Schema(description = "提交人名称")
    private String submitterName;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "审核人ID")
    private Long approverId;

    @Schema(description = "审核人名称")
    private String approverName;

    @Schema(description = "审核时间")
    private LocalDateTime approveTime;

    @Schema(description = "审核意见")
    private String approveRemark;

    @Schema(description = "业务备注")
    private String remark;

    @Schema(description = "状态(1:启用,0:停用)")
    @DictEnumType(type = StatusEnum.class)
    private Integer status;

    @Schema(description = "状态变更描述")
    private String statusDesc;

    @Schema(description = "归属用户ID,数据权限归属到用户时使用")
    private Long ownerUserId;

    @Schema(description = "归属门店ID(组织ID),数据权限归属到本组织以及本下级")
    private Long ownerOrgId;


}
