package com.leapmotor.export.vehicle.infrastructure.pricechangerecord.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_vehicle_price_change_record", autoResultMap = true)
public class PriceChangeRecord extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("serial_no")
    private String serialNo;

    @TableField("vin")
    private String vin;

    @TableField("so_no")
    private String soNo;

    @TableField("before_price")
    private BigDecimal beforePrice;

    @TableField("after_price")
    private BigDecimal afterPrice;

    @TableField("payable_list_no")
    private String payableListNo;

    @TableField("status_desc")
    private String statusDesc;

    @TableField("status")
    private Integer status;

    @TableField("owner_user_id")
    private Long ownerUserId;

    @TableField("owner_org_id")
    private Long ownerOrgId;
}
