package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SO订单用于应付清单的DTO
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class SoOrderForPayableDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SO订单ID
     */
    private String soId;

    /**
     * SO订单号
     */
    private String soNo;

    /**
     * PO单号
     */
    private String poNo;

    /**
     * VIN号
     */
    private String vin;

    /**
     * 整车物料编码
     */
    private String materialCode;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 车辆价格
     */
    private BigDecimal vehiclePrice;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 应收金额
     */
    private BigDecimal receivableAmount;

    /**
     * 质检通过时间
     */
    private LocalDateTime qcResultTime;

    /**
     * PO创建时间
     */
    private LocalDateTime poCreateTime;

    /**
     * 销售市场
     */
    private String saleMarket;

    /**
     * 年款
     */
    private String modelYear;

    /**
     * 选装包
     */
    private String optionPackage;
}
