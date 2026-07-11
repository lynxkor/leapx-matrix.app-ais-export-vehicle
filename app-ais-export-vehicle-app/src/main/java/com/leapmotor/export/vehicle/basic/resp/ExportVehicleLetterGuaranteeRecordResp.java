package com.leapmotor.export.vehicle.basic.resp;

import com.leapmotor.export.vehicle.letterGuarantee.enums.LgStatusEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.LgTypeEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.RecordTypeEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import com.leapmotor.paltform.ais.common.model.enums.StatusEnum;
import com.leapmotor.platform.ais.common.web.serializer.BigDecimalFormat;
import com.leapmotor.platform.ais.common.web.serializer.Desensitize;
import com.leapmotor.platform.ais.common.web.serializer.DictEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保函交易明细值对象
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleLetterGuaranteeRecordResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "保函交易明细编号,生成规则 BK+yyyyMMdd+HHmmss+5位流水号(例 BK20260415143000001),全库唯一")
    private String lgRecordCode;

    @Schema(description = "保函ID")
    private String lgId;

    @Schema(description = "保函编号(冗余)")
    private String lgCode;

    @Schema(description = "保函类型(冗余,字典 letter_guarantee_lg_type)")
    @DictEnumType(type = LgTypeEnum.class)
    private String lgType;

    @Schema(description = "客户ID")
    private String customerId;
    private String customerCode;
    private String customerName;

    @Schema(description = "变动类型(字典 letter_guarantee_record_type: 10冻结 20解冻 30转应收 40释放应收 50失效)")
    @DictEnumType(type = RecordTypeEnum.class)
    private String transactionType;

    @Schema(description = "变动金额(始终为正)")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal transactionAmount;

    @Schema(description = "变动说明(如 \"PO单 101提交,资金冻结\")")
    private String transactionRemark;

    @Schema(description = "变动前可用订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal beforeAvailable;

    @Schema(description = "变动后可用订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal afterAvailable;

    @Schema(description = "变动前占用订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal beforeOccupied;

    @Schema(description = "变动后占用订单金额")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal afterOccupied;

    @Schema(description = "变动前应收订单金额(冻结/解冻/失效 时为 NULL,转应收/释放应收 时填值)")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal beforeReceivable;

    @Schema(description = "变动后应收订单金额(同上)")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal afterReceivable;

    @Schema(description = "PO 单号(不允许编辑,根据冻结/解冻情况自动赋值)")
    private String poNo;

    @Schema(description = "SO 单号(不允许编辑,根据转应收/释放应收情况自动赋值,冻结/解冻 时为 NULL)")
    private String soNo;

    @Schema(description = "VIN 车辆识别码(不允许编辑,根据转应收情况自动赋值,冻结/解冻 时为 NULL)")
    @Desensitize(strategy = Desensitize.DefaultStrategy.VIN)
    private String vin;

    @Schema(description = "状态(1:启用,0:停用)")
    @DictEnumType(type = StatusEnum.class)
    private Integer status;

    @Schema(description = "状态变更描述")
    private String statusDesc;

    @Schema(description = "归属用户ID(沿用主保函的归属)")
    private Long ownerUserId;

    @Schema(description = "归属门店ID(沿用主保函的归属)")
    private Long ownerOrgId;

    // ---- 以下 5 个字段来源保函主表,由 Service 层关联查询后填充 ----

    @Schema(description = "保函状态(字典 letter_guarantee_lg_status)")
    @DictEnumType(type = LgStatusEnum.class)
    private String lgStatus;

    @Schema(description = "审核时间")
    private LocalDateTime approveTime;

    @Schema(description = "保函金额(人民币CNY)")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal totalAmount;

    @Schema(description = "换算订单额度比例 0~1,保留 6 位小数")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal conversionRatio;

    @Schema(description = "换算订单额度(可用订单金额)")
    @BigDecimalFormat(pattern = "#.########")
    private BigDecimal availableAmount;


}
