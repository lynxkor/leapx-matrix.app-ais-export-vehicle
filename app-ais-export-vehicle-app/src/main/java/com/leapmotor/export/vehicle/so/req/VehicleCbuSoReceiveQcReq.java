package com.leapmotor.export.vehicle.so.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "VIN码不能为空")
    private String vin;

    /**
     * 质检结果：10 通过、20 不通过、30 未收到车辆
     */
    @NotNull(message = "质检结果不能为空")
    private Integer result;

    /**
     * 质检说明
     */
    private String remark;

    /**
     * 质检反馈时间
     */
    @NotNull(message = "质检反馈时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime qcTime;
}
