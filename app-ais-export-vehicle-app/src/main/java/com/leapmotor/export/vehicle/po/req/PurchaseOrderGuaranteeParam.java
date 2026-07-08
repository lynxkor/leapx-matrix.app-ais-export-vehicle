package com.leapmotor.export.vehicle.po.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;


/**
 *
 * PO-保函关联表(一个PO多保函)参数对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PurchaseOrderGuaranteeParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号,关联主表 purchase_order.po_number")
    private String poNumber;

    @Schema(description = "保函ID,关联保函表")
    private String guaranteeId;

    @Schema(description = "保函编号")
    private String guaranteeNumber;

    @Schema(description = "PO当前占用金额,= 该保函的冻结金额")
    private BigDecimal poCurrentOccupiedAmount;

    @Schema(description = "PO当前应收金额,= 0")
    private BigDecimal poCurrentReceivableAmount;

    @Schema(description = "状态变更描述")
    private String statusDesc;

    @Schema(description = "归属用户ID")
    private Long ownerUserId;

    @Schema(description = "归属门店ID(组织ID)")
    private Long ownerOrgId;


}
