package com.leapmotor.export.vehicle.letterGuarantee.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 保函类型(对应字段 lg_type,字典 letter_guarantee_lg_type)
 *
 * <p>G117 红旗项目目前仅启用 预付款保函;下拉框单选,VARCHAR(10) 存储。
 * 其它类型(履约/质量/投标)预留扩展位,后续业务上线时按需补登记字典项与枚举常量。
 *
 * <p>必填 NOT NULL,新增时无默认值,调用方必须显式赋值。
 */
public enum LgTypeEnum implements IBaseEnum<String> {
    PREPAYMENT ("PREPAYMENT", "付款保函");

    private final String code;
    private final String message;

    LgTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode()    { return code; }
    @Override public String getMessage() { return message; }

    public static LgTypeEnum of(String code) {
        for (LgTypeEnum e : values()) if (e.code.equals(code)) return e;
        return null;
    }
}
