package com.leapmotor.export.vehicle.infrastructure.payablelist.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
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
@TableName(value = "export_vehicle_payable_list_detail", autoResultMap = true)
public class PayableListDetail extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联应付清单ID
     */
    @TableField("payable_list_id")
    private String payableListId;

    /**
     * 明细编号，规则：YX+年月日+6位流水号
     */
    @TableField("detail_no")
    private String detailNo;

    /**
     * 关联SO订单ID
     */
    @TableField("so_id")
    private String soId;

    /**
     * SO订单号
     */
    @TableField("so_no")
    private String soNo;

    /**
     * PO单号
     */
    @TableField("po_no")
    private String poNo;

    /**
     * VIN号
     */
    @TableField("vin")
    private String vin;

    /**
     * 整车物料编码
     */
    @TableField("material_code")
    private String materialCode;

    /**
     * 物料描述
     */
    @TableField("material_desc")
    private String materialDesc;

    /**
     * 车辆价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 质检通过时间
     */
    @TableField("qc_pass_time")
    private LocalDateTime qcPassTime;

    /**
     * 是否收款：0=否/1=是
     */
    @TableField("is_received")
    private Integer isReceived;

    /**
     * 收款时间
     */
    @TableField("receive_time")
    private LocalDateTime receiveTime;

    /**
     * 归属用户ID，当数据权限归属到用户时
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

    /**
     * 归属门店ID（组织ID），当数据权限归属到本组织以及本下级
     */
    @TableField("owner_org_id")
    private Long ownerOrgId;
}
