package com.leapmotor.export.vehicle.so.req;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * CBU整车销售订单主表新增/编辑请求对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoAddOrUpdateReq extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态变更描述", requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusDesc;

    @Schema(description = "SO单号，后台自动生成", requiredMode = Schema.RequiredMode.REQUIRED)
    private String soNo;

    @Schema(description = "关联PO单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poId;

    @Schema(description = "PO单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNo;

    @Schema(description = "关联PO明细ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poDetailId;

    @Schema(description = "PO明细编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String poDetailNo;

    @Schema(description = "行号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lineNo;

    @Schema(description = "销售公司编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String salesCompanyCode;

    @Schema(description = "客户编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerName;

    @Schema(description = "外部客户编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String externalCustomerCode;

    @Schema(description = "订单类型：1 CBU", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderType;

    @Schema(description = "计划版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String planVersion;

    @Schema(description = "期望下线日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate expectedOfflineDate;

    @Schema(description = "VIN码年份", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vinYear;

    @Schema(description = "生产备注", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productionRemark;

    @Schema(description = "车辆价格，取自PO单", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal vehiclePrice;

    @Schema(description = "冻结金额，默认=车辆价格", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal frozenAmount;

    @Schema(description = "应收金额，质检通过后由冻结金额转入", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal receivableAmount;

    @Schema(description = "订单状态：1001未下发生产、1002已下发生产、1003排产中、1004焊装上线、1005涂装上线、1006总装上线、1007总装下线、1008质检通过、1009质检未通过、1010质检未收到车、1011已收款、1012已作废", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderStatus;

    @Schema(description = "下发生产时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime issueTime;

    @Schema(description = "作废时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime cancelTime;

    @Schema(description = "当前质检结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer qcResult;

    @Schema(description = "当前质检结果反馈时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime qcResultTime;

    @Schema(description = "当前质检说明", requiredMode = Schema.RequiredMode.REQUIRED)
    private String qcRemark;

    @Schema(description = "上次质检结果", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer lastQcResult;

    @Schema(description = "上次质检结果反馈时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime lastQcResultTime;

    @Schema(description = "上次质检说明", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastQcRemark;

    @Schema(description = "完成收款时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime receiveTime;

    @Schema(description = "备注", requiredMode = Schema.RequiredMode.REQUIRED)
    private String remark;


}
