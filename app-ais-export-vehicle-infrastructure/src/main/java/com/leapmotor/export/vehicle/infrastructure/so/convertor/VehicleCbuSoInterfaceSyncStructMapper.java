package com.leapmotor.export.vehicle.infrastructure.so.convertor;

import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;

import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoInterfaceSync;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * VehicleCbuSoInterfaceSync领域转换工具
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Mapper
public interface VehicleCbuSoInterfaceSyncStructMapper {

    VehicleCbuSoInterfaceSyncStructMapper INSTANCE = Mappers.getMapper(VehicleCbuSoInterfaceSyncStructMapper.class);

    VehicleCbuSoInterfaceSync domainToDataObject(VehicleCbuSoInterfaceSyncDo source);

    List<VehicleCbuSoInterfaceSync> domainToDataObjects(List<VehicleCbuSoInterfaceSyncDo> sources);

    VehicleCbuSoInterfaceSyncDo dataObjectToDomain(VehicleCbuSoInterfaceSync source);

    List<VehicleCbuSoInterfaceSyncDo> dataObjectsToDomains(List<VehicleCbuSoInterfaceSync> sources);

}
