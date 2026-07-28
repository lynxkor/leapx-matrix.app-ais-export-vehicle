package com.leapmotor.export.vehicle.acl.rpc.hongqi.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 红旗 VDO53003 车辆生产信息同步出参（data 数组元素）。
 *
 * <p>每条入参对应一条出参，回填 poNo/soNo/lpMtlNo/vin，并给出接收结果 isSign/errMsg。
 *
 * @author lijianuo
 * @since 2026-07-23
 */
@Data
public class HongqiProductionNodeResultData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO单号（回填入参）
     */
    private String poNo;

    /**
     * SO单号（回填入参）
     */
    private String soNo;

    /**
     * 18位零跑物料编码（回填入参）
     */
    private String lpMtlNo;

    /**
     * VIN（回填入参）
     */
    private String vin;

    /**
     * 是否接收成功：Y=成功 / N=失败
     */
    @JsonProperty("isSign")
    @JSONField(name = "isSign")
    private String isSign;

    /**
     * 错误消息（失败时含具体单号，便于排查）
     */
    @JsonProperty("errMsg")
    @JSONField(name = "errMsg")
    private String errMsg;
}
