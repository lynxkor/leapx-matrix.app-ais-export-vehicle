package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SAP清账结果请求
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class SapClearAccountReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 清账车辆列表
     */
    private List<SapClearAccountItem> clearAccountList;

    /**
     * SAP清账车辆项
     */
    @Data
    public static class SapClearAccountItem implements Serializable {

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
         * 收款时间
         */
        private LocalDateTime receiveTime;
    }
}
