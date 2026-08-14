package com.leapmotor.export.vehicle.payablelist.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * 应付清单单据状态枚举
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public enum PayableListStatusEnum implements IBaseEnum<String> {

    /**
     * 草稿
     */
    DRAFT("10", "草稿"),

    /**
     * 已提交待确认
     */
    SUBMITTED("20", "已提交待确认"),

    /**
     * 已确认
     */
    CONFIRMED("30", "已确认"),

    /**
     * 驳回
     */
    REJECTED("40", "驳回"),

    /**
     * 部分收款
     */
    PARTIAL_RECEIVED("50", "部分收款"),

    /**
     * 已收款
     */
    RECEIVED("60", "已收款");

    private final String code;
    private final String message;

    PayableListStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getDictType() {
        return "PayableListStatusEnum";
    }
}
