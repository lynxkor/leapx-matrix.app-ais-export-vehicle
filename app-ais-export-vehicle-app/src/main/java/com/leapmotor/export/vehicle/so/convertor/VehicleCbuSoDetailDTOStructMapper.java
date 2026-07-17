package com.leapmotor.export.vehicle.so.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.request.VehicleCbuSoDetailRequest;
import com.leapmotor.export.vehicle.so.response.VehicleCbuSoDetailResponse;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * VehicleCbuSoDetailDTO转换工具
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Mapper
public interface VehicleCbuSoDetailDTOStructMapper {

    VehicleCbuSoDetailDTOStructMapper INSTANCE = Mappers.getMapper(VehicleCbuSoDetailDTOStructMapper.class);

    VehicleCbuSoDetailDo addCmdToDomain(VehicleCbuSoDetailAddOrUpdateReq source);

    VehicleCbuSoDetailResp domainToResp(VehicleCbuSoDetailDo source);

    List<VehicleCbuSoDetailResp> domainsToResps(List<VehicleCbuSoDetailDo> sources);

    /**
     * 从 SO 主表领域对象（已 JOIN 车辆明细字段）转换车辆明细响应
     */
    VehicleCbuSoDetailResp domainToResp(VehicleCbuSoDo source);

    LpOptionalResult domainsToOptional(VehicleCbuSoDetailResp sources);

    List<LpOptionalResult> domainsToOptionals(List<VehicleCbuSoDetailResp> sources);

    PageResult<VehicleCbuSoDetailResp> domainsToRespPage(PageResult<VehicleCbuSoDetailDo> sources);

    VehicleCbuSoDetailQo paramToQuery(VehicleCbuSoDetailParam source);

    VehicleCbuSoDetailParam requestToParam(VehicleCbuSoDetailRequest source);

    VehicleCbuSoDetailAddOrUpdateReq dtoToReq(VehicleCbuSoDetailRequest source);

    VehicleCbuSoDetailResponse domainToResponse(VehicleCbuSoDetailResp source);

    List<VehicleCbuSoDetailResponse> domainsToResponses(List<VehicleCbuSoDetailResp> sources);

    PageResult<VehicleCbuSoDetailResponse> domainsToResponsePage(PageResult<VehicleCbuSoDetailResp> sources);
}
