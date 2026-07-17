package com.leapmotor.export.vehicle.acl.http.sap.resp;

import lombok.Data;

import java.io.Serializable;

/**
 * SAP 接口返回（契约待定，先占位：code 200=成功）。
 */
@Data
public class SapResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
}
