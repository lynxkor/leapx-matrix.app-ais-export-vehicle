package com.leapmotor.export.vehicle.acl.http.sap.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * SO 订单同步 SAP 请求（按接口文档字段占位）。
 */
@Data
public class SapOrderSyncReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户 PO
     */
    private String customerPoNo;

    /**
     * 销服 SO
     */
    private String salesServiceSoNo;

    /**
     * 订单日期
     */
    private LocalDate orderDate;

    /**
     * 订单/物流状态
     */
    private String logisticsStatus;

    /**
     * 订单状态日期
     */
    private LocalDateTime orderStatusDate;

    /**
     * 销售公司
     */
    private String salesCompanyCode;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单分类
     */
    private String orderCategory;

    /**
     * 拆分订单号
     */
    private String splitOrderNo;

    /**
     * VIN
     */
    private String vin;

    /**
     * 物料编码
     */
    private String materialCode;

    /**
     * 订单数量
     */
    private BigDecimal orderQuantity;

    /**
     * EXW 单价
     */
    private BigDecimal exwUnitPrice;
}
