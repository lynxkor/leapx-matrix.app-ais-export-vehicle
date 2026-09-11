package com.leapmotor.export.vehicle.payablelist.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SAP清账结果请求
 *
 * <p>行项目按 SAP 提供的参数名（PAYNR/PORDER/SORDER/VHVIN/AUGDT）映射。
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
         * 应付清单编号
         */
        @JsonProperty("PAYNR")
        @JSONField(name = "PAYNR")
        private String paynr;

        /**
         * PO订单号
         */
        @JsonProperty("PORDER")
        @JSONField(name = "PORDER")
        private String porder;

        /**
         * SO订单号
         */
        @JsonProperty("SORDER")
        @JSONField(name = "SORDER")
        private String sorder;

        /**
         * 车辆VIN号
         */
        @JsonProperty("VHVIN")
        @JSONField(name = "VHVIN")
        private String vhvin;

        /**
         * 清账日期，格式 yyyy-MM-dd HH:mm:ss
         */
        @JsonProperty("AUGDT")
        @JSONField(name = "AUGDT")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime augdt;
    }
}
