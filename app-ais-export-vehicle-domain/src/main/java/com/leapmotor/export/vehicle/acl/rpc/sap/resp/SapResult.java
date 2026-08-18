package com.leapmotor.export.vehicle.acl.rpc.sap.resp;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * SAP 接口返回（按 SO 订单同步 SAP 出参结构）。
 *
 * <p>外层 code/message/success 为网关/框架级返回，body.esRet.retCode 为业务级结果：</p>
 * <ul>
 *   <li>S 成功</li>
 *   <li>E 错误</li>
 *   <li>W 警告</li>
 * </ul>
 */
@Data
public class SapResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型（框架级，如 "0" 表示成功）
     */
    private String code;

    /**
     * 消息文本（框架级）
     */
    private String message;

    /**
     * 是否成功（框架级）
     */
    private boolean success;

    /**
     * SAP 业务返回体
     */
    private Body body;

    /**
     * SAP 业务返回体
     */
    @Data
    public static class Body implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * SAP 返回结果
         */
        @JsonProperty("ES_RET")
        @JSONField(name = "ES_RET")
        private SapRet esRet;
    }

    /**
     * ES_RET 结果结构
     */
    @Data
    public static class SapRet implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 消息类型：S 成功，E 错误，W 警告
         */
        @JsonProperty("RET_CODE")
        @JSONField(name = "RET_CODE")
        private String retCode;

        /**
         * 消息文本
         */
        @JsonProperty("RET_REASON")
        @JSONField(name = "RET_REASON")
        private String retReason;
    }
}