package com.leapmotor.export.vehicle.acl.http.so.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SO 创建入参（契约待 SO 侧，字段先按评审 04 节点 SO 单字段占位）
 */
@Data
public class SoCreateReq {

    private String poNo;                  // PO单号
    private Integer orderType;             // 订单类型(CBU/SKD/CKD)
    private String salesCompanyCode;          // 销售公司
    private String customerCode;          // 客户编码
    private String customerName;          // 客户名称
    private String externalCustomerCode;  // 外部客户编码
    private String planVersion;           // 计划版本号
    private List<SoDetailReq> details;    // 明细（一车一单，按需求量拆 SO）

    @Data
    public static class SoDetailReq {
        private String poDetailNo;        // PO明细编号
        private BigDecimal demandQuantity;     // 需求数量
        private BigDecimal unitPrice;          // 车辆价格 = 明细单价
        private String vinYear;                // VIN码年份
        private String expectedOfflineDate;    // 期望下线日期
        private String productionRemark;       // 生产备注
    }
}
