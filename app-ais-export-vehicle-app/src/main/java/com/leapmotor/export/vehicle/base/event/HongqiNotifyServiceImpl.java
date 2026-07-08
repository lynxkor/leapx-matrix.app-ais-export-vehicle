package com.leapmotor.export.vehicle.base.event;

import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.HongqiThirdTargetPlatformEnum;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.config.EventConfig;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.event.core.support.AbstractThirdNotifyService;
import com.leapmotor.paltform.ais.common.event.service.EventNotifyTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 红旗平台通知服务（平台路由）：框架按 platform()+事件类型路由到本类，
 * doExecuteNotify 再分发给对应的事件处理器（CbuApprovalResultToHongqi）。
 */
@Slf4j
@Service
public class HongqiNotifyServiceImpl extends AbstractThirdNotifyService {

    private final Map<BizEventType, AbstractEventHandler> handlerMap;

    public HongqiNotifyServiceImpl(EventConfig eventConfig,
                                   EventNotifyTaskService eventNotifyTaskService,
                                   List<AbstractEventHandler> handlers) {
        super(List.of(HongqiEventTypeEnum.values()), eventConfig, eventNotifyTaskService);
        handlerMap = new HashMap<>();
        for (AbstractEventHandler handler : handlers) {
            if (Strings.CS.equals(HongqiThirdTargetPlatformEnum.HONGQI.getPlatform(),
                    handler.getSupportPlatform().getPlatform())) {
                handlerMap.put(handler.getSupportEvent(), handler);
            }
        }
    }

    @Override
    protected void doExecuteNotify(EventNotifyTask task) {
        HongqiEventTypeEnum eventTypeEnum = HongqiEventTypeEnum.matchByType(task.getType());
        AbstractEventHandler handler = handlerMap.get(eventTypeEnum);
        if (handler == null) {
            log.error("红旗事件未找到对应的处理类:{}", eventTypeEnum);
            return;
        }
        handler.handle(task);
    }

    @Override
    public String platform() {
        return HongqiThirdTargetPlatformEnum.HONGQI.getPlatform();
    }
}
