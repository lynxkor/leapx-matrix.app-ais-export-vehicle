package com.leapmotor.export.vehicle.acl.http.aps.gateway;

import com.leapmotor.export.vehicle.acl.http.aps.req.ApsCancelReq;
import com.leapmotor.export.vehicle.acl.http.aps.req.ApsIssueReq;
import com.leapmotor.export.vehicle.acl.http.aps.resp.ApsResult;

/**
 * APS 产销平台领域网关（domain 定义接口，infrastructure 提供 Feign 实现）。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface ApsGateway {

    /**
     * SO 下发排产同步 APS（未下发生产 -> 已下发生产）。
     *
     * @param req 下发请求
     * @return APS 返回
     */
    ApsResult issueSync(ApsIssueReq req);

    /**
     * SO 作废同步 APS（poNo 与 soNo 至少传一个，changeType = -1）。
     *
     * @param req 作废请求
     * @return APS 返回
     */
    ApsResult cancelSync(ApsCancelReq req);
}
