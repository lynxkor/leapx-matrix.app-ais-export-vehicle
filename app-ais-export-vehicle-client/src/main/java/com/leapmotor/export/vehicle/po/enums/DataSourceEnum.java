package com.leapmotor.export.vehicle.po.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 数据来源枚举（data_source）。
 *
 * @author leapmotor
 * @since 2026-07-13
 */
public enum DataSourceEnum implements IBaseEnum<Integer> {

    SALES_SERVICE(1, "销服"),
    HONGQI(2, "红旗");

    private final Integer code;
    private final String message;

    DataSourceEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
