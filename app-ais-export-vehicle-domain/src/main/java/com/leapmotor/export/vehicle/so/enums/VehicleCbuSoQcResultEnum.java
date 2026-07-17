package com.leapmotor.export.vehicle.so.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

import java.util.Arrays;

/**
 * CBU 整车销售订单质检结果枚举（对应字段 qc_result / last_qc_result）
 *
 * <p>10 通过、20 不通过、30 未收到车辆。
 */
public enum VehicleCbuSoQcResultEnum implements IBaseEnum<Integer> {

    PASSED(10, "通过"),
    FAILED(20, "不通过"),
    NO_VEHICLE(30, "未收到车辆");

    private final Integer code;
    private final String message;

    VehicleCbuSoQcResultEnum(Integer code, String message) {
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

    public static VehicleCbuSoQcResultEnum of(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
