package com.leapmotor.export.vehicle.so.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoResponse;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * VehicleCbuSoDTO转换工具
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Mapper
public interface VehicleCbuSoDTOStructMapper {

    VehicleCbuSoDTOStructMapper INSTANCE = Mappers.getMapper(VehicleCbuSoDTOStructMapper.class);

    VehicleCbuSoDo addCmdToDomain(VehicleCbuSoAddOrUpdateReq source);

    VehicleCbuSoResp domainToResp(VehicleCbuSoDo source);

    List<VehicleCbuSoResp> domainsToResps(List<VehicleCbuSoDo> sources);

    LpOptionalResult domainsToOptional(VehicleCbuSoResp sources);

    List<LpOptionalResult> domainsToOptionals(List<VehicleCbuSoResp> sources);

    PageResult<VehicleCbuSoResp> domainsToRespPage(PageResult<VehicleCbuSoDo> sources);

    VehicleCbuSoQo paramToQuery(VehicleCbuSoParam source);

    VehicleCbuSoParam requestToParam(VehicleCbuSoRequest source);

    VehicleCbuSoAddOrUpdateReq dtoToReq(VehicleCbuSoRequest source);

    VehicleCbuSoResponse domainToResponse(VehicleCbuSoResp source);

    List<VehicleCbuSoResponse> domainsToResponses(List<VehicleCbuSoResp> sources);

    PageResult<VehicleCbuSoResponse> domainsToResponsePage(PageResult<VehicleCbuSoResp> sources);
}
