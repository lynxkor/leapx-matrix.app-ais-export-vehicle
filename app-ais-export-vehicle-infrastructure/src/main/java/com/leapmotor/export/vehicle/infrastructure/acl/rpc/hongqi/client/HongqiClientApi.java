package com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.client;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiProductionNodeResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 红旗系统 Feign 客户端，经统一出口网关 app-ais-xuanniao 调用，前缀 /external/hongqi。服务名/契约待红旗提供，先占位。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@Component
@FeignClient(name = "app-ais-xuanniao", contextId = "HongqiClientApi", path = "/external/hongqi")
public interface HongqiClientApi {

    /**
     * 推送 CBU 审批结果到红旗
     */
    @PostMapping("/JT/MA/MA-0203/VDO/IVDO/receiveRevApproval")
    HongqiResult<HongqiResultData> pushCbuApprovalResult(@RequestBody CbuApprovalResultReq request);

    /**
     * 车辆生产信息同步红旗（VDO53003，契约待红旗提供，先占位）
     */
    @PostMapping("/JT/MA/MA-0203/001/IVDO/receiveM1Data")
    HongqiResult<List<HongqiProductionNodeResultData>> pushProductionNodeSync(
            @RequestBody List<HongqiProductionNodeNotifyReq> request);


    /**
     * 应付清单同步红旗（契约待红旗提供，先占位）
     */
    @PostMapping("/payable/list/sync")
    HongqiResult syncPayableList(@RequestBody PayableListSyncHqRecordReq request);
}
