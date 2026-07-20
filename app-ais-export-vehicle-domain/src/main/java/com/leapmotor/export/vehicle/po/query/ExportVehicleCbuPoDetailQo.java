package com.leapmotor.export.vehicle.po.query;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Data;
import java.io.Serializable;


/**
 * PO订单明细表查询对象
 * @author leapmotor
 * @since 2026-07-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExportVehicleCbuPoDetailQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 客户编码（主表字段，需子查询） */
    private String customerCode;

    /** PO单号（模糊查询） */
    private String poNoLike;

    /** PO单号列表（批量精确查询） */
    private List<String> poNos;

    /** 订单状态列表（主表字段，需子查询） */
    private List<String> orderStatuses;

    /** PO单号（精确匹配） */
    private String poNo;

    /** PO明细编号 */
    private String poDetailNo;

    /** 订单明细编号 */
    private String orderDetailNumber;

    /** 物料编码（模糊查询） */
    private String materialCodeLike;

    /** 物料编码列表（批量精确查询） */
    private List<String> materialCodes;

    /** 物料编码（精确匹配） */
    private String materialCode;

    /** 同步物料编码 */
    private String syncMaterialCode;

    /** 物料描述（模糊查询） */
    private String materialDescriptionLike;

    /** 物料描述列表（批量精确查询） */
    private List<String> materialDescriptions;

    /** 物料描述（精确匹配） */
    private String materialDescription;

    /** 销售市场 */
    private String salesMarket;

    /** 年款 */
    private String modelYear;

    /** 选装包 */
    private String optionalPackage;

    /** 销售市场列表(多选精确查询,选项取自物料主数据 sales_market 去重值) */
    private List<String> salesMarketList;

    /** 年款列表(多选精确查询,选项取自物料主数据 year_type 去重值,明细列 model_year) */
    private List<String> modelYearList;

    /** 选装包列表(多选精确查询,选项取自物料主数据 option_package 去重值,明细列 optional_package) */
    private List<String> optionalPackageList;

    /** 需求数量 */
    private BigDecimal demandQuantity;

    /** 期望下线日期 */
    private LocalDate expectedOfflineDate;

    /** VIN码年份 */
    private String vinYear;

    /** 生产备注 */
    private String productionRemark;

    /** 单价 */
    private BigDecimal unitPrice;

    /** 金额合计 */
    private BigDecimal totalAmount;

    /** 创建时间范围起 */
    private LocalDateTime createTimeStart;

    /** 创建时间范围止 */
    private LocalDateTime createTimeEnd;

    /** 状态变更描述 */
    private String statusDesc;

    /** 归属用户ID */
    private Long ownerUserId;

    /** 归属门店ID(组织ID) */
    private Long ownerOrgId;


}
