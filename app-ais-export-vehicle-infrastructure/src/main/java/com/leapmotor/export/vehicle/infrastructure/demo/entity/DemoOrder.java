package com.leapmotor.export.vehicle.infrastructure.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 整车订单示例表 实体
 * </p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "demo_order", autoResultMap = true)
public class DemoOrder extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 订单类型（CBU/SKD/CKD）
     */
    @TableField("order_type")
    private String orderType;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
