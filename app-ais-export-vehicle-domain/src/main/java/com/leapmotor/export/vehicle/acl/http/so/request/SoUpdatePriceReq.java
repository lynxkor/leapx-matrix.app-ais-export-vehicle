package com.leapmotor.export.vehicle.acl.http.so.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * SO 更新车辆价格/冻结金额请求（T09 刷新价格联动，契约待 SO 侧提供，先占位）。
 *
 * @author leapmotor
 * @since 2026-07-14
 */
@Data
public class SoUpdatePriceReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /** PO单号 */
    private String poNo;

    /** 明细价格列表（一车一线） */
    private List<DetailPrice> details;

    @Data
    public static class DetailPrice implements Serializable {
        private static final long serialVersionUID = 1L;

        /** PO明细编号 */
        private String poDetailNo;

        /** 新单价 */
        private BigDecimal unitPrice;

        /** 新金额合计 */
        private BigDecimal totalAmount;
    }
}
