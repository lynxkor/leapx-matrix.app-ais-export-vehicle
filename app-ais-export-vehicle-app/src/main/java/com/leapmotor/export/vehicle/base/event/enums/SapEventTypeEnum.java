package com.leapmotor.export.vehicle.base.event.enums;

import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;

import java.util.Arrays;

/**
 * SAP 事件类型
 */
public enum SapEventTypeEnum implements BizEventType {

    /**
     * SO 订单同步 SAP
     */
    SYNC_SAP("SYNC_SAP", "SO订单同步SAP"),

    /**
     * 应付清单提交至SAP
     */
    PAYABLE_LIST_SYNC_SAP_SUBMIT("PAYABLE_LIST_SYNC_SAP_SUBMIT", "应付清单提交至SAP"),

    /**
     * SAP清账结果回执
     */
    PAYABLE_LIST_SAP_CLEAR_ACCOUNT("PAYABLE_LIST_SAP_CLEAR_ACCOUNT", "SAP清账结果回执");

    private final String type;
    private final String desc;

    SapEventTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static SapEventTypeEnum matchByType(String type) {
        return Arrays.stream(values())
                .filter(e -> e.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
