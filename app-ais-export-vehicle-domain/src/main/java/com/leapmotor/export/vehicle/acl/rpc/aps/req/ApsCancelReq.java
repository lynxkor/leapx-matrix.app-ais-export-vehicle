package com.leapmotor.export.vehicle.acl.rpc.aps.req;

import lombok.Data;

import java.io.Serializable;

/**
 * APS 作废同步请求，对齐《海外销售订单接口文档》POST /api/abroad/cancelOrder。
 *
 * <p>字段对齐：vehPoName、vehCbuSoName 二者至少传一个，changeType 固定 -1（代表作废）。
 * 仅传 vehPoName 作废该 PO 下所有 SO；同时传 vehPoName + vehCbuSoName 仅作废该 SO。
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Data
public class ApsCancelReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * PO 单号（与 vehCbuSoName 至少传一个）
     */
    private String vehPoName;

    /**
     * SO 单号（与 vehPoName 至少传一个）
     */
    private String vehCbuSoName;

    /**
     * 变更类型：-1 作废
     */
    private Integer changeType = -1;
}
