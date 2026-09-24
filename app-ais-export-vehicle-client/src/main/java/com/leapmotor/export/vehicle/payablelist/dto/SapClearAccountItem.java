package com.leapmotor.export.vehicle.payablelist.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SAP清账车辆项
 *
 *
 * @author huangtao
 * @since 2026-07-20
 */
@Data
public class SapClearAccountItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单编号
     */
    @JsonProperty("paynr")
    @JSONField(name = "paynr")
    private String paynr;

    /**
     * PO订单号
     */
    @JsonProperty("porder")
    @JSONField(name = "porder")
    private String porder;

    /**
     * SO订单号
     */
    @JsonProperty("sorder")
    @JSONField(name = "sorder")
    private String sorder;

    /**
     * 车辆VIN号
     */
    @JsonProperty("vhvin")
    @JSONField(name = "vhvin")
    private String vhvin;

    /**
     * 清账日期，格式 yyyy-MM-dd
     */
    @JsonProperty("augdt")
    @JSONField(name = "augdt")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime augdt;
}
