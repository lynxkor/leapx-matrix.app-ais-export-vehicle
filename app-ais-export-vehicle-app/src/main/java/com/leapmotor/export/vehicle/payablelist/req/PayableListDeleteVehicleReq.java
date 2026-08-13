package com.leapmotor.export.vehicle.payablelist.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 应付清单删除车辆请求
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Data
public class PayableListDeleteVehicleReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应付清单ID")
    @NotBlank(message = "应付清单ID不能为空")
    private String payableListId;

    @Schema(description = "明细ID列表")
    @NotEmpty(message = "明细ID列表不能为空")
    private List<String> detailIds;

    @Schema(description = "版本号")
    private Integer version;
}
