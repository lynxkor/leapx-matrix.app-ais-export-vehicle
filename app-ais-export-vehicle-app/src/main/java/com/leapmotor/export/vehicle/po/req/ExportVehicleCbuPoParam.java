package com.leapmotor.export.vehicle.po.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;


/**
 * PO订单主表参数对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleCbuPoParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号（模糊查询）")
    private String poNoLike;

    @Schema(description = "PO单号列表（批量精确查询）")
    private List<String> poNos;

    @Schema(description = "订单状态列表（多选查询）")
    private List<String> orderStatuses;

    @Schema(description = "销售公司编码")
    private String salesCompanyCode;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户名称（模糊查询）")
    private String customerNameLike;

    @Schema(description = "提交时间范围起")
    private LocalDateTime submitTimeStart;

    @Schema(description = "提交时间范围止")
    private LocalDateTime submitTimeEnd;

    @Schema(description = "创建时间范围起")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围止")
    private LocalDateTime createTimeEnd;

    @Schema(description = "产销平衡通过时间范围起")
    private LocalDateTime productionSalesBalanceApproveTimeStart;

    @Schema(description = "产销平衡通过时间范围止")
    private LocalDateTime productionSalesBalanceApproveTimeEnd;

    @Schema(description = "供需审核通过时间范围起")
    private LocalDateTime supplyDemandApproveTimeStart;

    @Schema(description = "供需审核通过时间范围止")
    private LocalDateTime supplyDemandApproveTimeEnd;

}
