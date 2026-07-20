package com.leapmotor.export.vehicle.acl.rpc.aps.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * APS 接口返回（契约待 APS 侧，先占位：code 200=成功）。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
public class ApsResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
}
