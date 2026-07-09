package com.leapmotor.export.vehicle.infrastructure.po.entity;

import java.io.Serializable;
import com.leapmotor.platform.ais.common.mybatis.support.entity.LpBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * <p>
 * PO-保函关联表(一个PO多保函)领域对象
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "export_cbu_vehicle_po_guarantee",autoResultMap = true)
public class ExportCbuVehiclePoGuarantee extends LpBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号,关联主表 export_cbu_vehicle_po.po_no
     */
    @TableField("po_no")
    private String poNo;

    /**
     * 保函ID,关联保函表
     */
    @TableField("guarantee_id")
    private String guaranteeId;

    /**
     * 保函编号
     */
    @TableField("guarantee_number")
    private String guaranteeNumber;

    /**
     * PO当前占用金额,= 该保函的冻结金额
     */
    @TableField("po_current_occupied_amount")
    private BigDecimal poCurrentOccupiedAmount;

    /**
     * PO当前应收金额,= 0
     */
    @TableField("po_current_receivable_amount")
    private BigDecimal poCurrentReceivableAmount;

    /**
     * 状态(1:启用,0:停用),平台规范
     */
    @TableField("status")
    private Integer status;

    /**
     * 状态变更描述
     */
    @TableField("status_desc")
    private String statusDesc;

    /**
     * 归属用户ID
     */
    @TableField("owner_user_id")
    private Long ownerUserId;

    /**
     * 归属门店ID(组织ID)
     */
    @TableField("owner_org_id")
    private Long ownerOrgId;


}
