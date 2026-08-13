package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.SapResult;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.so.enums.InterfaceSyncTypeEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * SO 订单同步 SAP 事件处理器：
 * 反序列化 dataParam -> 调 SapGateway -> 写类型4 接口同步记录（自动刷新明细表 SAP 三字段）
 * -> ES_RET.RET_CODE != S 抛异常触发重试。
 *
 * <p>不加 @Transactional：writeInterfaceSyncRecord 依赖 addVehicleCbuSoInterfaceSync 自带事务落库，
 * SAP 失败抛异常时不回滚已写入的失败记录（对齐 HongqiProductionNodeNotifyHandler）。
 */
@AllArgsConstructor
@Slf4j
@Component
public class SapSyncNotifyHandler extends AbstractEventHandler {

    private SapGateway sapGateway;
    private VehicleCbuSoGateway vehicleCbuSoGateway;
    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

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
        writeInterfaceSyncRecord(task.getBizNo(), result);
        if (!isSapSuccess(result)) {
            throw new BizException("SO订单同步SAP失败: " + JSON.toJSONString(result));
        }
    }

    /**
     * 写订单同步SAP记录（类型4），写入时自动刷新明细表 sync_sap_result/time/response_info 三字段。
     * 写记录失败只 log，不影响 SAP 调用结果（参照 HongqiProductionNodeNotifyHandler）。
     *
     * @param soNo  SO 单号（事件 bizNo）
     * @param result SAP 返回
     */
    private void writeInterfaceSyncRecord(String soNo, SapResult result) {
        try {
            VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoBySoNo(soNo);
            VehicleCbuSoInterfaceSyncDo syncDo = new VehicleCbuSoInterfaceSyncDo();
            syncDo.setSoId(so != null ? so.getId() : null);
            syncDo.setInterfaceType(InterfaceSyncTypeEnum.SAP_ORDER_SYNC.getCode());
            syncDo.setInterfaceDesc("订单同步SAP");
            syncDo.setCallTime(LocalDateTime.now());
            syncDo.setRequestInfo(StringUtils.left(soNo, 2000));
            syncDo.setResultStatus(isSapSuccess(result) ? 1 : 0);
            syncDo.setResponseInfo(StringUtils.left(extractSapReason(result), 2000));
            syncDo.setRemark("质检通过同步SAP");
            syncDo.setStatus(1);
            vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(syncDo);
        } catch (Exception e) {
            log.error("写SAP同步记录失败, soNo={}", soNo, e);
        }
    }

    private boolean isSapSuccess(SapResult result) {
        if (result == null || !result.isSuccess()) {
            return false;
        }
        if (result.getBody() == null || result.getBody().getEsRet() == null) {
            return false;
        }
        return "S".equals(result.getBody().getEsRet().getRetCode());
    }

    private String extractSapReason(SapResult result) {
        if (result == null) {
            return null;
        }
        if (result.getBody() != null && result.getBody().getEsRet() != null
                && StringUtils.isNotBlank(result.getBody().getEsRet().getRetReason())) {
            return result.getBody().getEsRet().getRetReason();
        }
        return result.getMessage();
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        return null;
    }
}
