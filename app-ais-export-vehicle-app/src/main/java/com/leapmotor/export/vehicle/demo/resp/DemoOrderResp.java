package com.leapmotor.export.vehicle.demo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 整车订单示例 响应
 */
@Data
@Schema(description = "整车订单示例 响应")
public class DemoOrderResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单类型（CBU/SKD/CKD）")
    private String orderType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
