package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportCbuVehiclePo领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportCbuVehiclePoStructMapper {
    ExportCbuVehiclePoStructMapper INSTANCE = Mappers.getMapper(ExportCbuVehiclePoStructMapper.class);

    ExportCbuVehiclePo domainToDataObject(ExportCbuVehiclePoDo source);

    List<ExportCbuVehiclePo> domainToDataObjects(List<ExportCbuVehiclePoDo> sources);

    ExportCbuVehiclePoDo dataObjectToDomain(ExportCbuVehiclePo source);

    List<ExportCbuVehiclePoDo> dataObjectsToDomains(List<ExportCbuVehiclePo> sources);

}
