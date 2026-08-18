package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoGuarantee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleCbuPoGuarantee领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportVehicleCbuPoGuaranteeStructMapper {
    ExportVehicleCbuPoGuaranteeStructMapper INSTANCE = Mappers.getMapper(ExportVehicleCbuPoGuaranteeStructMapper.class);

    ExportVehicleCbuPoGuarantee domainToDataObject(ExportVehicleCbuPoGuaranteeDo source);

    List<ExportVehicleCbuPoGuarantee> domainToDataObjects(List<ExportVehicleCbuPoGuaranteeDo> sources);

    ExportVehicleCbuPoGuaranteeDo dataObjectToDomain(ExportVehicleCbuPoGuarantee source);

    List<ExportVehicleCbuPoGuaranteeDo> dataObjectsToDomains(List<ExportVehicleCbuPoGuarantee> sources);

}
