package com.leapmotor.export.vehicle.so.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CBU SO 创建入参（PO 供需审核通过后调用）
 */
@Data
@Schema(description = "CBU SO 创建入参")
public class SoCreateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "PO单号")
    private String poNo;

    @Schema(description = "PO单ID")
    private String poId;

    @Schema(description = "销售公司编码")
    private String salesCompanyCode;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "外部客户编码")
    private String externalCustomerCode;

    @Schema(description = "订单类型：1 CBU")
    private Integer orderType;

    @Schema(description = "计划版本号")
    private String planVersion;

    @Schema(description = "接单时间，取 PO 主单创建时间")
    private LocalDateTime receiveOrderTime;

    @Schema(description = "数据来源，枚举:1销服/2红旗")
    private Integer dataSource;

    @Schema(description = "PO 明细列表（一车一单拆分来源）")
    private List<SoDetailReq> details;

    @Data
    @Schema(description = "CBU SO 创建明细入参")
    public static class SoDetailReq implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "PO明细编号")
        private String poDetailNo;

        @Schema(description = "PO明细ID")
        private String poDetailId;

        @Schema(description = "需求数量")
        private BigDecimal demandQuantity;

        @Schema(description = "车辆价格（取自PO明细unitPrice）")
        private BigDecimal unitPrice;

        @Schema(description = "VIN码年份")
        private String vinYear;

        @Schema(description = "期望下线日期")
        private LocalDate expectedOfflineDate;

        @Schema(description = "生产备注")
        private String productionRemark;

        @Schema(description = "物料编码")
        private String materialCode;

        @Schema(description = "物料描述")
        private String materialDescription;
    }
}
