package com.leapmotor.export.vehicle.po.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * PO订单状态(对应字段 order_status)
 *
 * <p>状态机(详见技术评审 00-总览 §2):
 * <pre>
 *   草稿(10) ──销售提交──▶ 销售已提交(20) ──供需通过──▶ 供需审核通过(40) ──产销平衡通过──▶ 产销平衡通过(60,终态)
 *      │                        │                          │
 *      └──销售驳回──▶ 销售已驳回(30,终态)                 └──供需驳回──▶ 供需审核驳回(50,终态)
 *                                                         │
 *                                供需审核通过(40) ──产销平衡驳回──▶ 产销平衡驳回(70,终态)
 * </pre>
 * 终态: 销售已驳回(30) / 供需审核驳回(50) / 产销平衡驳回(70) / 产销平衡通过(60)。
 * 红旗重推按 4.1 创建为新订单(新 po_no),原驳回单不复活。
 */
public enum ExportVehicleCbuPoStatusEnum implements IBaseEnum<String> {

    DRAFT                             ("10", "草稿"),
    SALES_SUBMITTED                   ("20", "销售已提交"),
    SALES_REJECTED                    ("30", "销售已驳回"),
    SUPPLY_DEMAND_APPROVED            ("40", "供需审核通过"),
    SUPPLY_DEMAND_REJECTED            ("50", "供需审核驳回"),
    PRODUCTION_SALES_BALANCE_APPROVED ("60", "产销平衡通过"),
    PRODUCTION_SALES_BALANCE_REJECTED ("70", "产销平衡驳回");

    private final String code;
    private final String message;

    ExportVehicleCbuPoStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode()    { return code; }
    @Override public String getMessage() { return message; }

    public static ExportVehicleCbuPoStatusEnum of(String code) {
        for (ExportVehicleCbuPoStatusEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}
