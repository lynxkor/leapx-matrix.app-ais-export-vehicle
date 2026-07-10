package com.leapmotor.export.vehicle.base.event.enums;

import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;

import java.util.Arrays;

/**
 * 红旗事件类型
 */
public enum HongqiEventTypeEnum implements BizEventType {

    /** 推送CBU审批结果到红旗 */
    PUSH_CBU_APPROVAL_RESULT("PUSH_CBU_APPROVAL_RESULT", "推送CBU审批结果到红旗"),
    ;

    private final String type;
    private final String desc;

    HongqiEventTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static HongqiEventTypeEnum matchByType(String type) {
        return Arrays.stream(values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
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
