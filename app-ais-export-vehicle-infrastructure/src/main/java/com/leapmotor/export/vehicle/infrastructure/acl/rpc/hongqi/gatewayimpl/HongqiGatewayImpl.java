package com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.gatewayimpl;

import com.alibaba.fastjson.JSON;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiProductionNodeResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResultData;
import com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.client.HongqiClientApi;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 红旗领域网关实现：调用 Feign 客户端并处理响应，失败抛业务异常。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@AllArgsConstructor
@Service
@Slf4j
public class HongqiGatewayImpl implements HongqiGateway {

    private HongqiClientApi hongqiClientApi;

    @Override
    public HongqiResult<HongqiResultData> pushCbuApprovalResult(CbuApprovalResultReq req) {
        try {
            log.info("红旗CBU审批结果推送-请求: {}", JSON.toJSONString(req));
            HongqiResult<HongqiResultData> result = hongqiClientApi.pushCbuApprovalResult(req);
            log.info("红旗CBU审批结果推送-返回: {}", JSON.toJSONString(result));
            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("红旗CBU审批结果推送异常：{}", e.getMessage());
            throw new BizException("红旗CBU审批结果推送异常：" + e.getMessage());
        }
    }

    @Override
    public HongqiResult<List<HongqiProductionNodeResultData>> pushProductionNodeSync(
            List<HongqiProductionNodeNotifyReq> req) {
        try {
            log.info("红旗车辆生产信息同步-请求: {}", JSON.toJSONString(req));
            HongqiResult<List<HongqiProductionNodeResultData>> result = hongqiClientApi.pushProductionNodeSync(req);
            log.info("红旗车辆生产信息同步-返回: {}", JSON.toJSONString(result));
            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("红旗车辆生产信息同步异常：{}", e.getMessage());
            throw new BizException("红旗车辆生产信息同步异常：" + e.getMessage());
        }
    }
}
