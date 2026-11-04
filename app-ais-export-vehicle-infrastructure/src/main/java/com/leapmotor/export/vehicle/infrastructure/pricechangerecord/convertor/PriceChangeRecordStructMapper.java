package com.leapmotor.export.vehicle.infrastructure.pricechangerecord.convertor;

import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.entity.PriceChangeRecord;
import com.leapmotor.export.vehicle.pricechangerecord.model.PriceChangeRecordDo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PriceChangeRecordStructMapper {

    PriceChangeRecordStructMapper INSTANCE = Mappers.getMapper(PriceChangeRecordStructMapper.class);

    PriceChangeRecord domainToDataObject(PriceChangeRecordDo source);

    List<PriceChangeRecord> domainToDataObjects(List<PriceChangeRecordDo> sources);

    PriceChangeRecordDo dataObjectToDomain(PriceChangeRecord source);

    List<PriceChangeRecordDo> dataObjectsToDomains(List<PriceChangeRecord> sources);
}
