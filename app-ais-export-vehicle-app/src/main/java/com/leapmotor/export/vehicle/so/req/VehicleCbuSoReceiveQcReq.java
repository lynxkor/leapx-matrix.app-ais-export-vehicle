package com.leapmotor.export.vehicle.so.req;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 红旗质检结果回传请求
 *
 * <p>外部字符 {@code result} 映射：P→10 通过、N→20 不通过、F→30 未收到车辆。
 */
@Data
public class VehicleCbuSoReceiveQcReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * VIN 码
     */
    private String vin;

    /**
     * 质检结果：P/N/F
     */
    private String result;

    /**
     * 质检说明
     */
    private String remark;

    /**
     * 质检反馈时间
     */
    private LocalDateTime qcTime;
}
