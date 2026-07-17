package com.leapmotor.export.vehicle.base.event.enums;

import com.leapmotor.paltform.ais.common.event.core.base.ThirdTargetPlatform;

/**
 * 第三方目标平台枚举（红旗 / SAP）
 */
public enum ThirdTargetPlatformEnum implements ThirdTargetPlatform {

    HONGQI("HONGQI", "红旗系统"),
    SAP("SAP", "SAP系统");

    private final String platform;
    private final String desc;

    ThirdTargetPlatformEnum(String platform, String desc) {
        this.platform = platform;
        this.desc = desc;
    }

    @Override
    public String getPlatform() {
        return platform;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
