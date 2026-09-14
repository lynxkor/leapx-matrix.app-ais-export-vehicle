package com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.gatewayimpl;

import com.alibaba.fastjson.JSON;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiTokenReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiProductionNodeResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiEpReceiveResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiTokenResult;
import com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.client.HongqiClientApi;
import com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.client.HongqiEpClientApi;
import com.leapmotor.export.vehicle.infrastructure.config.HongqiEpProperties;
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

    private HongqiEpClientApi hongqiEpClientApi;

    private HongqiEpProperties hongqiEpProperties;

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

    @Override
    public HongqiResult syncPayableListEp(PayableListSyncHqRecordReq req) {
        try {
            // 1. 实时获取 token（每次现取，不缓存；token 报文与 secretkey 不打日志）
            HongqiTokenResult tokenResult = hongqiEpClientApi.getToken(
                    hongqiEpProperties.getTenantId(),
                    hongqiEpProperties.getUserAgent(),
                    buildTokenReq());
            if (tokenResult == null || !"S".equals(tokenResult.getStatus())
                    || tokenResult.getCode() == null || tokenResult.getCode() != 200) {
                log.error("应付清单同步红旗EP-获取token失败: code={}, status={}",
                        tokenResult == null ? null : tokenResult.getCode(),
                        tokenResult == null ? null : tokenResult.getStatus());
                throw new BizException("红旗EP获取token失败，请检查EP鉴权配置");
            }

            // 2. 带 Jwt 调应付清单接收接口
            HongqiEpReceiveResult ep = hongqiEpClientApi.receivePayableList(
                    tokenResult.getData().getToken(),
                    hongqiEpProperties.getUserAgent(),
                    hongqiEpProperties.getTenantId(),
                    hongqiEpProperties.getCompanyId(),
                    hongqiEpProperties.getAppkey(),
                    req);
            log.info("应付清单同步红旗EP-返回: {}", JSON.toJSONString(ep));

            // 3. 响应转化：{code, msg, data} → 统一壳 {code, message, data, success}
            HongqiResult result = new HongqiResult();
            result.setCode(ep.getCode());
            result.setMessage(ep.getMsg());
            result.setData(ep.getData());
            result.setSuccess(ep.getCode() != null && ep.getCode() == 200);
            return result;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("应付清单同步红旗EP异常：{}", e.getMessage(), e);
            throw new BizException("应付清单同步红旗EP异常：" + e.getMessage());
        }
    }

    @Override
    public HongqiTokenResult getEpToken() {
        try {
            HongqiTokenResult tokenResult = hongqiEpClientApi.getToken(
                    hongqiEpProperties.getTenantId(),
                    hongqiEpProperties.getUserAgent(),
                    buildTokenReq());
            log.info("获取红旗EP token: status={}, code={}",
                    tokenResult == null ? null : tokenResult.getStatus(),
                    tokenResult == null ? null : tokenResult.getCode());
            return tokenResult;
        } catch (Exception e) {
            log.error("获取红旗EP token 异常：{}", e.getMessage(), e);
            throw new BizException("获取红旗EP token 异常：" + e.getMessage());
        }
    }

    /**
     * 构建红旗 EP 获取 token 请求
     */
    private HongqiTokenReq buildTokenReq() {
        HongqiTokenReq req = new HongqiTokenReq();
        req.setAppkey(hongqiEpProperties.getAppkey());
        req.setSecretkey(hongqiEpProperties.getSecretkey());
        req.setLoginTimestamp(System.currentTimeMillis());
        return req;
    }
}
