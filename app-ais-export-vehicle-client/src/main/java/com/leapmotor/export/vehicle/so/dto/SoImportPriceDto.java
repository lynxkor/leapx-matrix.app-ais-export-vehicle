package com.leapmotor.export.vehicle.so.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class SoImportPriceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String soNo;

    private BigDecimal price;

    /**
     * 失败原因（仅导入结果回显时填充）
     */
    private String failReason;
}
