package com.leapmotor.export.vehicle.so.resp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import com.leapmotor.platform.ais.common.web.serializer.DictEnumType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * CBU整车销售订单主表值对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoResp extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态(1:启用,0:停用)")
    private Integer status;

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
    @DictEnumType(prefix = "VehicleCbuSoStatusEnum")
    private Integer orderStatus;

    @Schema(description = "下发生产时间")
    private LocalDateTime issueTime;

    @Schema(description = "作废时间")
    private LocalDateTime cancelTime;

    @Schema(description = "当前质检结果：10通过、20不通过、30未收到车辆")
    @DictEnumType(prefix = "VehicleCbuSoQcResultEnum")
    private Integer qcResult;

    @Schema(description = "当前质检结果反馈时间")
    private LocalDateTime qcResultTime;

    @Schema(description = "当前质检说明")
    private String qcRemark;

    @Schema(description = "上次质检结果：10通过、20不通过、30未收到车辆；再次反馈时由 updateQcResult 自动将当前结果迁移至此")
    @DictEnumType(prefix = "VehicleCbuSoQcResultEnum")
    private Integer lastQcResult;

    @Schema(description = "上次质检结果反馈时间")
    private LocalDateTime lastQcResultTime;

    @Schema(description = "上次质检说明")
    private String lastQcRemark;

    @Schema(description = "完成收款时间")
    private LocalDateTime receiveTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "销售公司名称")
    private String salesCompanyName;

    @Schema(description = "内部车辆号，从APS获取")
    private String internalVehicleNo;

    @Schema(description = "产销识别码，从APS获取")
    private String productionIdentifyCode;

    @Schema(description = "VIN号，从APS获取")
    private String vin;

    @Schema(description = "物料编码，取自PO单")
    private String materialCode;

    @Schema(description = "物料描述")
    private String materialDescription;

    @Schema(description = "销售市场")
    private String salesMarket;

    @Schema(description = "年款")
    private String modelYear;

    @Schema(description = "选装包")
    private String optionalPackage;

    @Schema(description = "排产时间，从APS获取")
    private LocalDateTime scheduleTime;

    @Schema(description = "打印条码时间，从APS获取")
    private LocalDateTime barcodePrintTime;

    @Schema(description = "焊装上线时间，从APS获取")
    private LocalDateTime bodyInTime;

    @Schema(description = "涂装上线时间，从APS获取")
    private LocalDateTime paintInTime;

    @Schema(description = "总装上线时间，从APS获取")
    private LocalDateTime assemblyInTime;

    @Schema(description = "总装下线时间，从APS获取")
    private LocalDateTime assemblyOutTime;

    @Schema(description = "APS最新同步结果：0失败、1成功（下发排产或作废，取最近一次）")
    private Integer syncApsResult;

    @Schema(description = "APS最新同步时间")
    private LocalDateTime syncApsTime;

    @Schema(description = "APS最新返回信息")
    private String syncApsResponseInfo;

    @Schema(description = "SAP最新同步结果：0失败、1成功")
    private Integer syncSapResult;

    @Schema(description = "SAP最新同步时间")
    private LocalDateTime syncSapTime;

    @Schema(description = "SAP最新返回信息")
    private String syncSapResponseInfo;

    @Schema(description = "客户系统最新同步结果：0失败、1成功")
    private Integer syncCustomerResult;

    @Schema(description = "客户系统最新同步时间")
    private LocalDateTime syncCustomerTime;

    @Schema(description = "客户系统最新返回信息")
    private String syncCustomerResponseInfo;


}
