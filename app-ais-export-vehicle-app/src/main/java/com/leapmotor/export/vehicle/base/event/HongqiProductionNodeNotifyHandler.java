package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiProductionNodeResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
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
import java.util.List;

/**
 * 红旗车辆生产信息同步事件处理器（VDO53003）：
 * 反序列化 dataParam -> 调 HongqiGateway 批量推送 -> 按红旗返回逐条写客户系统同步记录（类型3）
 * -> success=false 或 data 中任一 isSign!=Y 抛异常触发重试
 * （VDO53003 契约：data=List，每条入参对应一条出参，isSign=Y/N）。
 */
@AllArgsConstructor
@Slf4j
@Component
public class HongqiProductionNodeNotifyHandler extends AbstractEventHandler {

    private HongqiGateway hongqiGateway;
    private VehicleCbuSoGateway vehicleCbuSoGateway;
    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

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
        HongqiResult<List<HongqiProductionNodeResultData>> result = hongqiGateway.pushProductionNodeSync(reqList);
        log.info("红旗车辆生产信息同步结果: {}", result);
        List<HongqiProductionNodeResultData> dataList = result == null ? null : result.getData();
        // 按红旗返回逐条写客户系统同步记录（类型3，成功/失败按 isSign）
        if (dataList != null) {
            for (HongqiProductionNodeResultData data : dataList) {
                writeInterfaceSyncRecord(data);
            }
        }
        boolean ok = result != null && result.isSuccess()
                && dataList != null && !dataList.isEmpty()
                && dataList.stream().allMatch(d -> "Y".equals(d.getIsSign()));
        if (!ok) {
            throw new BizException("红旗车辆生产信息同步失败: " + JSON.toJSONString(result));
        }
    }

    /**
     * 写客户系统同步记录（类型3：生产状态同步客户/红旗），按 isSign 记成功/失败。
     *
     * @param data 红旗返回的单条出参（含 soNo/isSign/errMsg）
     */
    private void writeInterfaceSyncRecord(HongqiProductionNodeResultData data) {
        try {
            VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoBySoNo(data.getSoNo());
            VehicleCbuSoInterfaceSyncDo syncDo = new VehicleCbuSoInterfaceSyncDo();
            syncDo.setSoId(so != null ? so.getId() : null);
            syncDo.setInterfaceType(3);
            syncDo.setInterfaceDesc("车辆生产信息同步红旗");
            syncDo.setCallTime(LocalDateTime.now());
            syncDo.setRequestInfo(StringUtils.left(data.getSoNo(), 2000));
            syncDo.setResultStatus("Y".equals(data.getIsSign()) ? 1 : 0);
            syncDo.setResponseInfo(StringUtils.left(data.getErrMsg(), 2000));
            syncDo.setRemark("生产节点同步");
            syncDo.setStatus(1);
            vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(syncDo);
        } catch (Exception e) {
            log.error("写红旗生产信息同步记录失败, soNo={}", data.getSoNo(), e);
        }
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        return null;
    }
}
