package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 保函释放应收请求
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class LgReleaseReceivableReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保函ID
     */
    private String lgId;

    /**
     * SO单号
     */
    private String soNo;

    /**
     * VIN号
     */
    private String vin;

    /**
     * 释放金额
     */
    private BigDecimal releaseAmount;

    /**
     * 变动说明
     */
    private String remark;
}
