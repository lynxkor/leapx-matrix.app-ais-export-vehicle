package com.leapmotor.export.vehicle.basic.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 保函交易明细查询对象
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleLetterGuaranteeRecordQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保函交易明细编号,生成规则 BK+yyyyMMdd+HHmmss+5位流水号(例 BK20260415143000001),全库唯一
     */
    private String lgRecordCode;

    /**
     * 交易明细编号-单条模糊(配合 lgRecordCode 使用;输入单个值时模糊匹配)
     */
    private String lgRecordCodeLike;

    /**
     * 交易明细编号-批量精确(逗号或数组传入,多条 IN 精确匹配)
     */
    private List<String> lgRecordCodes;

    /**
     * 保函ID
     */
    private String lgId;

    /**
     * 保函编号(冗余)
     */
    private String lgCode;

    /**
     * 保函编号-单条模糊
     */
    private String lgCodeLike;

    /**
     * 保函编号-批量精确
     */
    private List<String> lgCodes;

    /**
     * 保函类型(冗余,字典 letter_guarantee_lg_type)
     */
    private String lgType;

    /**
     * 客户ID(单选)
     */
    private String customerId;

    /**
     * 变动类型(单值精确)
     */
    private String transactionType;

    /**
     * 变动类型-多选(字典 letter_guarantee_record_type: 10冻结 20解冻 30转应收 40应收释放 50失效;IN 查询)
     */
    private List<String> transactionTypes;

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
     * PO单号-单条模糊
     */
    private String poNoLike;

    /**
     * PO单号-批量精确
     */
    private List<String> poNos;

    /**
     * SO 单号(不允许编辑,根据转应收/应收释放情况自动赋值,冻结/解冻 时为 NULL)
     */
    private String soNo;

    /**
     * SO单号-单条模糊
     */
    private String soNoLike;

    /**
     * SO单号-批量精确
     */
    private List<String> soNos;

    /**
     * VIN 车辆识别码(不允许编辑,根据转应收情况自动赋值,冻结/解冻 时为 NULL)
     */
    private String vin;

    /**
     * VIN号-单条模糊
     */
    private String vinLike;

    /**
     * VIN号-批量精确
     */
    private List<String> vins;

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
     * 归属用户ID(沿用主保函的归属)
     */
    private Long ownerUserId;

    /**
     * 归属门店ID(沿用主保函的归属)
     */
    private Long ownerOrgId;


}
