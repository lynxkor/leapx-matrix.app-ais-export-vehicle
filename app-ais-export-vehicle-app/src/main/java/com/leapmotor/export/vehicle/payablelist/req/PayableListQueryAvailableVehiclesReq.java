package com.leapmotor.export.vehicle.payablelist.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询可添加车辆请求
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Data
public class PayableListQueryAvailableVehiclesReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单ID（排除已绑定到该清单的SO）
     */
    private String payableListId;

    /**
     * VIN号（模糊查询）
     */
    private String vin;

    /**
     * SO订单号（模糊查询）
     */
    private String soNo;
}
