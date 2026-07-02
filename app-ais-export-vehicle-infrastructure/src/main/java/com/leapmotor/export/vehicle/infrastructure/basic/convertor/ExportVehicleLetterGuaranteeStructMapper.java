package com.leapmotor.export.vehicle.infrastructure.basic.convertor;

import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;

import com.leapmotor.export.vehicle.infrastructure.basic.entity.ExportVehicleLetterGuarantee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleLetterGuarantee领域转换工具
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Mapper
public interface ExportVehicleLetterGuaranteeStructMapper {
    ExportVehicleLetterGuaranteeStructMapper INSTANCE = Mappers.getMapper(ExportVehicleLetterGuaranteeStructMapper.class);

    ExportVehicleLetterGuarantee domainToDataObject(ExportVehicleLetterGuaranteeDo source);

    List<ExportVehicleLetterGuarantee> domainToDataObjects(List<ExportVehicleLetterGuaranteeDo> sources);

    ExportVehicleLetterGuaranteeDo dataObjectToDomain(ExportVehicleLetterGuarantee source);

    List<ExportVehicleLetterGuaranteeDo> dataObjectsToDomains(List<ExportVehicleLetterGuarantee> sources);

}
