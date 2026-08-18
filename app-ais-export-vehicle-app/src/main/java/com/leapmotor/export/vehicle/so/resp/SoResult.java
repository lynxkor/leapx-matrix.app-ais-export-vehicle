package com.leapmotor.export.vehicle.so.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * CBU SO 创建结果
 *
 * <p>结构上表达"一个 PO 对应多个 SO"的关系：PO 标识放顶层避免在每个 SoItem 重复。
 */
@Data
@Schema(description = "CBU SO 创建结果")
public class SoResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单ID")
    private String poId;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "生成的 SO 列表")
    private List<SoItem> data;

    @Data
    @Schema(description = "CBU SO 创建结果项")
    public static class SoItem implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "SO主键ID")
        private String soId;

        @Schema(description = "SO单号")
        private String soNo;

        @Schema(description = "行号")
        private Integer lineNo;

        @Schema(description = "订单状态")
        private Integer orderStatus;
    }
}
