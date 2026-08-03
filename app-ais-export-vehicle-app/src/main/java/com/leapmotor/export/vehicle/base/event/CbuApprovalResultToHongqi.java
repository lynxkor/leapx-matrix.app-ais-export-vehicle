package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResultData;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CBU 审批结果推送红旗（单事件执行器）：
 * 核心推送+回写逻辑在 {@link #pushAndWriteBack(String, CbuApprovalResultReq)}，
 * 事件机制 {@link #handle} 和直接调用方共用同一入口，避免代码重复。
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

    /**
     * 推送 CBU 审批结果到红旗并回写 PO 同步结果。
     * 推送成功/失败/异常均不回滚主流程，仅回写 PO + 返回 ok 标识。
     *
     * @param poId PO 订单 ID
     * @param req  推送请求体
     * @return true 推送成功（receiveflag=Y），false 推送失败或异常
     */
    public boolean pushAndWriteBack(String poId, CbuApprovalResultReq req) {
        HongqiResult<HongqiResultData> result;
        try {
            result = hongqiGateway.pushCbuApprovalResult(req);
        } catch (Exception e) {
            log.error("红旗CBU审批结果推送异常, poId: {}", poId, e);
            purchaseOrderGateway.updateApprovalSyncResult(poId, LocalDateTime.now(), "失败", StringUtils.left("推送异常:" + e.getMessage(), 2000));
            return false;
        }
        boolean ok = result != null && result.isSuccess()
                && result.getData() != null && "Y".equals(result.getData().getReceiveFlag());
        // 红旗整包响应 JSON 可能较长，按列长 VARCHAR(2000) 截断兜底，避免 strict sql_mode 下 Data too long（与 SO 侧接口同步信息口径一致）
        String returnInfo = StringUtils.left(JSON.toJSONString(result), 2000);
        purchaseOrderGateway.updateApprovalSyncResult(poId, LocalDateTime.now(), ok ? "成功" : "失败", returnInfo);
        if (!ok) {
            log.error("红旗CBU审批结果推送失败, poId: {}, returnInfo: {}", poId, returnInfo);
        }
        return ok;
    }

    @Override
    protected void handle(EventNotifyTask task) {
        log.info("红旗CBU审批结果推送-开始, bizNo={}", task.getBizNo());
        CbuApprovalResultReq req = JSON.parseObject(task.getDataParam(), CbuApprovalResultReq.class);
        boolean ok = pushAndWriteBack(task.getBizNo(), req);
        if (!ok) {
            throw new BizException("红旗CBU审批结果推送失败, bizNo=" + task.getBizNo());
        }
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        // 走「带预构建 data」发布时不进这里；如需查库组装可在此实现
        return null;
    }
}
