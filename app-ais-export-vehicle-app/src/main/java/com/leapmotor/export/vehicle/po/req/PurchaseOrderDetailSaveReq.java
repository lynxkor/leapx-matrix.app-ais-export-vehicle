package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 4.2 明细生产备注编辑入参
 */
@Data
@Schema(description = "明细生产备注")
public class PurchaseOrderDetailSaveReq {

    @Schema(description = "明细主键ID")
    @NotBlank(message = "明细ID不能为空")
    private String detailId;

    @Schema(description = "生产备注(最多1000字)")
    @Size(max = 1000, message = "生产备注长度不能超过1000")
    private String productionRemark;
}
