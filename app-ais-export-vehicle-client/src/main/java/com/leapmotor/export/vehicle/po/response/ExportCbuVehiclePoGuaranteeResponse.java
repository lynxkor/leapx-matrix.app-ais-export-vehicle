package com.leapmotor.export.vehicle.po.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import java.io.Serializable;
import com.leapmotor.paltform.ais.common.model.base.LpBaseResponse;

/**
 *
 * PO-保函关联表(一个PO多保函)响应对象
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
public class ExportCbuVehiclePoGuaranteeResponse extends LpBaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID(UUID 或雪花ID 字符串形式)
     */
    private String id;

    /**
     * PO单号,关联主表 export_cbu_vehicle_po.po_no
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