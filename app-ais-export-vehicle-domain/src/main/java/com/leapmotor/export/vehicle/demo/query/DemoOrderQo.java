package com.leapmotor.export.vehicle.demo.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 整车订单示例查询对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DemoOrderQo extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单类型（CBU/SKD/CKD）
     */
    private String orderType;

    /**
     * 备注
     */
    private String remark;
}
