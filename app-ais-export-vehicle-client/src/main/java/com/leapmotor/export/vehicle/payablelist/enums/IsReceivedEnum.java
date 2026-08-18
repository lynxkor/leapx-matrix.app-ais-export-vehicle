package com.leapmotor.export.vehicle.payablelist.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 是否收款枚举
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public enum IsReceivedEnum implements IBaseEnum<Integer> {

    /**
     * 否
     */
    NO(0, "否"),

    /**
     * 是
     */
    YES(1, "是");

    private final Integer code;
    private final String message;

    IsReceivedEnum(Integer code, String message) {
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

    @Override
    public String getDictType() {
        return "YesNoEnum";
    }
}
