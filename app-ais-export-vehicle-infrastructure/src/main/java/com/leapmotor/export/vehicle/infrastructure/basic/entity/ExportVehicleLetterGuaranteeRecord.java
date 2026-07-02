package com.leapmotor.export.vehicle.infrastructure.basic.entity;

import java.io.Serializable;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * <p>
 * 保函交易明细(追加写)领域对象
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_vehicle_letter_guarantee_record",autoResultMap = true)
public class ExportVehicleLetterGuaranteeRecord extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保函交易明细编号,生成规则 BK+yyyyMMdd+HHmmss+5位流水号(例 BK20260415143000001),全库唯一
     */
    @TableField("lg_record_code")
    private String lgRecordCode;

    /**
     * 保函ID
     */
    @TableField("lg_id")
    private String lgId;

    /**
     * 保函编号(冗余)
     */
    @TableField("lg_code")
    private String lgCode;

    /**
     * 保函类型(冗余,字典 letter_guarantee_lg_type)
     */
    @TableField("lg_type")
    private String lgType;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private String customerId;

    /**
     * 变动类型(字典 letter_guarantee_record_type: 10冻结 20解冻 30转应收 40释放应收 50失效)
     */
    @TableField("transaction_type")
    private String transactionType;

    /**
     * 变动金额(始终为正)
     */
    @TableField("transaction_amount")
    private BigDecimal transactionAmount;

    /**
     * 变动说明(如 \"PO单 101提交,资金冻结\")
     */
    @TableField("transaction_remark")
    private String transactionRemark;

    /**
     * 变动前可用订单金额
     */
    @TableField("before_available")
    private BigDecimal beforeAvailable;

    /**
     * 变动后可用订单金额
     */
    @TableField("after_available")
    private BigDecimal afterAvailable;

    /**
     * 变动前占用订单金额
     */
    @TableField("before_occupied")
    private BigDecimal beforeOccupied;

    /**
     * 变动后占用订单金额
     */
    @TableField("after_occupied")
    private BigDecimal afterOccupied;

    /**
     * 变动前应收订单金额(冻结/解冻/失效 时为 NULL,转应收/释放应收 时填值)
     */
    @TableField("before_receivable")
    private BigDecimal beforeReceivable;

    /**
     * 变动后应收订单金额(同上)
     */
    @TableField("after_receivable")
    private BigDecimal afterReceivable;

    /**
     * PO 单号(不允许编辑,根据冻结/解冻情况自动赋值)
     */
    @TableField("po_no")
    private String poNo;

    /**
     * SO 单号(不允许编辑,根据转应收/释放应收情况自动赋值,冻结/解冻 时为 NULL)
     */
    @TableField("so_no")
    private String soNo;

    /**
     * VIN 车辆识别码(不允许编辑,根据转应收情况自动赋值,冻结/解冻 时为 NULL)
     */
    @TableField("vin")
    private String vin;

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
     * 归属用户ID(沿用主保函的归属)
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

    /**
     * 归属门店ID(沿用主保函的归属)
     */
    @TableField("owner_org_id")
    private Long ownerOrgId;


}
