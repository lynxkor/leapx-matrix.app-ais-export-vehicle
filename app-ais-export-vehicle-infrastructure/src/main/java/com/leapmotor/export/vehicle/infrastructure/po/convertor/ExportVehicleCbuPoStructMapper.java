package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleCbuPo领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportVehicleCbuPoStructMapper {
    ExportVehicleCbuPoStructMapper INSTANCE = Mappers.getMapper(ExportVehicleCbuPoStructMapper.class);

    ExportVehicleCbuPo domainToDataObject(ExportVehicleCbuPoDo source);

    List<ExportVehicleCbuPo> domainToDataObjects(List<ExportVehicleCbuPoDo> sources);

    ExportVehicleCbuPoDo dataObjectToDomain(ExportVehicleCbuPo source);

    List<ExportVehicleCbuPoDo> dataObjectsToDomains(List<ExportVehicleCbuPo> sources);

}
