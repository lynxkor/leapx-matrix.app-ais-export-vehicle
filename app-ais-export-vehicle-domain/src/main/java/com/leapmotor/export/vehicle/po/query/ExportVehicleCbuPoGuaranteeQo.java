package com.leapmotor.export.vehicle.po.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;


/**
 *
 * PO-保函关联表(一个PO多保函)查询对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleCbuPoGuaranteeQo extends LpBaseQo implements Serializable {

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
