package com.leapmotor.export.vehicle.pricechangerecord.query;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.leapmotor.paltform.ais.common.model.base.LpBaseQo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PriceChangeRecordQo extends LpBaseQo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> vins;

    private List<String> soNos;

    private LocalDateTime createTimeStart;

    private LocalDateTime createTimeEnd;
}
