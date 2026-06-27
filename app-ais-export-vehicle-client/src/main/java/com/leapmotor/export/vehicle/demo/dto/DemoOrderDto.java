package com.leapmotor.export.vehicle.demo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 整车订单示例 数据传输对象（Feign 调用）
 */
@Data
public class DemoOrderDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String id;

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
