package com.leapmotor.export.vehicle.po.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * PO 订单类型枚举（order_type，tinyint）。
 * PO 内部存 Integer(1/2/3)，调保函等外部（要 String "CBU"）时用 getName 转换。
 */
public enum PoOrderTypeEnum implements IBaseEnum<Integer> {

    CBU(1, "CBU"),
    SKD(2, "SKD"),
    CKD(3, "CKD");

    private final Integer code;
    private final String name;

    PoOrderTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return name;
    }

    public String getName() {
        return name;
    }

    /** Integer code → String name（调保函等要 String 的外部用）；未知 code 回退数字字符串 */
    public static String getName(Integer code) {
        if (code == null) {
            return null;
        }
        for (PoOrderTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e.name;
            }
        }
        return String.valueOf(code);
    }
}
