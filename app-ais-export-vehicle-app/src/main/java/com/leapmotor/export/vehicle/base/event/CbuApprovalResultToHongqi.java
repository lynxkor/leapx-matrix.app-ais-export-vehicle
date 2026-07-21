package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CBU 审批结果推送红旗（单事件执行器）：
 * 反序列化 dataParam → 调 HongqiGateway → success=false 或 data.receiveflag!=Y 抛异常触发框架重试。
 * 推送结果(同步时间/成功失败/红旗返回)回写 PO 主表 audit_result_sync_* + customer_system_return_info。
 */
@AllArgsConstructor
@Slf4j
@Component
public class CbuApprovalResultToHongqi extends AbstractEventHandler {

    private HongqiGateway hongqiGateway;
    private ExportVehicleCbuPoGateway purchaseOrderGateway;

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
        String poId = task.getBizNo();
        HongqiResult result;
        try {
            result = hongqiGateway.pushCbuApprovalResult(req);
        } catch (Exception e) {
            // 推送本身异常(网络/超时),先回写失败再抛异常走框架重试
            purchaseOrderGateway.updateApprovalSyncResult(poId, LocalDateTime.now(), "失败", "推送异常:" + e.getMessage());
            throw new BizException("红旗CBU审批结果推送异常: " + e.getMessage());
        }
        log.info("红旗CBU审批结果推送结果: {}", result);
        // 成功需 success=true 且 data.receiveflag=Y（code=200 但 receiveflag=N 表示红旗业务层拒绝）
        boolean ok = result != null && result.isSuccess()
                && result.getData() != null && "Y".equals(result.getData().getReceiveFlag());
        String returnInfo = JSON.toJSONString(result);
        // 回写 PO 同步信息(独立事务,重试不被外层回滚带走;每次推送覆盖,以最后一次为准)
        purchaseOrderGateway.updateApprovalSyncResult(poId, LocalDateTime.now(), ok ? "成功" : "失败", returnInfo);
        if (!ok) {
            throw new BizException("红旗CBU审批结果推送失败: " + returnInfo);
        }
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        // 走「带预构建 data」发布时不进这里；如需查库组装可在此实现
        return null;
    }
}
