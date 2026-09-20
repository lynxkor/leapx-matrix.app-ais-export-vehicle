package com.leapmotor.export.vehicle.payablelist.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询可添加车辆请求
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PayableListQueryAvailableVehiclesReq extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单ID（排除已绑定到该清单的SO）
     */
    private String payableListId;

    /**
     * VIN号（精确查询）
     */
    private String vin;

    /**
     * SO订单号（精确查询）
     */
    private String soNo;
}
