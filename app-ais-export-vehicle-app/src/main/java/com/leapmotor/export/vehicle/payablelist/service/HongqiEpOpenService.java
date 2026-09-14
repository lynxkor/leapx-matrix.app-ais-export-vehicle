package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiTokenResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 红旗 EP 开放接口辅助服务（供排查问题、补推数据使用）
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
     * 获取红旗 EP 访问令牌
     */
    public HongqiTokenResult getToken() {
        HongqiTokenResult tokenResult = hongqiGateway.getEpToken();
        log.info("获取红旗EP token: status={}, code={}",
                tokenResult == null ? null : tokenResult.getStatus(),
                tokenResult == null ? null : tokenResult.getCode());
        return tokenResult;
    }

    /**
     * 同步应付清单到红旗 EP（先取 token 再带 Jwt 调接收接口）
     */
    public HongqiResult syncPayableList(PayableListSyncHqRecordReq req) {
        return hongqiGateway.syncPayableListEp(req);
    }
}