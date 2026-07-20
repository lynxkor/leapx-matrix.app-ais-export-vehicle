package com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;

import java.util.List;

/**
 * 红旗系统领域网关（domain 定义接口，infrastructure 提供 Feign 实现）。
 *
 * <p>用于 CBU 审批结果回传红旗、车辆生产信息同步红旗。服务名/契约待红旗提供，先占位。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
public interface HongqiGateway {

    /**
     * 推送 CBU 审批结果到红旗。
     *
     * @param req 审批结果回传请求
     * @return 红旗返回
     */
    HongqiResult pushCbuApprovalResult(CbuApprovalResultReq req);

    /**
     * 车辆生产信息同步红旗（VDO53003）。
     *
     * @param req 生产信息同步请求列表
     * @return 红旗返回
     */
    HongqiResult pushProductionNodeSync(List<HongqiProductionNodeNotifyReq> req);
}
