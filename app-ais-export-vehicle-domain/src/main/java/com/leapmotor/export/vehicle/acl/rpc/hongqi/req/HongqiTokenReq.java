package com.leapmotor.export.vehicle.acl.rpc.hongqi.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 红旗 EP 获取 Token 请求
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Data
public class HongqiTokenReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用标识（EP 提供，测试/生产一致）
     */
    private String appkey;

    /**
     * 应用密钥（EP 提供，测试/生产一致）
     */
    private String secretkey;

    /**
     * 请求时间戳（毫秒）
     */
    private Long loginTimestamp;
}