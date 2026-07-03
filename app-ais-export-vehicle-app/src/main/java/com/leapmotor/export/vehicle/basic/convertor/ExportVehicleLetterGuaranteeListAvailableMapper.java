package com.leapmotor.export.vehicle.basic.convertor;

import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeListAvailableResp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Do → ListAvailableResp(只暴露资金校验所需的字段)
 *
 * @author wangkai2527148
 */
@Mapper
public interface ExportVehicleLetterGuaranteeListAvailableMapper {
    ExportVehicleLetterGuaranteeListAvailableMapper INSTANCE =
        Mappers.getMapper(ExportVehicleLetterGuaranteeListAvailableMapper.class);

    ExportVehicleLetterGuaranteeListAvailableResp doToResp(ExportVehicleLetterGuaranteeDo source);
}
