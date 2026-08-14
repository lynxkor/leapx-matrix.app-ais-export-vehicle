package com.leapmotor.export.vehicle.payablelist.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 红旗同步结果枚举
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public enum SyncResultEnum implements IBaseEnum<String> {

    /**
     * 成功
     */
    SUCCESS("SUCCESS", "成功"),

    /**
     * 失败
     */
    FAIL("FAIL", "失败");

    private final String code;
    private final String message;

    SyncResultEnum(String code, String message) {
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

    @Override
    public String getDictType() {
        return "SyncResultEnum";
    }
}
