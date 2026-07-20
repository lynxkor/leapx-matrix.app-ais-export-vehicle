package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 红旗车辆生产信息同步事件处理器（VDO53003）：
 * 反序列化 dataParam → 调 HongqiGateway → success=false 或 data.receiveflag!=Y 抛异常触发重试（响应壳假设同 VDO53002，待 VDO53003 契约确认）。
 */
@AllArgsConstructor
@Slf4j
@Component
public class HongqiProductionNodeNotifyHandler extends AbstractEventHandler {

    private HongqiGateway hongqiGateway;

    @Override
    protected BizEventType getSupportEvent() {
        return HongqiEventTypeEnum.PRODUCTION_NODE_SYNC;
    }

    @Override
    protected ThirdTargetPlatformEnum getSupportPlatform() {
        return ThirdTargetPlatformEnum.HONGQI;
    }

    @Override
    protected void handle(EventNotifyTask task) {
        log.info("红旗车辆生产信息同步-开始, bizNo={}", task.getBizNo());
        List<HongqiProductionNodeNotifyReq> reqList = JSON.parseObject(task.getDataParam(),
                new TypeReference<List<HongqiProductionNodeNotifyReq>>() {});
        HongqiResult result = hongqiGateway.pushProductionNodeSync(reqList);
        log.info("红旗车辆生产信息同步结果: {}", result);
        if (result == null || !result.isSuccess()
                || result.getData() == null || !"Y".equals(result.getData().getReceiveFlag())) {
            throw new BizException("红旗车辆生产信息同步失败: " + JSON.toJSONString(result));
        }
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        return null;
    }
}
