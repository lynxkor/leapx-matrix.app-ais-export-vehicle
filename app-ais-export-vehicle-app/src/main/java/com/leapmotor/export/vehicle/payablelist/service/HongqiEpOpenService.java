package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 红旗 EP 开放接口
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Slf4j
@Service
@AllArgsConstructor
public class HongqiEpOpenService {

    private final HongqiGateway hongqiGateway;

    /**
     * 同步应付清单到红旗 EP（token 与固定请求头由入口网关注入）
     */
    public HongqiResult syncPayableList(PayableListSyncHqRecordReq req) {
        return hongqiGateway.syncPayableListEp(req);
    }
}