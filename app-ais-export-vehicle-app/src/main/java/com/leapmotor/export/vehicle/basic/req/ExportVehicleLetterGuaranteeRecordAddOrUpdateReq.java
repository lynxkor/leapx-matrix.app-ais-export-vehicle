package com.leapmotor.export.vehicle.basic.req;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;

/**
 *
 * 保函交易明细新增/编辑请求对象
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleLetterGuaranteeRecordAddOrUpdateReq extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函交易明细编号,生成规则 BK+yyyyMMdd+HHmmss+5位流水号(例 BK20260415143000001),全库唯一" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgRecordCode;

    @Schema(description = "保函ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgId;

    @Schema(description = "保函编号(冗余)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgCode;

    @Schema(description = "保函类型(冗余,字典 letter_guarantee_lg_type)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String lgType;

    @Schema(description = "客户ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @Schema(description = "变动类型(字典 letter_guarantee_record_type: 10冻结 20解冻 30转应收 40释放应收 50失效)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String transactionType;

    @Schema(description = "变动金额(始终为正)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal transactionAmount;

    @Schema(description = "变动说明(如 \"PO单 101提交,资金冻结\")" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String transactionRemark;

    @Schema(description = "变动前可用订单金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal beforeAvailable;

    @Schema(description = "变动后可用订单金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal afterAvailable;

    @Schema(description = "变动前占用订单金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal beforeOccupied;

    @Schema(description = "变动后占用订单金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal afterOccupied;

    @Schema(description = "变动前应收订单金额(冻结/解冻/失效 时为 NULL,转应收/释放应收 时填值)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal beforeReceivable;

    @Schema(description = "变动后应收订单金额(同上)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal afterReceivable;

    @Schema(description = "PO 单号(不允许编辑,根据冻结/解冻情况自动赋值)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "SO 单号(不允许编辑,根据转应收/释放应收情况自动赋值,冻结/解冻 时为 NULL)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String soNo;

    @Schema(description = "VIN 车辆识别码(不允许编辑,根据转应收情况自动赋值,冻结/解冻 时为 NULL)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String vin;

    @Schema(description = "状态变更描述" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusDesc;

    @Schema(description = "归属用户ID(沿用主保函的归属)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerUserId;

    @Schema(description = "归属门店ID(沿用主保函的归属)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerOrgId;


}
