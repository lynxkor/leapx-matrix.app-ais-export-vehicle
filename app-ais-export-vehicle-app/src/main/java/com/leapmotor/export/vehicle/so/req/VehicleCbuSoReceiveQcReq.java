package com.leapmotor.export.vehicle.so.req;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 红旗质检结果回传请求
 *
 * <p>直接使用内部状态码：10 通过、20 不通过、30 未收到车辆。
 */
@Data
public class VehicleCbuSoReceiveQcReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * VIN 码
     */
    private String vin;

    /**
     * 质检结果：10 通过、20 不通过、30 未收到车辆
     */
    private Integer result;

    /**
     * 质检说明
     */
    private String remark;

    /**
     * 质检反馈时间
     */
    private LocalDateTime qcTime;
}
