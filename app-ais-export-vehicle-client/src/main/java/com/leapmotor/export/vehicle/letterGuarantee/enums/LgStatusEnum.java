package com.leapmotor.export.vehicle.letterGuarantee.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 保函状态(对应字段 lg_status,字典 letter_guarantee_lg_status)
 *
 * <p>三态机(详见设计文档 §4):
 * <ul>
 *   <li>10(未生效):草稿/已提交/驳回/作废 — 主表字段 NOT NULL DEFAULT '10'</li>
 *   <li>20(有效):审核通过时由 Gateway 置位</li>
 *   <li>30(失效):到期自动失效(@XxlJob)或手动失效(expireEarly)</li>
 * </ul>
 */
public enum LgStatusEnum implements IBaseEnum<String> {
    INACTIVE  ("10", "未生效"),
    EFFECTIVE ("20", "有效"),
    INVALID   ("30", "失效");

    private final String code;
    private final String message;

    LgStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode()    { return code; }
    @Override public String getMessage() { return message; }

    public static LgStatusEnum of(String code) {
        for (LgStatusEnum e : values()) if (e.code.equals(code)) return e;
        return null;
    }
}
