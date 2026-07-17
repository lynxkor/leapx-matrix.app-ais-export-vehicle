package com.leapmotor.export.vehicle.acl.http.aps.req;

import lombok.Data;

import java.io.Serializable;

/**
 * APS 下发排产同步请求（契约待 APS 侧，先占位）。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
public class ApsIssueReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SO 主表 ID
     */
    private String soId;

    /**
     * SO 单号
     */
    private String soNo;

    /**
     * PO 单号
     */
    private String poNo;

    /**
     * 变更类型：1 下发排产
     */
    private Integer changeType = 1;
}
