package com.leapmotor.export.vehicle.infrastructure.basic.convertor;

import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;

import com.leapmotor.export.vehicle.infrastructure.basic.entity.ExportVehicleLetterGuaranteeRecord;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleLetterGuaranteeRecord领域转换工具
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Mapper
public interface ExportVehicleLetterGuaranteeRecordStructMapper {
    ExportVehicleLetterGuaranteeRecordStructMapper INSTANCE = Mappers.getMapper(ExportVehicleLetterGuaranteeRecordStructMapper.class);

    ExportVehicleLetterGuaranteeRecord domainToDataObject(ExportVehicleLetterGuaranteeRecordDo source);

    List<ExportVehicleLetterGuaranteeRecord> domainToDataObjects(List<ExportVehicleLetterGuaranteeRecordDo> sources);

    ExportVehicleLetterGuaranteeRecordDo dataObjectToDomain(ExportVehicleLetterGuaranteeRecord source);

    List<ExportVehicleLetterGuaranteeRecordDo> dataObjectsToDomains(List<ExportVehicleLetterGuaranteeRecord> sources);

}
