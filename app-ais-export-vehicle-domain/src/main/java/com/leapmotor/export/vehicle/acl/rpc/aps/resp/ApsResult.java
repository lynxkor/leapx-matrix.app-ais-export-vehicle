package com.leapmotor.export.vehicle.acl.rpc.aps.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * APS 接口返回，对齐《海外销售订单接口文档》：{code:200/500, msg, data}。
 *
 * <p>code 200=成功、500=失败；msg 透传到接口同步记录与异常信息。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
public class ApsResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;

    @JsonProperty("msg")
    private String message;
}
