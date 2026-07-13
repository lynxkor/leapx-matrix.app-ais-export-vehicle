package com.leapmotor.export.vehicle.so.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * CBU整车销售订单主表数据传输对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
public class VehicleCbuSoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态(1:启用,0:停用)
     */
    private Integer status;

    /**
     * 状态变更描述
     */
    private String statusDesc;

    /**
     * SO单号，后台自动生成
     */
    private String soNo;

    /**
     * 关联PO单ID
     */
    private String poId;

    /**
     * PO单号
     */
    private String poNo;

    /**
     * 关联PO明细ID
     */
    private String poDetailId;

    /**
     * PO明细编号
     */
    private String poDetailNo;

    /**
     * 行号
     */
    private Integer lineNo;

    /**
     * 销售公司编码
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
     * 外部客户编码
     */
    private String externalCustomerCode;

    /**
     * 订单类型：1 CBU
     */
    private Integer orderType;

    /**
     * 计划版本号
     */
    private String planVersion;

    /**
     * 期望下线日期
     */
    private LocalDate expectedOfflineDate;

    /**
     * VIN码年份
     */
    private String vinYear;

    /**
     * 生产备注
     */
    private String productionRemark;

    /**
     * 车辆价格，取自PO单
     */
    private BigDecimal vehiclePrice;

    /**
     * 冻结金额，默认=车辆价格
     */
    private BigDecimal frozenAmount;

    /**
     * 应收金额，质检通过后由冻结金额转入
     */
    private BigDecimal receivableAmount;

    /**
     * 订单状态：1001未下发生产、1002已下发生产、1003排产中、1004焊装上线、1005涂装上线、1006总装上线、1007总装下线、1008质检通过、1009质检未通过、1010质检未收到车、1011已收款、1012已作废
     */
    private Integer orderStatus;

    /**
     * 下发生产时间
     */
    private LocalDateTime issueTime;

    /**
     * 作废时间
     */
    private LocalDateTime cancelTime;

    /**
     * 当前质检结果
     */
    private Integer qcResult;

    /**
     * 当前质检结果反馈时间
     */
    private LocalDateTime qcResultTime;

    /**
     * 当前质检说明
     */
    private String qcRemark;

    /**
     * 上次质检结果
     */
    private Integer lastQcResult;

    /**
     * 上次质检结果反馈时间
     */
    private LocalDateTime lastQcResultTime;

    /**
     * 上次质检说明
     */
    private String lastQcRemark;

    /**
     * 完成收款时间
     */
    private LocalDateTime receiveTime;

    /**
     * 备注
     */
    private String remark;


}