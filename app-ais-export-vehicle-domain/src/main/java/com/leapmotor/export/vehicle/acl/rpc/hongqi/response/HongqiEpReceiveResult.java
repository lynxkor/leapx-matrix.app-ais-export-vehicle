package com.leapmotor.export.vehicle.acl.rpc.hongqi.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 红旗 EP 应付清单接收响应（{code, msg, data}，code=200 成功）
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Data
public class HongqiEpReceiveResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口状态码：200=成功；非200=失败
     */
    private Integer code;

    /**
     * 返回信息：成功为「接收成功」；失败返回具体原因
     */
    private String msg;

    /**
     * 业务数据（本接口固定 null）
     */
    private Object data;
}