package com.leapmotor.export.vehicle.acl.http.so.response;

import lombok.Data;

import java.util.List;

/**
 * SO 接口返回（契约待 SO 侧，先占位：code 200=成功）
 */
@Data
public class SoResult {

    private int code;
    private String message;
    private List<SoItem> data;

    @Data
    public static class SoItem {
        private String soId;
        private String soNo;          // SO+年月日+时分+5位流水
        private String poNo;
        private Integer lineNo;       // 行号 1..N
        private String orderStatus;   // 默认 未下发生产
    }
}
