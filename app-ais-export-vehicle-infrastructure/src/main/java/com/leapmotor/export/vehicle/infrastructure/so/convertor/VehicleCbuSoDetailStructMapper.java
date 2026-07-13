package com.leapmotor.export.vehicle.infrastructure.so.convertor;

import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;

import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * VehicleCbuSoDetail领域转换工具
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Mapper
public interface VehicleCbuSoDetailStructMapper {

    VehicleCbuSoDetailStructMapper INSTANCE = Mappers.getMapper(VehicleCbuSoDetailStructMapper.class);

    VehicleCbuSoDetail domainToDataObject(VehicleCbuSoDetailDo source);

    List<VehicleCbuSoDetail> domainToDataObjects(List<VehicleCbuSoDetailDo> sources);

    VehicleCbuSoDetailDo dataObjectToDomain(VehicleCbuSoDetail source);

    List<VehicleCbuSoDetailDo> dataObjectsToDomains(List<VehicleCbuSoDetail> sources);

}
