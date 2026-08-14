package com.leapmotor.export.vehicle.payablelist.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 导入最新价格请求
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Data
public class PayableListImportPriceReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应付清单ID
     */
    @Schema(description = "应付清单ID")
    @NotBlank(message = "应付清单ID不能为空")
    private String payableListId;

    /**
     * 乐观锁版本号
     */
    @Schema(description = "乐观锁版本号")
    @NotNull(message = "版本号不能为空")
    private Integer version;

    /**
     * Excel数据JSON字符串
     */
    @Schema(description = "Excel数据JSON字符串（datacenter解析Excel后注入）")
    @NotBlank(message = "导入数据不能为空")
    private String excel;
}
