package com.leapmotor.export.vehicle.letterGuarantee.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 订单类型(对应字段 order_type,字典 letter_guarantee_order_type)
 *
 * <p>下拉框单选,VARCHAR(10) 存储,可空(NULL = 通用,匹配所有订单类型)。
 * 仅在 {@link ApprovalStatusEnum#DRAFT 草稿} / {@link ApprovalStatusEnum#REJECTED 驳回}
 * 状态下允许编辑;提交后订单类型由订单系统决定,保函侧锁定。
 *
 * <p>G117 红旗项目默认新建草稿时取值 {@link #CBU}。
 */
public enum OrderTypeEnum implements IBaseEnum<String> {
    CBU ("CBU", "CBU");

    private final String code;
    private final String message;

    OrderTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode()    { return code; }
    @Override public String getMessage() { return message; }

    public static OrderTypeEnum of(String code) {
        for (OrderTypeEnum e : values()) if (e.code.equals(code)) return e;
        return null;
    }
}
