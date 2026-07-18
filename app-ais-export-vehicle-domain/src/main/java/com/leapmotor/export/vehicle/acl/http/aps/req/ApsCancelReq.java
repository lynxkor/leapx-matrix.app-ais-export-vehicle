package com.leapmotor.export.vehicle.acl.http.aps.req;

import lombok.Data;

import java.io.Serializable;

/**
 * APS 作废同步请求（契约待 APS 侧，先占位）。
 *
 * <p>字段对齐：poNo、soNo 二者至少传一个，changeType 固定 -1（代表作废）。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
public class ApsCancelReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO 单号（与 soNo 至少传一个）
     */
    private String poNo;

    /**
     * SO 单号（与 poNo 至少传一个）
     */
    private String soNo;

    /**
     * 变更类型：-1 作废
     */
    private Integer changeType = -1;
}
