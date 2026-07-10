package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoResp;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDo;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoResponse;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoQo;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportCbuVehiclePoDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportCbuVehiclePoDTOStructMapper {
    ExportCbuVehiclePoDTOStructMapper INSTANCE = Mappers.getMapper(ExportCbuVehiclePoDTOStructMapper.class);

    ExportCbuVehiclePoDo addCmdToDomain(ExportCbuVehiclePoAddOrUpdateReq source);

    ExportCbuVehiclePoResp domainToResp(ExportCbuVehiclePoDo source);

    List<ExportCbuVehiclePoResp> domainsToResps(List<ExportCbuVehiclePoDo> sources);

    LpOptionalResult domainsToOptional(ExportCbuVehiclePoResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportCbuVehiclePoResp> sources);

    PageResult<ExportCbuVehiclePoResp> domainsToRespPage(PageResult<ExportCbuVehiclePoDo> sources);

    ExportCbuVehiclePoQo paramToQuery(ExportCbuVehiclePoParam source);

    ExportCbuVehiclePoParam requestToParam(ExportCbuVehiclePoRequest source);

    ExportCbuVehiclePoAddOrUpdateReq dtoToReq(ExportCbuVehiclePoRequest source);

    ExportCbuVehiclePoResponse domainToResponse(ExportCbuVehiclePoResp source);

    List<ExportCbuVehiclePoResponse> domainsToResponses(List<ExportCbuVehiclePoResp> sources);

    PageResult<ExportCbuVehiclePoResponse> domainsToResponsePage(PageResult<ExportCbuVehiclePoResp> sources);
}
