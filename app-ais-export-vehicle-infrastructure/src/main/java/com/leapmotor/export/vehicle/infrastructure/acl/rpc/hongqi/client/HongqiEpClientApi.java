package com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.client;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiTokenReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiEpReceiveResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiTokenResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 红旗 EP 系统 Feign 客户端，经统一出口网关 app-ais-xuanniao 转发，
 * 前缀 /external/hongqi-ep（网关 StripPrefix=2 后转发红旗 EP，EP 地址由网关联配）。
 *
 * <p>调用约定：先获取 token（Jwt），再带 Jwt 头调用业务接口。
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Component
@FeignClient(name = "app-ais-xuanniao", contextId = "HongqiEpClientApi", path = "/external/hongqi-ep")
public interface HongqiEpClientApi {

    /**
     * 获取 Token（EP 接口 validateAuthByAppKey）
     *
     * @param tenantId 固定请求头 tenantId（Nacos 配置）
     * @param userAgent 固定请求头 useragent（Nacos 配置）
     * @param req token 请求（appkey/secretkey/loginTimestamp）
     * @return token 响应（status=S 且 code=200 成功，data.token 为访问令牌）
     */
    @PostMapping("/api/sys/integrationAPI/validateAuthByAppKey")
    HongqiTokenResult getToken(@RequestHeader("tenantId") String tenantId,
                               @RequestHeader("useragent") String userAgent,
                               @RequestBody HongqiTokenReq req);

    /**
     * 应付清单接收（EP 接口 salb902/receive）
     *
     * @param jwt 访问令牌（getToken 返回值，请求头 Jwt）
     * @param userAgent 固定请求头 useragent（Nacos 配置）
     * @param tenantId 固定请求头 tenantId（Nacos 配置）
     * @param companyId 固定请求头 companyid（Nacos 配置）
     * @param appkey 固定请求头 appkey（Nacos 配置）
     * @param req 应付清单请求（records 结构为 EP 文档格式）
     * @return 接收结果（code=200 成功）
     */
    @PostMapping("/api/sal-contract/salb902/receive")
    HongqiEpReceiveResult receivePayableList(@RequestHeader("Jwt") String jwt,
                                             @RequestHeader("useragent") String userAgent,
                                             @RequestHeader("tenantId") String tenantId,
                                             @RequestHeader("companyid") String companyId,
                                             @RequestHeader("appkey") String appkey,
                                             @RequestBody PayableListSyncHqRecordReq req);
}