package com.leapmotor.export.vehicle.basic.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;


/**
 *
 * 保函交易明细领域对象
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleLetterGuaranteeRecordDo extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保函交易明细编号,生成规则 BK+yyyyMMdd+HHmmss+5位流水号(例 BK20260415143000001),全库唯一
     */
    private String lgRecordCode;

    /**
     * 保函ID
     */
    private String lgId;

    /**
     * 保函编号(冗余)
     */
    private String lgCode;

    /**
     * 保函类型(冗余,字典 letter_guarantee_lg_type)
     */
    private String lgType;

    /**
     * 客户ID
     */
    private String customerId;

    /**
     * 变动类型(字典 letter_guarantee_record_type: 10冻结 20解冻 30转应收 40应收释放 50失效)
     */
    private String transactionType;

    /**
     * 变动金额(始终为正)
     */
    private BigDecimal transactionAmount;

    /**
     * 变动说明(如 \"PO单 101提交,资金冻结\")
     */
    private String transactionRemark;

    /**
     * 变动前可用订单金额
     */
    private BigDecimal beforeAvailable;

    /**
     * 变动后可用订单金额
     */
    private BigDecimal afterAvailable;

    /**
     * 变动前占用订单金额
     */
    private BigDecimal beforeOccupied;

    /**
     * 变动后占用订单金额
     */
    private BigDecimal afterOccupied;

    /**
     * 变动前应收订单金额(冻结/解冻/失效 时为 NULL,转应收/应收释放 时填值)
     */
    private BigDecimal beforeReceivable;

    /**
     * 变动后应收订单金额(同上)
     */
    private BigDecimal afterReceivable;

    /**
     * PO 单号(不允许编辑,根据冻结/解冻情况自动赋值)
     */
    private String poNo;

    /**
     * SO 单号(不允许编辑,根据转应收/应收释放情况自动赋值,冻结/解冻 时为 NULL)
     */
    private String soNo;

    /**
     * VIN 车辆识别码(不允许编辑,根据转应收情况自动赋值,冻结/解冻 时为 NULL)
     */
    private String vin;

    /**
     * 状态(1:启用,0:停用)
     */
    private Integer status;

    /**
     * 状态变更描述
     */
    private String statusDesc;

    /**
     * 归属用户ID(沿用主保函的归属)
     */
    private Long ownerUserId;

    /**
     * 归属门店ID(沿用主保函的归属)
     */
    private Long ownerOrgId;

    // ---- 以下 5 个字段来源保函主表,由 Service 层关联查询后填充 ----

    /**
     * 保函状态(字典 letter_guarantee_lg_status)
     */
    private String lgStatus;

    /**
     * 审核时间
     */
    private LocalDateTime approveTime;

    /**
     * 保函金额
     */
    private BigDecimal totalAmount;

    /**
     * 换算订单额度比例
     */
    private BigDecimal conversionRatio;

    /**
     * 换算订单额度(可用订单金额)
     */
    private BigDecimal availableAmount;


}