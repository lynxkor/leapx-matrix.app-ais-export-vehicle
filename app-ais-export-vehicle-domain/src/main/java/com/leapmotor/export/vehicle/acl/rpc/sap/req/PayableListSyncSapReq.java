package com.leapmotor.export.vehicle.acl.rpc.sap.req;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应付清单同步 SAP 请求（按 SAP 接口文档 ZSD_DMS_G117_PAYLIST）。
 *
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Data
public class PayableListSyncSapReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统标识，默认 DMS
     */
    @JsonProperty("IV_SYSID")
    @JSONField(name = "IV_SYSID")
    private String ivSysid = "DMS";

    /**
     * 传入 VIN 明细（表格）
     */
    @JsonProperty("IM_TAB")
    @JSONField(name = "IM_TAB")
    private List<ImTabItem> imTab;

    /**
     * IM_TAB 行项目
     */
    @Data
    public static class ImTabItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 付款清单编号（应付清单编号）
         */
        @JsonProperty("PAYNR")
        @JSONField(name = "PAYNR")
        private String paynr;

        /**
         * 红旗 PO
         */
        @JsonProperty("PORDER")
        @JSONField(name = "PORDER")
        private String porder;

        /**
         * 销服 SO
         */
        @JsonProperty("SORDER")
        @JSONField(name = "SORDER")
        private String sorder;

        /**
         * VIN 码
         */
        @JsonProperty("VHVIN")
        @JSONField(name = "VHVIN")
        private String vhvin;
    }
}
