package com.leapmotor.export.vehicle.payablelist.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 批量提交应付清单请求
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Data
public class PayableListBatchSubmitReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应付清单ID列表")
    @NotEmpty(message = "应付清单ID列表不能为空")
    private List<PayableListSubmitItem> items;

    /**
     * 提交项
     */
    @Data
    public static class PayableListSubmitItem implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "应付清单ID")
        private String id;

        @Schema(description = "版本号")
        private Integer version;
    }
}
