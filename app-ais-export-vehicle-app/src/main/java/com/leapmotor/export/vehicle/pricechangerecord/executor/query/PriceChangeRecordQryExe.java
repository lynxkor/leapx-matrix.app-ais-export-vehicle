package com.leapmotor.export.vehicle.pricechangerecord.executor.query;

import com.leapmotor.export.vehicle.pricechangerecord.convertor.PriceChangeRecordDTOStructMapper;
import com.leapmotor.export.vehicle.pricechangerecord.gateway.PriceChangeRecordGateway;
import com.leapmotor.export.vehicle.pricechangerecord.model.PriceChangeRecordDo;
import com.leapmotor.export.vehicle.pricechangerecord.query.PriceChangeRecordQo;
import com.leapmotor.export.vehicle.pricechangerecord.req.PriceChangeRecordParam;
import com.leapmotor.export.vehicle.pricechangerecord.resp.PriceChangeRecordResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PriceChangeRecordQryExe {

    private final PriceChangeRecordGateway priceChangeRecordGateway;

    public PageResult<PriceChangeRecordResp> pagePriceChangeRecord(PriceChangeRecordParam param) {
        PriceChangeRecordQo qo = PriceChangeRecordDTOStructMapper.INSTANCE.paramToQo(param);
        PageResult<PriceChangeRecordDo> page = priceChangeRecordGateway.pagePriceChangeRecord(qo);
        return PriceChangeRecordDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }
}
