package com.leapmotor.export.vehicle.po.req;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;

/**
 *
 * PO-保函关联表(一个PO多保函)新增/编辑请求对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PurchaseOrderGuaranteeAddOrUpdateReq extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号,关联主表 purchase_order.po_number" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String poNumber;

    @Schema(description = "保函ID,关联保函表" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String guaranteeId;

    @Schema(description = "保函编号" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String guaranteeNumber;

    @Schema(description = "PO当前占用金额,= 该保函的冻结金额" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal poCurrentOccupiedAmount;

    @Schema(description = "PO当前应收金额,= 0" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal poCurrentReceivableAmount;

    @Schema(description = "状态变更描述" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusDesc;

    @Schema(description = "归属用户ID" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerUserId;

    @Schema(description = "归属门店ID(组织ID)" ,requiredMode = Schema.RequiredMode.REQUIRED)
    private Long ownerOrgId;


}
