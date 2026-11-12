package com.leapmotor.export.vehicle.pricechangerecord.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.leapmotor.paltform.ais.common.model.base.LpBaseResponse;
import lombok.Data;

import java.io.Serializable;

@Data
public class PriceChangeRecordResponse extends LpBaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String serialNo;
    private String vin;
    private String soNo;
    private BigDecimal beforePrice;
    private BigDecimal afterPrice;
    private String payableListNo;
    private Integer status;
    private Long ownerUserId;
    private Long ownerOrgId;
}
