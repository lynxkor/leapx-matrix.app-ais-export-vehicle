package com.leapmotor.export.vehicle.so.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * SO 接口同步记录类型枚举（对应字段 interface_type）
 *
 * <p>1 下发排产同步APS、2 作废同步APS、3 客户系统同步红旗（出站）、4 订单同步SAP。
 * <p>仅用于本系统调用三方系统的出站调用；入站反馈禁止写入接口同步记录表。
 */
public enum InterfaceSyncTypeEnum implements IBaseEnum<Integer> {

    APS_ISSUE_SYNC(1, "下发排产同步APS"),
    APS_CANCEL_SYNC(2, "作废同步APS"),
    CUSTOMER_HONGQI_SYNC(3, "客户系统同步红旗"),
    SAP_ORDER_SYNC(4, "订单同步SAP");

    private final Integer code;
    private final String message;

    InterfaceSyncTypeEnum(Integer code, String message) {
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
