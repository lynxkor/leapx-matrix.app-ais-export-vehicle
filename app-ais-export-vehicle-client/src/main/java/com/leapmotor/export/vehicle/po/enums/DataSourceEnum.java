package com.leapmotor.export.vehicle.po.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 数据来源枚举（data_source）。
 *
 * @author leapmotor
 * @since 2026-07-13
 */
public enum DataSourceEnum implements IBaseEnum<String> {

    HONGQI("红旗", "红旗CBU订单"),
    SALES_SERVICE("销服", "销服订单");

    private final String code;
    private final String message;

    DataSourceEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
