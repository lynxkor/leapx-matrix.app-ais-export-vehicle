package com.leapmotor.export.vehicle.payablelist.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 提交付应付清单请求
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class PayableListSubmitReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应付清单ID")
    private String id;

    @Schema(description = "版本号")
    private Integer version;
}