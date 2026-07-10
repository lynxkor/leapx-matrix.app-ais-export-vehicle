package com.leapmotor.export.vehicle.base.event.enums;

import com.leapmotor.paltform.ais.common.event.core.base.ThirdTargetPlatform;

/**
 * 红旗事件目标平台
 */
public enum HongqiThirdTargetPlatformEnum implements ThirdTargetPlatform {

    HONGQI("HONGQI", "红旗系统");

    private final String platform;
    private final String desc;

    HongqiThirdTargetPlatformEnum(String platform, String desc) {
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
