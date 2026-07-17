package com.leapmotor.export.vehicle.so.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * CBU整车销售订单主表参数对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态变更描述")
    private String statusDesc;

    @Schema(description = "SO单号，后台自动生成")
    private String soNo;

    @Schema(description = "关联PO单ID")
    private String poId;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "关联PO明细ID")
    private String poDetailId;

    @Schema(description = "PO明细编号")
    private String poDetailNo;

    @Schema(description = "行号")
    private Integer lineNo;

    @Schema(description = "销售公司编码")
    private String salesCompanyCode;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "外部客户编码")
    private String externalCustomerCode;

    @Schema(description = "订单类型：1 CBU")
    private Integer orderType;

    @Schema(description = "计划版本号")
    private String planVersion;

    @Schema(description = "期望下线日期")
    private LocalDate expectedOfflineDate;

    @Schema(description = "VIN码年份")
    private String vinYear;

    @Schema(description = "生产备注")
    private String productionRemark;

    @Schema(description = "车辆价格，取自PO单")
    private BigDecimal vehiclePrice;

    @Schema(description = "冻结金额，默认=车辆价格")
    private BigDecimal frozenAmount;

    @Schema(description = "应收金额，质检通过后由冻结金额转入")
    private BigDecimal receivableAmount;

    @Schema(description = "订单状态：1001未下发生产、1002已下发生产、1003排产中、1004焊装上线、1005涂装上线、1006总装上线、1007总装下线、1008质检通过、1009质检未通过、1010质检未收到车、1011已收款、1012已作废")
    private Integer orderStatus;

    @Schema(description = "订单状态列表（批量精确查询）")
    private List<Integer> orderStatuses;

    @Schema(description = "下发生产时间")
    private LocalDateTime issueTime;

    @Schema(description = "作废时间")
    private LocalDateTime cancelTime;

    @Schema(description = "当前质检结果：10通过、20不通过、30未收到车辆")
    private Integer qcResult;

    @Schema(description = "当前质检结果反馈时间")
    private LocalDateTime qcResultTime;

    @Schema(description = "当前质检说明")
    private String qcRemark;

    @Schema(description = "上次质检结果：10通过、20不通过、30未收到车辆；再次反馈时由 updateQcResult 自动将当前结果迁移至此")
    private Integer lastQcResult;

    @Schema(description = "上次质检结果反馈时间")
    private LocalDateTime lastQcResultTime;

    @Schema(description = "上次质检说明")
    private String lastQcRemark;

    @Schema(description = "完成收款时间")
    private LocalDateTime receiveTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "客户名称（模糊查询）")
    private String customerNameLike;

    @Schema(description = "SO单号（模糊查询）")
    private String soNoLike;

    @Schema(description = "PO单号（模糊查询）")
    private String poNoLike;

    @Schema(description = "VIN号（模糊查询）")
    private String vinLike;

    @Schema(description = "产销识别码（模糊查询）")
    private String productionIdentifyCodeLike;

    @Schema(description = "内部车辆号（模糊查询）")
    private String internalVehicleNoLike;

    @Schema(description = "物料编码（模糊查询）")
    private String materialCodeLike;

    @Schema(description = "物料编码列表（批量精确查询）")
    private List<String> materialCodes;

    @Schema(description = "物料描述（模糊查询）")
    private String materialDescriptionLike;

    @Schema(description = "物料描述列表（批量精确查询）")
    private List<String> materialDescriptions;

    @Schema(description = "SO单号列表（批量精确查询）")
    private List<String> soNos;

    @Schema(description = "PO单号列表（批量精确查询）")
    private List<String> poNos;

    @Schema(description = "VIN号列表（批量精确查询）")
    private List<String> vins;

    @Schema(description = "产销识别码列表（批量精确查询）")
    private List<String> productionIdentifyCodes;

    @Schema(description = "内部车辆号列表（批量精确查询）")
    private List<String> internalVehicleNos;

    @Schema(description = "APS最新同步结果：0失败、1成功")
    private Integer syncApsResult;

    @Schema(description = "SAP最新同步结果：0失败、1成功")
    private Integer syncSapResult;

    @Schema(description = "创建时间范围起")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围止")
    private LocalDateTime createTimeEnd;

    @Schema(description = "下发生产时间范围起")
    private LocalDateTime issueTimeStart;

    @Schema(description = "下发生产时间范围止")
    private LocalDateTime issueTimeEnd;

    @Schema(description = "质检反馈时间范围起")
    private LocalDateTime qcResultTimeStart;

    @Schema(description = "质检反馈时间范围止")
    private LocalDateTime qcResultTimeEnd;


}
