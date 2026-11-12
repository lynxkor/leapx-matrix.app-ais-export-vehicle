package com.leapmotor.export.vehicle.pricechangerecord.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.leapmotor.paltform.ais.common.model.base.LpBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PriceChangeRecordDo extends LpBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serialNo;

    private String vin;

    private String soNo;

    private BigDecimal beforePrice;

    private BigDecimal afterPrice;

    private String payableListNo;

    private String statusDesc;

    private Integer status;

    private Long ownerUserId;

    private Long ownerOrgId;
}
