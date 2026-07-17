package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.paltform.ais.common.event.core.EventPublisher;
import com.leapmotor.paltform.ais.common.event.core.NotifyEventSourceAdapter;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.base.ThirdTargetPlatform;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 事件发布封装：业务侧调 publishAsync 发事件 → ais-event 落 event_notify_task → XXL-Job 轮询消费。
 */
@Slf4j
@Component
@AllArgsConstructor
public class EventHandlerHelper {

    private final List<AbstractEventHandler> handlers;
    private final EventPublisher eventPublisher;

    public AbstractEventHandler getHandler(ThirdTargetPlatform platform, BizEventType eventType) {
        return handlers.stream()
                .filter(handler -> handler.getSupportEvent().equals(eventType)
                        && handler.getSupportPlatform().equals(platform))
                .findFirst()
                .orElse(null);
    }

    /**
     * 同步发布事件
     */
    public void publishSync(ThirdTargetPlatform platform, BizEventType eventType, String bizNo) {
        NotifyEventSourceAdapter event = new NotifyEventSourceAdapter();
        event.setTargetPlatforms(Set.of(platform));
        event.setBizEventType(eventType);
        event.setBizNo(bizNo);
        AbstractEventHandler handler = getHandler(platform, eventType);
        if (handler != null) {
            EventNotifyTask task = new EventNotifyTask();
            task.setBizNo(bizNo);
            event.setDataParam(getDataParam(handler.buildRequest(task)));
        }
        eventPublisher.publish(event, false, false, false);
    }

    /**
     * 同步发布事件（带预构建 data）
     */
    public void publishSync(ThirdTargetPlatform platform, BizEventType eventType, String bizNo, Object data) {
        NotifyEventSourceAdapter event = new NotifyEventSourceAdapter();
        event.setTargetPlatforms(Set.of(platform));
        event.setBizEventType(eventType);
        event.setBizNo(bizNo);
        if (data != null) {
            event.setDataParam(getDataParam(data));
        }
        eventPublisher.publish(event, false, false, false);
    }

    /**
     * 异步发布事件（落表后由 Job 轮询消费，推荐）
     */
    public void publishAsync(ThirdTargetPlatform platform, BizEventType eventType, String bizNo, String relatedNo) {
        NotifyEventSourceAdapter event = new NotifyEventSourceAdapter();
        event.setTargetPlatforms(Set.of(platform));
        event.setBizEventType(eventType);
        event.setBizNo(bizNo);
        event.setRelatedNo(relatedNo);
        AbstractEventHandler handler = getHandler(platform, eventType);
        if (handler != null) {
            EventNotifyTask task = new EventNotifyTask();
            task.setBizNo(bizNo);
            task.setRelatedNo(relatedNo);
            event.setDataParam(getDataParam(handler.buildRequest(task)));
        }
        eventPublisher.publish(event, true, false, true);
    }

    public void publishAsync(ThirdTargetPlatform platform, BizEventType eventType, String bizNo) {
        publishAsync(platform, eventType, bizNo, bizNo);
    }

    /**
     * 异步发布事件（带预构建 data，落表后立即异步执行）
     */
    public void publishAsync(ThirdTargetPlatform platform, BizEventType eventType, String bizNo, Object data) {
        NotifyEventSourceAdapter event = new NotifyEventSourceAdapter();
        event.setTargetPlatforms(Set.of(platform));
        event.setBizEventType(eventType);
        event.setBizNo(bizNo);
        event.setRelatedNo(bizNo);
        if (data != null) {
            event.setDataParam(getDataParam(data));
        }
        eventPublisher.publish(event, true, true, true);
        log.info("异步发布事件成功，event:{}", event);
    }

    public void publish(ThirdTargetPlatform platform, BizEventType eventType, String bizNo,
                        boolean publishAsync, boolean executeImmediately, boolean executeAsync) {
        NotifyEventSourceAdapter event = new NotifyEventSourceAdapter();
        event.setTargetPlatforms(Set.of(platform));
        event.setBizEventType(eventType);
        event.setBizNo(bizNo);
        AbstractEventHandler handler = getHandler(platform, eventType);
        if (handler != null) {
            EventNotifyTask task = new EventNotifyTask();
            task.setBizNo(bizNo);
            event.setDataParam(getDataParam(handler.buildRequest(task)));
        }
        eventPublisher.publish(event, publishAsync, executeImmediately, executeAsync);
    }

    private String getDataParam(Object data) {
        if (data == null) {
            return null;
        }
        String json = JSON.toJSONString(data);
        if (json.length() > 65535) {
            return json.substring(0, 65535);
        }
        return json;
    }
}
