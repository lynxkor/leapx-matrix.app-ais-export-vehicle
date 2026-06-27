package com.leapmotor.export.vehicle.demo.model;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 整车订单示例领域对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DemoOrderDo extends LpBaseModel implements Serializable {

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
