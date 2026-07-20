package com.leapmotor.export.vehicle.po.req;

import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;


/**
 * PO订单明细表参数对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleCbuPoDetailParam extends LpBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "客户编码（精确匹配）")
    private String customerCode;

    @Schema(description = "PO单号（模糊查询）")
    private String poNoLike;

    @Schema(description = "PO单号列表（批量精确查询）")
    private List<String> poNos;

    @Schema(description = "订单状态列表（多选查询）")
    private List<String> orderStatuses;

    @Schema(description = "PO单号,关联主表 export_vehicle_cbu_po.po_no")
    private String poNo;

    @Schema(description = "PO明细编号,自动生成 POD+年月日+时分+5位流水")
    private String poDetailNo;

    @Schema(description = "订单明细编号,取自红旗CBU接口")
    private String orderDetailNumber;

    @Schema(description = "物料编码（模糊查询）")
    private String materialCodeLike;

    @Schema(description = "物料编码列表（批量精确查询）")
    private List<String> materialCodes;

    @Schema(description = "物料编码,按 sync_material_code 匹配整编主数据;提交时必填")
    private String materialCode;

    @Schema(description = "同步物料编码,红旗18位物料编码")
    private String syncMaterialCode;

    @Schema(description = "物料描述（模糊查询）")
    private String materialDescriptionLike;

    @Schema(description = "物料描述列表（批量精确查询）")
    private List<String> materialDescriptions;

    @Schema(description = "物料描述,关联整编主数据")
    private String materialDescription;

    @Schema(description = "销售市场,关联整编主数据")
    private String salesMarket;

    @Schema(description = "年款,关联整编主数据")
    private String modelYear;

    @Schema(description = "选装包,关联整编主数据")
    private String optionalPackage;

    @Schema(description = "销售市场列表(多选精确查询)")
    private List<String> salesMarketList;

    @Schema(description = "年款列表(多选精确查询)")
    private List<String> modelYearList;

    @Schema(description = "选装包列表(多选精确查询)")
    private List<String> optionalPackageList;

    @Schema(description = "需求数量,取自红旗CBU接口")
    private BigDecimal demandQuantity;

    @Schema(description = "期望下线日期,取自红旗CBU接口")
    private LocalDate expectedOfflineDate;

    @Schema(description = "VIN码年份,取自红旗CBU接口")
    private String vinYear;

    @Schema(description = "生产备注,可编辑,非必填,最多1000字")
    private String productionRemark;

    @Schema(description = "单价,按 物料编码+创建时间+订单类型 匹配价格主数据(取最后审核通过价);提交时必填")
    private BigDecimal unitPrice;

    @Schema(description = "金额合计,= 单价 × 需求数量")
    private BigDecimal totalAmount;

    @Schema(description = "创建时间范围起")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间范围止")
    private LocalDateTime createTimeEnd;

    @Schema(description = "状态变更描述")
    private String statusDesc;

    @Schema(description = "归属用户ID")
    private Long ownerUserId;

    @Schema(description = "归属门店ID(组织ID)")
    private Long ownerOrgId;


}
