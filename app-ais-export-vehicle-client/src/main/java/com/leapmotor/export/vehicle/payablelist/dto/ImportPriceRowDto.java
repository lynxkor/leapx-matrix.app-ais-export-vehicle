package com.leapmotor.export.vehicle.payablelist.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 导入价格行DTO
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Data
public class ImportPriceRowDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * VIN号
     */
    private String vin;

    /**
     * 车辆价格
     */
    private BigDecimal price;
}
