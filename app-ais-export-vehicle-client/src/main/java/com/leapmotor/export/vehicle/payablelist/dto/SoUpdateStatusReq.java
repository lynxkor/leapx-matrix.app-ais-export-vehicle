package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 更新SO状态为已收款请求
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class SoUpdateStatusReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SO订单ID
     */
    private String soId;

    /**
     * 收款时间
     */
    private LocalDateTime receiveTime;
}
