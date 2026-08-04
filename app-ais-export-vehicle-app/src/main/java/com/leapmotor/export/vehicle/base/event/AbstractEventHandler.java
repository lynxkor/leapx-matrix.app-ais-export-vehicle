package com.leapmotor.export.vehicle.base.event;

import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.base.ThirdTargetPlatform;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;

/**
 * 事件处理器抽象类（支持多目标平台）
 */
public abstract class AbstractEventHandler {

    /**
     * 支持的事件类型
     */
    protected abstract BizEventType getSupportEvent();

    /**
     * 支持的目标平台
     */
    protected abstract ThirdTargetPlatform getSupportPlatform();

    /**
     * 处理事件（抛异常=失败，触发重试）
     */
    protected abstract void handle(EventNotifyTask task);

    /**
     * 构建请求参数（发布时若不带 data  则走此查库组装）
     */
    public abstract Object buildRequest(EventNotifyTask task);
}
