package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.http.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.http.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.http.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CBU 审批结果推送红旗（单事件执行器）：
 * 反序列化 dataParam → 调 HongqiGateway → code!=200 抛异常触发框架重试。
 */
@AllArgsConstructor
@Slf4j
@Component
public class CbuApprovalResultToHongqi extends AbstractEventHandler {

    private HongqiGateway hongqiGateway;

    @Override
    protected BizEventType getSupportEvent() {
        return HongqiEventTypeEnum.PUSH_CBU_APPROVAL_RESULT;
    }

    @Override
    protected ThirdTargetPlatformEnum getSupportPlatform() {
        return ThirdTargetPlatformEnum.HONGQI;
    }

    @Override
    protected void handle(EventNotifyTask task) {
        log.info("红旗CBU审批结果推送-开始, bizNo={}", task.getBizNo());
        CbuApprovalResultReq req = JSON.parseObject(task.getDataParam(), CbuApprovalResultReq.class);
        HongqiResult result = hongqiGateway.pushCbuApprovalResult(req);
        log.info("红旗CBU审批结果推送结果: {}", result);
        if (result == null || result.getCode() != 200) {
            throw new BizException("红旗CBU审批结果推送失败: " + JSON.toJSONString(result));
        }
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        // 走「带预构建 data」发布时不进这里；如需查库组装可在此实现
        return null;
    }
}
