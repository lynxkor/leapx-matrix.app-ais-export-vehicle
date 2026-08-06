package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 更新SO价格请求
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class SoUpdatePriceReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SO订单ID
     */
    private String soId;

    /**
     * 新价格
     */
    private BigDecimal newPrice;
}
