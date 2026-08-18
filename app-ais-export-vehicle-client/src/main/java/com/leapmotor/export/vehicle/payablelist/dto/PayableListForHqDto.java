package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 推送给红旗的应付清单DTO
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class PayableListForHqDto implements Serializable {

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
     * 生成月份
     */
    private String generateMonth;

    /**
     * 车辆总数量
     */
    private Integer totalVehicles;

    /**
     * 金额合计
     */
    private BigDecimal totalAmount;

    /**
     * 明细列表
     */
    private List<PayableListDetailForHqDto> details;

    /**
     * 推送给红旗的明细DTO
     */
    @Data
    public static class PayableListDetailForHqDto implements Serializable {

        private static final long serialVersionUID = 1L;

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
         * 车辆价格
         */
        private BigDecimal price;

        /**
         * 质检通过时间
         */
        private LocalDateTime qcPassTime;
    }
}
