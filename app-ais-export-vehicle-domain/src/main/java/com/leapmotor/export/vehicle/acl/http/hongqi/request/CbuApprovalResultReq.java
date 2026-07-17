package com.leapmotor.export.vehicle.acl.http.hongqi.request;

import lombok.Data;

/**
 * 红旗 CBU 审批结果回传请求（接口2，已确认 2026-07-14，目标联调 8/20）。
 * 方向：零跑销服系统 → 红旗。触发：商务驳回、供需审批通过/驳回。
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Data
public class CbuApprovalResultReq {

    /** PO单号 */
    private String poNo;

    /** 计划版本号，例 20260305V1 */
    private String planVersion;

    /** 状态：R=驳回 / A=审批通过 */
    private String status;

    /** 审核时间，格式 YYYYMMDD HH:MM:SS */
    private String auditTime;

    /** 说明（驳回原因，驳回时必填） */
    private String remark;
}
