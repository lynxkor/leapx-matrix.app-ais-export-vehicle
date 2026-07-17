package com.leapmotor.export.vehicle.so.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * CBU 整车销售订单同步结果枚举（对应 APS / SAP / 客户系统同步结果字段）
 * <p>0 失败、1 成功
 */
public enum VehicleCbuSoSyncResultEnum implements IBaseEnum<Integer> {

    FAILED(0, "失败"),
    SUCCESS(1, "成功");

    private final Integer code;
    private final String message;

    VehicleCbuSoSyncResultEnum(Integer code, String message) {
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

    public static VehicleCbuSoSyncResultEnum of(Integer code) {
        for (VehicleCbuSoSyncResultEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}