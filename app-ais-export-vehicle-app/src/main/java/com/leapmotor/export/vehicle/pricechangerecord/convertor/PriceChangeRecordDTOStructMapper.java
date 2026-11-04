package com.leapmotor.export.vehicle.pricechangerecord.convertor;

import com.leapmotor.export.vehicle.pricechangerecord.model.PriceChangeRecordDo;
import com.leapmotor.export.vehicle.pricechangerecord.query.PriceChangeRecordQo;
import com.leapmotor.export.vehicle.pricechangerecord.req.PriceChangeRecordParam;
import com.leapmotor.export.vehicle.pricechangerecord.resp.PriceChangeRecordResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PriceChangeRecordDTOStructMapper {

    PriceChangeRecordDTOStructMapper INSTANCE = Mappers.getMapper(PriceChangeRecordDTOStructMapper.class);

    PriceChangeRecordQo paramToQo(PriceChangeRecordParam source);

    PriceChangeRecordResp domainToResp(PriceChangeRecordDo source);

    List<PriceChangeRecordResp> domainsToResps(List<PriceChangeRecordDo> sources);

    PageResult<PriceChangeRecordResp> domainsToRespPage(PageResult<PriceChangeRecordDo> sources);
}
