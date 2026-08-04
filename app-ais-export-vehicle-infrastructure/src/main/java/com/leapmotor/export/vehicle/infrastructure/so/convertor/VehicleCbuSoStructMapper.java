package com.leapmotor.export.vehicle.infrastructure.so.convertor;

import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;

import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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

    @Mappings({
            @Mapping(target = "salesCompanyName", ignore = true),
            @Mapping(target = "internalVehicleNo", ignore = true),
            @Mapping(target = "productionIdentifyCode", ignore = true),
            @Mapping(target = "vin", ignore = true),
            @Mapping(target = "materialCode", ignore = true),
            @Mapping(target = "materialDescription", ignore = true),
            @Mapping(target = "salesMarket", ignore = true),
            @Mapping(target = "modelYear", ignore = true),
            @Mapping(target = "optionalPackage", ignore = true),
            @Mapping(target = "planOfflineDate", ignore = true),
            @Mapping(target = "scheduleTime", ignore = true),
            @Mapping(target = "barcodePrintTime", ignore = true),
            @Mapping(target = "bodyInTime", ignore = true),
            @Mapping(target = "paintInTime", ignore = true),
            @Mapping(target = "assemblyInTime", ignore = true),
            @Mapping(target = "assemblyOutTime", ignore = true),
            @Mapping(target = "syncApsResult", ignore = true),
            @Mapping(target = "syncApsTime", ignore = true),
            @Mapping(target = "syncApsResponseInfo", ignore = true),
            @Mapping(target = "syncSapResult", ignore = true),
            @Mapping(target = "syncSapTime", ignore = true),
            @Mapping(target = "syncSapResponseInfo", ignore = true),
            @Mapping(target = "syncCustomerResult", ignore = true),
            @Mapping(target = "syncCustomerTime", ignore = true),
            @Mapping(target = "syncCustomerResponseInfo", ignore = true)
    })
    VehicleCbuSoDo dataObjectToDomain(VehicleCbuSo source);

    List<VehicleCbuSoDo> dataObjectsToDomains(List<VehicleCbuSo> sources);

}
