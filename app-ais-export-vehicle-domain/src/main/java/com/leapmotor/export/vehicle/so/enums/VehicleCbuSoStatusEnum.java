package com.leapmotor.export.vehicle.so.enums;

import com.leapmotor.paltform.ais.common.model.enums.IBaseEnum;

/**
 * CBU 整车销售订单状态枚举（对应字段 order_status）
 *
 * <p>状态机：
 * <pre>
 *   1001 未下发生产 ──下发──▶ 1002 已下发生产 ──排产──▶ 1003 排产中
 *        │                         │
 *        │                         ├──▶ 1004 焊装上线 ──▶ 1005 涂装上线 ──▶ 1006 总装上线 ──▶ 1007 总装下线
 *        │                         │                                                    │
 *        │                         │                                                    ├──▶ 1008 质检通过 ──▶ 1011 已收款
 *        │                         │                                                    ├──▶ 1009 质检未通过
 *        │                         │                                                    └──▶ 1010 质检未收到车
 *        │                         │
 *        └──▶ 1012 已作废（仅未下发生产/排产中可作废，焊装上线及之后不可作废）
 * </pre>
 */
public enum VehicleCbuSoStatusEnum implements IBaseEnum<Integer> {

    NOT_ISSUED(1001, "未下发生产"),
    ISSUED(1002, "已下发生产"),
    SCHEDULING(1003, "排产中"),
    BODY_IN(1004, "焊装上线"),
    PAINT_IN(1005, "涂装上线"),
    ASSEMBLY_IN(1006, "总装上线"),
    ASSEMBLY_OUT(1007, "总装下线"),
    QC_PASSED(1008, "质检通过"),
    QC_FAILED(1009, "质检未通过"),
    QC_NO_VEHICLE(1010, "质检未收到车"),
    RECEIVED(1011, "已收款"),
    CANCELLED(1012, "已作废");

    private final Integer code;
    private final String message;

    VehicleCbuSoStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static VehicleCbuSoStatusEnum of(Integer code) {
        for (VehicleCbuSoStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * APS 外部状态码映射（不修改对象，仅返回枚举）：
     * 1->排产中、2->焊装上线、3->涂装上线、4->总装上线、5->总装下线。
     *
     * @param apsStatus APS 外部状态码
     * @return 对应订单状态枚举，不支持时返回 null
     */
    public static VehicleCbuSoStatusEnum fromApsStatus(Integer apsStatus) {
        if (apsStatus == null) {
            return null;
        }
        return switch (apsStatus) {
            case 1 -> SCHEDULING;
            case 2 -> BODY_IN;
            case 3 -> PAINT_IN;
            case 4 -> ASSEMBLY_IN;
            case 5 -> ASSEMBLY_OUT;
            default -> null;
        };
    }
}
