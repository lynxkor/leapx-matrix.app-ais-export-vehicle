package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleCbuPoDetail领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportVehicleCbuPoDetailStructMapper {
    ExportVehicleCbuPoDetailStructMapper INSTANCE = Mappers.getMapper(ExportVehicleCbuPoDetailStructMapper.class);

    ExportVehicleCbuPoDetail domainToDataObject(ExportVehicleCbuPoDetailDo source);

    List<ExportVehicleCbuPoDetail> domainToDataObjects(List<ExportVehicleCbuPoDetailDo> sources);

    ExportVehicleCbuPoDetailDo dataObjectToDomain(ExportVehicleCbuPoDetail source);

    List<ExportVehicleCbuPoDetailDo> dataObjectsToDomains(List<ExportVehicleCbuPoDetail> sources);

}
