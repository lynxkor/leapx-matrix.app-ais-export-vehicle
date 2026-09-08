package com.leapmotor.export.vehicle.acl.rpc.sap.resp;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应付清单同步 SAP 接口返回
 *   ES_RET：总状态，明细全部检查通过为 S，有一条错误则为 E
 *   RET_TAB：红旗付款车辆明细-返回参数，逐行返回明细状态
 *
 */
@Data
public class PayableListSyncSapResult implements Serializable {

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
         * 总返回结果（总状态）
         */
        @JsonProperty("ES_RET")
        @JSONField(name = "ES_RET")
        private EsRet esRet;

        /**
         * 应付清单明细-返回参数（逐行明细状态）
         */
        @JsonProperty("RET_TAB")
        @JSONField(name = "RET_TAB")
        private List<RetTabItem> retTab;
    }

    /**
     * ES_RET 结果结构
     */
    @Data
    public static class EsRet implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 消息类型：S 成功，E 错误
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

    /**
     * RET_TAB 表格行项目（应付清单明细返回）
     */
    @Data
    public static class RetTabItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 付款清单编号
         */
        @JsonProperty("PAYNR")
        @JSONField(name = "PAYNR")
        private String paynr;

        /**
         * 红旗PO
         */
        @JsonProperty("PORDER")
        @JSONField(name = "PORDER")
        private String porder;

        /**
         * 销服SO
         */
        @JsonProperty("SORDER")
        @JSONField(name = "SORDER")
        private String sorder;

        /**
         * VIN码
         */
        @JsonProperty("VHVIN")
        @JSONField(name = "VHVIN")
        private String vhvin;

        /**
         * 明细状态：S 成功，E 错误
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
