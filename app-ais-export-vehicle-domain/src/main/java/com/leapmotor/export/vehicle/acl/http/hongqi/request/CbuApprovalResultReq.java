package com.leapmotor.export.vehicle.acl.http.hongqi.request;

import lombok.Data;

/**
 * 红旗 CBU 审批结果回传请求（契约待红旗提供，字段先占位）
 */
@Data
public class CbuApprovalResultReq {

    private String poNo;          // PO单号(bizNo)
    private String approvalNo;    // 审批单号
    private String vin;
    private String result;        // APPROVED / REJECTED
    private String opinion;       // 审批意见
    private String approver;      // 审批人
}
