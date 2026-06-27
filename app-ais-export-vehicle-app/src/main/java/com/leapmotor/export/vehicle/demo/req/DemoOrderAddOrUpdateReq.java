package com.leapmotor.export.vehicle.demo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 整车订单示例 新增/编辑请求
 */
@Data
@Schema(description = "整车订单示例 新增/编辑请求")
public class DemoOrderAddOrUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID（编辑时必填）")
    private String id;

    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "订单类型（CBU/SKD/CKD）")
    private String orderType;

    @Schema(description = "备注")
    private String remark;
}
