package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoResponse;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleCbuPoDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportVehicleCbuPoDTOStructMapper {
    ExportVehicleCbuPoDTOStructMapper INSTANCE = Mappers.getMapper(ExportVehicleCbuPoDTOStructMapper.class);

    ExportVehicleCbuPoDo addCmdToDomain(ExportVehicleCbuPoAddOrUpdateReq source);

    ExportVehicleCbuPoResp domainToResp(ExportVehicleCbuPoDo source);

    List<ExportVehicleCbuPoResp> domainsToResps(List<ExportVehicleCbuPoDo> sources);

    LpOptionalResult domainsToOptional(ExportVehicleCbuPoResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportVehicleCbuPoResp> sources);

    PageResult<ExportVehicleCbuPoResp> domainsToRespPage(PageResult<ExportVehicleCbuPoDo> sources);

    ExportVehicleCbuPoQo paramToQuery(ExportVehicleCbuPoParam source);

    ExportVehicleCbuPoParam requestToParam(ExportVehicleCbuPoRequest source);

    ExportVehicleCbuPoAddOrUpdateReq dtoToReq(ExportVehicleCbuPoRequest source);

    ExportVehicleCbuPoResponse domainToResponse(ExportVehicleCbuPoResp source);

    List<ExportVehicleCbuPoResponse> domainsToResponses(List<ExportVehicleCbuPoResp> sources);

    PageResult<ExportVehicleCbuPoResponse> domainsToResponsePage(PageResult<ExportVehicleCbuPoResp> sources);
}
