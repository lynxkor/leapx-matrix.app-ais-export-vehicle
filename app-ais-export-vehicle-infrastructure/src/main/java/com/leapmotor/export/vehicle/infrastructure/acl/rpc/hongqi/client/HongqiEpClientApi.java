package com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.client;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiEpReceiveResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 红旗 EP 系统 Feign 客户端，经统一出口网关 app-ais-xuanniao 转发，前缀 /external/hongqi-ep。
 *
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Component
@FeignClient(name = "app-ais-xuanniao", contextId = "HongqiEpClientApi", path = "/external/hongqi-ep")
public interface HongqiEpClientApi {

    /**
     * 应付清单接收（EP 接口 salb902/receive，Jwt 与固定请求头由网关注入）
     *
     * @param request 应付清单请求（records 结构为 EP 文档格式）
     * @return 接收结果（code=200 成功）
     */
    @PostMapping("/api/sal-contract/salb902/receive")
    HongqiEpReceiveResult receivePayableList(@RequestBody PayableListSyncHqRecordReq request);
}