package com.leapmotor.export.vehicle.acl.http.aps.response;

import lombok.Data;

/**
 * APS 接口返回（契约待 APS 侧，先占位：code 200=成功）
 */
@Data
public class ApsResult {

    private int code;
    private String message;
}
