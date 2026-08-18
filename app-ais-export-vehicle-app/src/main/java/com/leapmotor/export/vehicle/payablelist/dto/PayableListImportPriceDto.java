package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 导入价格Excel行数据
 *
 * @author 赵吴涯
 * @since 2026-07-27
 */
@Data
public class PayableListImportPriceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * VIN码
     */
    private String vin;

    /**
     * 最新价格
     */
    private BigDecimal price;

    /**
     * 失败原因
     */
    private String failReason;
}
