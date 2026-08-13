package com.leapmotor.export.vehicle.payablelist.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应付清单明细分页查询参数
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PayableListDetailPageParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联应付清单ID")
    private String payableListId;

    @Schema(description = "SO订单号")
    private String soNo;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "VIN号")
    private String vin;

    @Schema(description = "是否收款：0=否/1=是")
    private Integer isReceived;

    @Schema(description = "质检通过时间开始")
    private LocalDateTime qcPassTimeStart;

    @Schema(description = "质检通过时间结束")
    private LocalDateTime qcPassTimeEnd;
}