package com.leapmotor.export.vehicle.acl.rpc.hongqi.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 应付清单同步红旗请求
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Data
public class PayableListSyncHqReq implements Serializable {

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
    private List<Detail> details;

    /**
     * 明细信息
     */
    @Data
    public static class Detail implements Serializable {
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
         * 车辆价格
         */
        private BigDecimal price;

        /**
         * 质检通过时间
         */
        private String qcPassTime;
    }
}