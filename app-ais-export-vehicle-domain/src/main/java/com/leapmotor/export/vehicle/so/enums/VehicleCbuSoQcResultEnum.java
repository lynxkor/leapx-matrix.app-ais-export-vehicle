package com.leapmotor.export.vehicle.so.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

import java.util.Arrays;

/**
 * CBU 整车销售订单质检结果枚举（对应字段 qc_result / last_qc_result）
 *
 * <p>外部字符映射：P → 10、N → 20、F → 30。
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

    /**
     * 根据外部字符解析质检结果。
     *
     * @param ch P/N/F（大小写不敏感）
     * @return 对应枚举，无法识别返回 null
     */
    public static VehicleCbuSoQcResultEnum fromExternalChar(String ch) {
        if (ch == null) {
            return null;
        }
        return switch (ch.trim().toUpperCase()) {
            case "P" -> PASSED;
            case "N" -> FAILED;
            case "F" -> NO_VEHICLE;
            default -> null;
        };
    }
}
