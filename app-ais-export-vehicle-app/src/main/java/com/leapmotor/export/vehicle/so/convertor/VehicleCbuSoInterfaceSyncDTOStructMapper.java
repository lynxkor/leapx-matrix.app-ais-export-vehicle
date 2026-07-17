package com.leapmotor.export.vehicle.so.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoInterfaceSyncRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoInterfaceSyncResponse;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * VehicleCbuSoInterfaceSyncDTO转换工具
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Mapper
public interface VehicleCbuSoInterfaceSyncDTOStructMapper {

    VehicleCbuSoInterfaceSyncDTOStructMapper INSTANCE = Mappers.getMapper(VehicleCbuSoInterfaceSyncDTOStructMapper.class);

    VehicleCbuSoInterfaceSyncDo addCmdToDomain(VehicleCbuSoInterfaceSyncAddOrUpdateReq source);

    VehicleCbuSoInterfaceSyncResp domainToResp(VehicleCbuSoInterfaceSyncDo source);

    List<VehicleCbuSoInterfaceSyncResp> domainsToResps(List<VehicleCbuSoInterfaceSyncDo> sources);

    LpOptionalResult domainsToOptional(VehicleCbuSoInterfaceSyncResp sources);

    List<LpOptionalResult> domainsToOptionals(List<VehicleCbuSoInterfaceSyncResp> sources);

    PageResult<VehicleCbuSoInterfaceSyncResp> domainsToRespPage(PageResult<VehicleCbuSoInterfaceSyncDo> sources);

    VehicleCbuSoInterfaceSyncQo paramToQuery(VehicleCbuSoInterfaceSyncParam source);

    VehicleCbuSoInterfaceSyncParam requestToParam(VehicleCbuSoInterfaceSyncRequest source);

    VehicleCbuSoInterfaceSyncAddOrUpdateReq dtoToReq(VehicleCbuSoInterfaceSyncRequest source);

    VehicleCbuSoInterfaceSyncResponse domainToResponse(VehicleCbuSoInterfaceSyncResp source);

    List<VehicleCbuSoInterfaceSyncResponse> domainsToResponses(List<VehicleCbuSoInterfaceSyncResp> sources);

    PageResult<VehicleCbuSoInterfaceSyncResponse> domainsToResponsePage(PageResult<VehicleCbuSoInterfaceSyncResp> sources);
}
