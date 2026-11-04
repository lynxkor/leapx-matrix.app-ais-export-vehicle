package com.leapmotor.export.vehicle.pricechangerecord.gateway;

import com.leapmotor.export.vehicle.pricechangerecord.model.PriceChangeRecordDo;
import com.leapmotor.export.vehicle.pricechangerecord.query.PriceChangeRecordQo;
import com.leapmotor.paltform.ais.common.model.response.PageResult;

import java.util.List;

public interface PriceChangeRecordGateway {

    String addPriceChangeRecord(PriceChangeRecordDo recordDo);

    int addBatchPriceChangeRecord(List<PriceChangeRecordDo> recordDos);

    PriceChangeRecordDo getById(String id);

    PageResult<PriceChangeRecordDo> pagePriceChangeRecord(PriceChangeRecordQo qo);
}
