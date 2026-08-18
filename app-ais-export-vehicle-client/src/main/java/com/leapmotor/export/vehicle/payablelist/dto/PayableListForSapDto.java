package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 推送给SAP的应付清单DTO
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class PayableListForSapDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单编号
     */
    private String payableListNo;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 金额合计
     */
    private BigDecimal totalAmount;

    /**
     * 明细列表
     */
    private List<PayableListDetailForSapDto> details;

    /**
     * 推送给SAP的明细DTO
     */
    @Data
    public static class PayableListDetailForSapDto implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * SO订单号
         */
        private String soNo;

        /**
         * VIN号
         */
        private String vin;

        /**
         * 车辆价格
         */
        private BigDecimal price;
    }
}
