package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.SapResult;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SO 订单同步 SAP 事件处理器：
 * 反序列化 dataParam → 调 SapGateway → code!=200 抛异常触发重试。
 */
@AllArgsConstructor
@Slf4j
@Component
public class SapSyncNotifyHandler extends AbstractEventHandler {

    private SapGateway sapGateway;

    @Override
    protected BizEventType getSupportEvent() {
        return SapEventTypeEnum.SYNC_SAP;
    }

    @Override
    protected ThirdTargetPlatformEnum getSupportPlatform() {
        return ThirdTargetPlatformEnum.SAP;
    }

    @Override
    protected void handle(EventNotifyTask task) {
        log.info("SO订单同步SAP-开始, bizNo={}", task.getBizNo());
        SapOrderSyncReq req = JSON.parseObject(task.getDataParam(), SapOrderSyncReq.class);
        SapResult result = sapGateway.syncSap(req);
        log.info("SO订单同步SAP结果: {}", result);
        if (result == null || result.getCode() != 200) {
            throw new BizException("SO订单同步SAP失败: " + JSON.toJSONString(result));
        }
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        return null;
    }
}
