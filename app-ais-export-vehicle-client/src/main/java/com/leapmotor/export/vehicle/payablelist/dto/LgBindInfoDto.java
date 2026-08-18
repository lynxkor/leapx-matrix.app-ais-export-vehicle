package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 保函绑定信息DTO
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class LgBindInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 保函ID
     */
    private String lgId;

    /**
     * 保函编号
     */
    private String lgCode;

    /**
     * 保函类型
     */
    private String lgType;

    /**
     * SO单号
     */
    private String soNo;

    /**
     * VIN号
     */
    private String vin;

    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;

    /**
     * 对接类型
     */
    private String bindingType;
}
