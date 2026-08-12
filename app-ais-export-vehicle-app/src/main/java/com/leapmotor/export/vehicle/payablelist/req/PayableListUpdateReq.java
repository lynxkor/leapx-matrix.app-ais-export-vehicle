package com.leapmotor.export.vehicle.payablelist.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 应付清单基础信息更新请求
 *
 * @author 赵吴涯
 * @since 2026-08-12
 */
@Data
public class PayableListUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应付清单ID")
    @NotBlank(message = "应付清单ID不能为空")
    private String id;

    @Schema(description = "版本号（乐观锁）")
    @NotNull(message = "版本号不能为空")
    private Integer version;

    @Schema(description = "客户编码")
    @NotBlank(message = "客户编码不能为空")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;
}
