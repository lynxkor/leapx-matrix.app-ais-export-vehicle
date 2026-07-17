package com.leapmotor.export.vehicle.infrastructure.so.entity;

import java.io.Serializable;

import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.platform.ais.common.mybatis.annotation.AutoCode;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * CBU整车销售订单主表领域对象
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_vehicle_cbu_so", autoResultMap = true)
public class VehicleCbuSo extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * SO单号，后台自动生成
     */
    @TableField("so_no")
    @AutoCode(prefix = "SO")
    private String soNo;

    /**
     * 关联PO单ID
     */
    @TableField("po_id")
    private String poId;

    /**
     * PO单号
     */
    @TableField("po_no")
    private String poNo;

    /**
     * 关联PO明细ID
     */
    @TableField("po_detail_id")
    private String poDetailId;

    /**
     * PO明细编号
     */
    @TableField("po_detail_no")
    private String poDetailNo;

    /**
     * 行号
     */
    @TableField("line_no")
    private Integer lineNo;

    /**
     * 销售公司编码
     */
    @TableField("sales_company_code")
    private String salesCompanyCode;

    /**
     * 客户编码
     */
    @TableField("customer_code")
    private String customerCode;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customerName;

    /**
     * 外部客户编码
     */
    @TableField("external_customer_code")
    private String externalCustomerCode;

    /**
     * 订单类型：1 CBU
     */
    @TableField("order_type")
    private Integer orderType;

    /**
     * 计划版本号
     */
    @TableField("plan_version")
    private String planVersion;

    /**
     * 期望下线日期
     */
    @TableField("expected_offline_date")
    private LocalDate expectedOfflineDate;

    /**
     * VIN码年份
     */
    @TableField("vin_year")
    private String vinYear;

    /**
     * 生产备注
     */
    @TableField("production_remark")
    private String productionRemark;

    /**
     * 车辆价格，取自PO单
     */
    @TableField("vehicle_price")
    private BigDecimal vehiclePrice;

    /**
     * 冻结金额，默认=车辆价格
     */
    @TableField("frozen_amount")
    private BigDecimal frozenAmount;

    /**
     * 应收金额，质检通过后由冻结金额转入
     */
    @TableField("receivable_amount")
    private BigDecimal receivableAmount;

    /**
     * 订单状态：1001未下发生产、1002已下发生产、1003排产中、1004焊装上线、1005涂装上线、1006总装上线、1007总装下线、1008质检通过、1009质检未通过、1010质检未收到车、1011已收款、1012已作废
     */
    @TableField("order_status")
    private Integer orderStatus;

    /**
     * 下发生产时间
     */
    @TableField("issue_time")
    private LocalDateTime issueTime;

    /**
     * 作废时间
     */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 当前质检结果：{@link VehicleCbuSoQcResultEnum}（10通过、20不通过、30未收到车辆）
     */
    @TableField("qc_result")
    private Integer qcResult;

    /**
     * 当前质检结果反馈时间
     */
    @TableField("qc_result_time")
    private LocalDateTime qcResultTime;

    /**
     * 当前质检说明
     */
    @TableField("qc_remark")
    private String qcRemark;

    /**
     * 上次质检结果：{@link VehicleCbuSoQcResultEnum}（10通过、20不通过、30未收到车辆）；
     * 同一 SO 再次反馈质检时由 updateQcResult 自动将当前结果迁移至此
     */
    @TableField("last_qc_result")
    private Integer lastQcResult;

    /**
     * 上次质检结果反馈时间
     */
    @TableField("last_qc_result_time")
    private LocalDateTime lastQcResultTime;

    /**
     * 上次质检说明
     */
    @TableField("last_qc_remark")
    private String lastQcRemark;

    /**
     * 完成收款时间
     */
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
