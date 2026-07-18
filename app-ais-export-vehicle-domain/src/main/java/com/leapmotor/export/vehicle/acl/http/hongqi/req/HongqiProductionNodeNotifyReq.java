package com.leapmotor.export.vehicle.acl.http.hongqi.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 红旗车辆生产信息同步请求（VDO53003），字段直接使用红旗接口标识。
 */
@Data
public class HongqiProductionNodeNotifyReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO 单号
     */
    private String poNo;

    /**
     * SO 单号
     */
    private String soNo;

    /**
     * VIN 码
     */
    private String vin;

    /**
     * 18 位零跑物料编码
     */
    private String lpMtlNo;

    /**
     * 预计总装下线日期，格式 yyyyMMdd
     */
    private String planOfflineDate;

    /**
     * 实际总装上线时间，格式 yyyyMMddHHmmss
     */
    private String actOnlineTime;

    /**
     * 实际总装下线时间，格式 yyyyMMddHHmmss
     */
    private String actOfflineTime;
}
