package com.leapmotor.export.vehicle.so.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 *
 * CBU整车销售订单主表领域对象
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VehicleCbuSoDo extends LpBaseModel implements Serializable {

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
     * 当前质检结果：{@link VehicleCbuSoQcResultEnum}（10通过、20不通过、30未收到车辆）
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
     * 上次质检结果：{@link VehicleCbuSoQcResultEnum}（10通过、20不通过、30未收到车辆）；
     * 同一 SO 再次反馈质检时由 {@link #updateQcResult(Integer)} 自动将当前结果迁移至此
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

    /**
     * 销售公司名称（JOIN 或外部装配）
     */
    private String salesCompanyName;

    /**
     * 内部车辆号，从APS获取
     */
    private String internalVehicleNo;

    /**
     * 产销识别码，从APS获取
     */
    private String productionIdentifyCode;

    /**
     * VIN号，从APS获取
     */
    private String vin;

    /**
     * 物料编码，取自PO单
     */
    private String materialCode;

    /**
     * 物料描述
     */
    private String materialDescription;

    /**
     * 销售市场
     */
    private String salesMarket;

    /**
     * 年款
     */
    private String modelYear;

    /**
     * 选装包
     */
    private String optionalPackage;

    /**
     * 排产时间，从APS获取
     */
    private LocalDateTime scheduleTime;

    /**
     * 打印条码时间，从APS获取
     */
    private LocalDateTime barcodePrintTime;

    /**
     * 焊装上线时间，从APS获取
     */
    private LocalDateTime bodyInTime;

    /**
     * 涂装上线时间，从APS获取
     */
    private LocalDateTime paintInTime;

    /**
     * 总装上线时间，从APS获取
     */
    private LocalDateTime assemblyInTime;

    /**
     * 总装下线时间，从APS获取
     */
    private LocalDateTime assemblyOutTime;

    /**
     * APS最新同步结果：0失败、1成功（下发排产或作废，取最近一次）
     */
    private Integer syncApsResult;

    /**
     * APS最新同步时间
     */
    private LocalDateTime syncApsTime;

    /**
     * APS最新返回信息
     */
    private String syncApsResponseInfo;

    /**
     * SAP最新同步结果：0失败、1成功
     */
    private Integer syncSapResult;

    /**
     * SAP最新同步时间
     */
    private LocalDateTime syncSapTime;

    /**
     * SAP最新返回信息
     */
    private String syncSapResponseInfo;

    /**
     * 客户系统最新同步结果：0失败、1成功
     */
    private Integer syncCustomerResult;

    /**
     * 客户系统最新同步时间
     */
    private LocalDateTime syncCustomerTime;

    /**
     * 客户系统最新返回信息
     */
    private String syncCustomerResponseInfo;

    /**
     * 下发生产：状态改为已下发生产，记录下发时间。
     *
     * @return 当前对象
     */
    public VehicleCbuSoDo issueToProduction() {
        this.orderStatus = VehicleCbuSoStatusEnum.ISSUED.getCode();
        this.issueTime = LocalDateTime.now();
        return this;
    }

    /**
     * 作废：状态改为已作废，记录作废时间。
     *
     * @return 当前对象
     */
    public VehicleCbuSoDo cancel() {
        this.orderStatus = VehicleCbuSoStatusEnum.CANCELLED.getCode();
        this.cancelTime = LocalDateTime.now();
        return this;
    }

    /**
     * 是否可下发生产（仅未下发生产可下发）。
     */
    public boolean canIssue() {
        return VehicleCbuSoStatusEnum.NOT_ISSUED.getCode().equals(this.orderStatus);
    }

    /**
     * 是否可作废（仅未下发生产、排产中可作废）。
     */
    public boolean canCancel() {
        return VehicleCbuSoStatusEnum.NOT_ISSUED.getCode().equals(this.orderStatus)
                || VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(this.orderStatus);
    }

    /**
     * 是否已作废。
     */
    public boolean isCancelled() {
        return VehicleCbuSoStatusEnum.CANCELLED.getCode().equals(this.orderStatus);
    }

    /**
     * 更新生产状态：仅将 APS 外部状态码映射为订单状态；节点时间由调用方按 APS 回传写入。
     *
     * <p>映射关系：1→1003 排产中、2→1004 焊装上线、3→1005 涂装上线、4→1006 总装上线、5→1007 总装下线。
     *
     * @param apsStatus APS 外部状态码
     * @return 映射后的订单状态码
     */
    public Integer updateProductionStatus(Integer apsStatus) {
        if (apsStatus == null) {
            throw new BizException("APS状态码不能为空");
        }
        VehicleCbuSoStatusEnum target = VehicleCbuSoStatusEnum.fromApsStatus(apsStatus);
        if (target == null) {
            throw new BizException("不支持的APS状态码：" + apsStatus);
        }
        this.orderStatus = target.getCode();
        return target.getCode();
    }

    /**
     * 更新质检结果：非首次反馈时先将当前质检结果复制到上次，再更新当前结果。
     *
     * @param qcResultCode {@link VehicleCbuSoQcResultEnum#getCode()}
     */
    public void updateQcResult(Integer qcResultCode) {
        if (this.qcResult != null) {
            this.lastQcResult = this.qcResult;
            this.lastQcResultTime = this.qcResultTime;
            this.lastQcRemark = this.qcRemark;
        }
        this.qcResult = qcResultCode;
        this.qcResultTime = LocalDateTime.now();
    }

    /**
     * 更新质检结果（含说明）。
     *
     * @param qcResultCode {@link VehicleCbuSoQcResultEnum#getCode()}
     * @param qcRemark     质检说明
     */
    public void updateQcResult(Integer qcResultCode, String qcRemark) {
        updateQcResult(qcResultCode);
        this.qcRemark = qcRemark;
    }

    /**
     * 标记已收款：状态改为 1011，记录收款时间。
     */
    public void updateReceivedStatus() {
        this.orderStatus = VehicleCbuSoStatusEnum.RECEIVED.getCode();
        this.receiveTime = LocalDateTime.now();
    }

}
