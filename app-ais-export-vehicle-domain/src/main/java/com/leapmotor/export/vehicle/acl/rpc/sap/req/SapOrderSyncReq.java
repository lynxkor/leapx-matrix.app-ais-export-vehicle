package com.leapmotor.export.vehicle.acl.rpc.sap.req;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * SO 订单同步 SAP 请求（按 SAP 接口文档）。
 *
 * <p>字段命名采用驼峰，JSON 序列化时使用大写字段名。</p>
 */
@Data
public class SapOrderSyncReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 外围系统标识，默认 DMS
     */
    @JsonProperty("IV_SYSID")
    @JSONField(name = "IV_SYSID")
    private String ivSysid;

    /**
     * DMS 传入结构（header）
     */
    @JsonProperty("IS_HEAD")
    @JSONField(name = "IS_HEAD")
    private IsHead isHead;

    /**
     * DMS 传入行数据（items）
     */
    @JsonProperty("IM_TAB")
    @JSONField(name = "IM_TAB")
    private List<ImTabItem> imTab;

    /**
     * IS_HEAD 头结构
     */
    @Data
    public static class IsHead implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 销服 SO，唯一标识
         */
        @JsonProperty("SORDER")
        @JSONField(name = "SORDER")
        private String sorder;

        /**
         * 客户 PO
         */
        @JsonProperty("PORDER")
        @JSONField(name = "PORDER")
        private String porder;

        /**
         * 订单日期，格式 YYYYMMDD
         */
        @JsonProperty("ERDAT")
        @JSONField(name = "ERDAT")
        private String erdat;

        /**
         * 订单/物流状态 77：红旗质检通过
         */
        @JsonProperty("LSTATUS")
        @JSONField(name = "LSTATUS")
        private String lstatus;

        /**
         * 订单状态日期，格式 YYYYMMDD
         */
        @JsonProperty("LSDATUM")
        @JSONField(name = "LSDATUM")
        private String lsdatum;

        /**
         * 销售公司
         */
        @JsonProperty("BUKRS")
        @JSONField(name = "BUKRS")
        private String bukrs;

        /**
         * 客户编号
         */
        @JsonProperty("KUNNR")
        @JSONField(name = "KUNNR")
        private String kunnr;

        /**
         * 订单类型（海外整车 CBU / 台套 KDOK / 散件 KDOL / CKD）
         */
        @JsonProperty("RECORDTYPE")
        @JSONField(name = "RECORDTYPE")
        private String recordType;

        /**
         * 订单分类 100-红旗CBU订单
         */
        @JsonProperty("ORDERWAY")
        @JSONField(name = "ORDERWAY")
        private String orderWay;
    }

    /**
     * IM_TAB 行项目
     */
    @Data
    public static class ImTabItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 拆分订单号
         */
        @JsonProperty("SORDER_SUB")
        @JSONField(name = "SORDER_SUB")
        private String sorderSub;

        /**
         * 内部车辆编号
         */
        @JsonProperty("VHCLE")
        @JSONField(name = "VHCLE")
        private String vhcle;

        /**
         * 车辆标识编号（VIN），CBU 传 VIN
         */
        @JsonProperty("VHVIN")
        @JSONField(name = "VHVIN")
        private String vhvin;

        /**
         * 物料编号
         */
        @JsonProperty("MATNR")
        @JSONField(name = "MATNR")
        private String matnr;

        /**
         * 订单数量，默认 1
         */
        @JsonProperty("MENGE")
        @JSONField(name = "MENGE")
        private BigDecimal menge;

        /**
         * EXW 单价（对外销售价格）
         */
        @JsonProperty("PRICE_EXW")
        @JSONField(name = "PRICE_EXW")
        private BigDecimal priceExw;
    }
}