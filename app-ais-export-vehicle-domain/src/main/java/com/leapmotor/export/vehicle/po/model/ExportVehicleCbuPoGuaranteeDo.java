package com.leapmotor.export.vehicle.po.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;


/**
 *
 * PO-保函关联表(一个PO多保函)领域对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleCbuPoGuaranteeDo extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号,关联主表 export_vehicle_cbu_po.po_no
     */
    private String poNo;

    /**
     * 保函ID,关联保函表
     */
    private String guaranteeId;

    /**
     * 保函编号
     */
    private String guaranteeNumber;

    /**
     * PO当前占用金额,= 该保函的冻结金额
     */
    private BigDecimal poCurrentOccupiedAmount;

    /**
     * PO当前应收金额,= 0
     */
    private BigDecimal poCurrentReceivableAmount;

    /**
     * 保函状态(字典 letter_guarantee_lg_status,不落关联表,通过保函接口关联)
     */
    private String guaranteeStatus;

    /**
     * 生效日期(不落关联表,通过保函接口关联)
     */
    private LocalDate effectiveDate;

    /**
     * 失效日期(不落关联表,通过保函接口关联)
     */
    private LocalDate expirationDate;

    /**
     * 状态(1:启用,0:停用),平台规范
     */
    private Integer status;

    /**
     * 状态变更描述
     */
    private String statusDesc;

    /**
     * 归属用户ID
     */
    private Long ownerUserId;

    /**
     * 归属门店ID(组织ID)
     */
    private Long ownerOrgId;


}