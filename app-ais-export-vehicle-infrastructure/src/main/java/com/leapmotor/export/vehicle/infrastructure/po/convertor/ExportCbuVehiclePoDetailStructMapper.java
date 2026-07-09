package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePoDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportCbuVehiclePoDetail领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportCbuVehiclePoDetailStructMapper {
    ExportCbuVehiclePoDetailStructMapper INSTANCE = Mappers.getMapper(ExportCbuVehiclePoDetailStructMapper.class);

    ExportCbuVehiclePoDetail domainToDataObject(ExportCbuVehiclePoDetailDo source);

    List<ExportCbuVehiclePoDetail> domainToDataObjects(List<ExportCbuVehiclePoDetailDo> sources);

    ExportCbuVehiclePoDetailDo dataObjectToDomain(ExportCbuVehiclePoDetail source);

    List<ExportCbuVehiclePoDetailDo> dataObjectsToDomains(List<ExportCbuVehiclePoDetail> sources);

}
