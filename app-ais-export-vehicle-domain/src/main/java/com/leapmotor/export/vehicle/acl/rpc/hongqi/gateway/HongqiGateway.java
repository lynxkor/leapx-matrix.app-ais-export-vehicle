package com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiProductionNodeResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiTokenResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;

import java.util.List;

/**
 * 红旗系统领域网关（domain 定义接口，infrastructure 提供 Feign 实现）。
 *
 * <p>用于 CBU 审批结果回传红旗、车辆生产信息同步红旗、应付清单同步红旗。服务名/契约待红旗提供，先占位。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
public interface HongqiGateway {

    /**
     * 推送 CBU 审批结果到红旗。
     *
     * @param req 审批结果回传请求
     * @return 红旗返回（data = HongqiResultData）
     */
    HongqiResult<HongqiResultData> pushCbuApprovalResult(CbuApprovalResultReq req);

    /**
     * 车辆生产信息同步红旗（VDO53003）。
     *
     * @param req 生产信息同步请求列表
     * @return 红旗返回（data = List&lt;HongqiProductionNodeResultData&gt;，每条入参对应一条出参）
     */
    HongqiResult<List<HongqiProductionNodeResultData>> pushProductionNodeSync(List<HongqiProductionNodeNotifyReq> req);

    /**
     * 应付清单同步红旗 EP（走出口网关 /external/hongqi-ep，调用前实时获取 token）。
     *
     * @param req 应付清单同步请求（records 结构为 EP 文档格式，复用 PayableListSyncHqRecordReq）
     * @return 统一响应壳（code=200 成功）
     */
    HongqiResult syncPayableListEp(PayableListSyncHqRecordReq req);

    /**
     * 获取红旗 EP 访问令牌（供排查问题/补推数据场景使用）
     *
     * @return EP 返回的 token 结果
     */
    HongqiTokenResult getEpToken();
}
