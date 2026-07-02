package com.leapmotor.export.vehicle.letterGuarantee.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 保函审核状态(对应字段 approval_status,字典 letter_guarantee_approval_status)
 *
 * <p>状态机(详见设计文档 §4):
 * <pre>
 *   草稿(10) ──submit──▶ 已提交(20) ──approve──▶ 审核通过(30)   lg_status = 20(有效)
 *                                  └──reject───▶ 驳回(40)      lg_status = 10(未生效)
 *   草稿(10) / 驳回(40) ──cancel──▶ 作废(50)                    lg_status = 10(未生效)
 *   审核通过(30) ──expireEarly──▶ 失效(走 lg_status = 30,approval_status 保持 30)
 * </pre>
 */
public enum ApprovalStatusEnum implements IBaseEnum<String> {
    DRAFT     ("10", "草稿"),
    SUBMITTED ("20", "已提交"),
    APPROVED  ("30", "审核通过"),
    REJECTED  ("40", "驳回"),
    CANCELLED ("50", "作废");

    private final String code;
    private final String message;

    ApprovalStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode()    { return code; }
    @Override public String getMessage() { return message; }

    public static ApprovalStatusEnum of(String code) {
        for (ApprovalStatusEnum e : values()) if (e.code.equals(code)) return e;
        return null;
    }
}
