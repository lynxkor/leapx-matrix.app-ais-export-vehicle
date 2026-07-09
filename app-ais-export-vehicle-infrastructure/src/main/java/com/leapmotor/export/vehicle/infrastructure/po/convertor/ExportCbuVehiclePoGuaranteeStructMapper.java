package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoGuaranteeDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePoGuarantee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportCbuVehiclePoGuarantee领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportCbuVehiclePoGuaranteeStructMapper {
    ExportCbuVehiclePoGuaranteeStructMapper INSTANCE = Mappers.getMapper(ExportCbuVehiclePoGuaranteeStructMapper.class);

    ExportCbuVehiclePoGuarantee domainToDataObject(ExportCbuVehiclePoGuaranteeDo source);

    List<ExportCbuVehiclePoGuarantee> domainToDataObjects(List<ExportCbuVehiclePoGuaranteeDo> sources);

    ExportCbuVehiclePoGuaranteeDo dataObjectToDomain(ExportCbuVehiclePoGuarantee source);

    List<ExportCbuVehiclePoGuaranteeDo> dataObjectsToDomains(List<ExportCbuVehiclePoGuarantee> sources);

}
