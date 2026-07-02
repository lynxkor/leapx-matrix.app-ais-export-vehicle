package com.leapmotor.export.vehicle.letterGuarantee.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 资金变动类型(对应字段 transaction_type,字典 letter_guarantee_record_type)
 *
 * <p>流水账的 5 种资金动作,详见设计文档 §3.4.5:
 * <ul>
 *   <li>10 冻结:PO 提交时占用可用金额</li>
 *   <li>20 解冻:PO 驳回时释放占用金额</li>
 *   <li>30 转应收:质检通过,占用 → 应收</li>
 *   <li>40 释放应收:SAP 清账,应收冲销</li>
 *   <li>50 保函失效:lg_status 20→30,无可用金额时置 0(凌晨任务 / 手动失效)</li>
 * </ul>
 *
 * <p>编码 10/20/30/40/50 与主表审核/保函状态对齐,预留扩展位。
 */
public enum RecordTypeEnum implements IBaseEnum<String> {
    FREEZE              ("10", "冻结"),
    UNFREEZE            ("20", "解冻"),
    TRANSFER_RECEIVABLE ("30", "转应收"),
    RELEASE_RECEIVABLE  ("40", "释放应收"),
    EXPIRE              ("50", "保函失效");

    private final String code;
    private final String message;

    RecordTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override public String getCode()    { return code; }
    @Override public String getMessage() { return message; }

    public static RecordTypeEnum of(String code) {
        for (RecordTypeEnum e : values()) if (e.code.equals(code)) return e;
        return null;
    }
}
