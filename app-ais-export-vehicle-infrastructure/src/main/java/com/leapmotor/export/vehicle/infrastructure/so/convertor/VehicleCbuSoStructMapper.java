package com.leapmotor.export.vehicle.infrastructure.so.convertor;

import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;

import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * VehicleCbuSo领域转换工具
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Mapper
public interface VehicleCbuSoStructMapper {

    VehicleCbuSoStructMapper INSTANCE = Mappers.getMapper(VehicleCbuSoStructMapper.class);

    VehicleCbuSo domainToDataObject(VehicleCbuSoDo source);

    List<VehicleCbuSo> domainToDataObjects(List<VehicleCbuSoDo> sources);

    VehicleCbuSoDo dataObjectToDomain(VehicleCbuSo source);

    List<VehicleCbuSoDo> dataObjectsToDomains(List<VehicleCbuSo> sources);

}
