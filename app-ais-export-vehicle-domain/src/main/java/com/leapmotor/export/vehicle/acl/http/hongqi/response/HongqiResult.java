package com.leapmotor.export.vehicle.acl.http.hongqi.response;

import lombok.Data;

/**
 * 红旗接口返回（契约待定，先占位：code 200=成功）
 */
@Data
public class HongqiResult {

    private int code;
    private String message;
    private Object data;
}
