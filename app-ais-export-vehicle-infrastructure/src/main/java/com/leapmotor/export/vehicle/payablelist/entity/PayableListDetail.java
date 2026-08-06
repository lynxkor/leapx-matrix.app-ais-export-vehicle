package com.leapmotor.export.vehicle.payablelist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应付清单明细表
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("export_vehicle_payable_list_detail")
public class PayableListDetail extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联应付清单ID
     */
    private String payableListId;

    /**
     * 明细编号，规则：YX+年月日+6位流水号
     */
    private String detailNo;

    /**
     * 关联SO订单ID
     */
    private String soId;

    /**
     * SO订单号
     */
    private String soNo;

    /**
     * PO单号
     */
    private String poNo;

    /**
     * VIN号
     */
    private String vin;

    /**
     * 整车物料编码
     */
    private String materialCode;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 车辆价格
     */
    private BigDecimal price;

    /**
     * 质检通过时间
     */
    private LocalDateTime qcPassTime;

    /**
     * 是否收款：0=否/1=是
     */
    private Integer isReceived;

    /**
     * 收款时间
     */
    private LocalDateTime receiveTime;

    /**
     * 归属用户ID，当数据权限归属到用户时
     */
    private Long ownerUserId;

    /**
     * 归属门店ID（组织ID），当数据权限归属到本组织以及本下级
     */
    private Long ownerOrgId;
}