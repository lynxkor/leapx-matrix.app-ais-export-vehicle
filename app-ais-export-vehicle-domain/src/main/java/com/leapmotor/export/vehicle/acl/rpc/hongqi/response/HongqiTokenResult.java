package com.leapmotor.export.vehicle.acl.rpc.hongqi.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 红旗 EP 获取 Token 响应
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Data
public class HongqiTokenResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态：S-成功，E-失败
     */
    private String status;

    /**
     * 响应代码：200-成功；500-失败
     */
    private Integer code;

    /**
     * 令牌对象（失败时为空）
     */
    private TokenData data;

    /**
     * 令牌对象
     */
    @Data
    public static class TokenData implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 访问令牌（调用 EP 业务接口时置于请求头 Jwt，有效期 1 小时）
         */
        private String token;
    }
}