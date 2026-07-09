package com.leapmotor.export.vehicle.po.enums;

/**
 * 订单类型枚举（order_type，tinyint）。
 * PO 内部存 Integer(1/2/3)，调保函等外部（要 String "CBU"）时用 getName 转换。
 */
public enum OrderTypeEnum {

    CBU(1, "CBU"),
    SKD(2, "SKD"),
    CKD(3, "CKD");

    private final Integer code;
    private final String name;

    OrderTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /** Integer code → String name（调保函等要 String 的外部用）；未知 code 回退数字字符串 */
    public static String getName(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e.name;
            }
        }
        return String.valueOf(code);
    }
}
