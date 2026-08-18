package com.leapmotor.export.vehicle.po.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 订单编辑保存入参（仅草稿态；仅销售公司 + 明细生产备注可改）
 */
@Data
@Schema(description = "PO订单编辑保存")
public class ExportVehicleCbuPoSaveReq {

    @Schema(description = "PO订单主键ID")
    @NotBlank(message = "PO订单ID不能为空")
    private String poId;

    @Schema(description = "销售公司(抬头,保存时必填)")
    @NotBlank(message = "销售公司不能为空")
    private String salesCompanyCode;

    @Schema(description = "明细生产备注(逐行)")
    @NotEmpty(message = "明细列表不能为空")
    @Valid
    private List<ExportVehicleCbuPoDetailSaveReq> detailList;
}
