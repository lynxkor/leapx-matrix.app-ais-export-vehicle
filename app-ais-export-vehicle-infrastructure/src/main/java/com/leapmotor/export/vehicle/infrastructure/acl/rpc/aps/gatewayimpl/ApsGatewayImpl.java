package com.leapmotor.export.vehicle.infrastructure.acl.rpc.aps.gatewayimpl;

import com.leapmotor.export.vehicle.acl.rpc.aps.gateway.ApsGateway;
import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsCancelReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsIssueReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.resp.ApsResult;
import com.leapmotor.export.vehicle.infrastructure.acl.rpc.aps.client.ApsClientApi;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * APS 领域网关实现：调用 Feign 客户端并处理响应，失败抛业务异常。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
@Slf4j
public class ApsGatewayImpl implements ApsGateway {

    private ApsClientApi apsClientApi;

    @Override
    public ApsResult issueSync(ApsIssueReq req) {
        try {
            log.info("SO下发排产同步APS, request:{}", req);
            ApsResult result = apsClientApi.soIssueSync(req);
            log.info("SO下发排产同步APS, request:{}, response:{}", req, result);
            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("SO下发排产同步APS异常：{}", e.getMessage());
            throw new BizException("SO下发排产同步APS异常：" + e.getMessage());
        }
    }

    @Override
    public ApsResult cancelSync(ApsCancelReq req) {
        try {
            log.info("SO作废同步APS, request:{}", req);
            ApsResult result = apsClientApi.soCancelSync(req);
            log.info("SO作废同步APS, request:{}, response:{}", req, result);
            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("SO作废同步APS异常：{}", e.getMessage());
            throw new BizException("SO作废同步APS异常：" + e.getMessage());
        }
    }
}
