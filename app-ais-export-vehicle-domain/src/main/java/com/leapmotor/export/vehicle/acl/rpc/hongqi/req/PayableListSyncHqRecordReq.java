package com.leapmotor.export.vehicle.acl.rpc.hongqi.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 应付清单同步红旗记录请求（按红旗提供数据对象结构，records 中每条记录同时包含清单头与明细信息）
 */
@Data
public class PayableListSyncHqRecordReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录列表
     */
    private List<Record> records;

    /**
     * 记录信息
     */
    @Data
    public static class Record implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 应付清单编号
         */
        private String payableListNo;

        /**
         * 红旗客户编码
         */
        private String customerCode;

        /**
         * 车辆总数量
         */
        private Integer totalVehicles;

        /**
         * 总金额
         */
        private BigDecimal totalAmount;

        /**
         * 生成月份（YYYYMM）
         */
        private String generateMonth;

        /**
         * 版本号，初始为1
         */
        private Integer version;

        /**
         * PO单号
         */
        private String poNo;

        /**
         * PO单创建时间（YYYY-MM-DD HH:MM:SS）
         */
        private String poCreateTime;

        /**
         * SO单号
         */
        private String soNo;

        /**
         * VIN码
         */
        private String vin;

        /**
         * 18位零跑物料编码
         */
        private String materialCode;

        /**
         * 车辆价格
         */
        private BigDecimal price;

        /**
         * 质检通过时间（YYYY-MM-DD HH:MM:SS）
         */
        private String qcPassTime;
    }
}
