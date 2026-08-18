package com.leapmotor.export.vehicle.payablelist.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询可添加车辆查询对象
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Data
public class PayableListAvailableVehicleQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单ID（排除已绑定到该清单的SO）
     */
    private String payableListId;

    /**
     * 客户编码（筛选指定客户的SO）
     */
    private String customerCode;

    /**
     * VIN号（模糊查询）
     */
    private String vinLike;

    /**
     * SO订单号（模糊查询）
     */
    private String soNoLike;
}
