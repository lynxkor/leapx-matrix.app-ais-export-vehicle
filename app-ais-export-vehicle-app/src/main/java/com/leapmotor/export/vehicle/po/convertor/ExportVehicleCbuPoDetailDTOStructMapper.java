package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoDetailRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoDetailResponse;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleCbuPoDetailDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportVehicleCbuPoDetailDTOStructMapper {
    ExportVehicleCbuPoDetailDTOStructMapper INSTANCE = Mappers.getMapper(ExportVehicleCbuPoDetailDTOStructMapper.class);

    ExportVehicleCbuPoDetailDo addCmdToDomain(ExportVehicleCbuPoDetailAddOrUpdateReq source);

    ExportVehicleCbuPoDetailResp domainToResp(ExportVehicleCbuPoDetailDo source);

    List<ExportVehicleCbuPoDetailResp> domainsToResps(List<ExportVehicleCbuPoDetailDo> sources);

    LpOptionalResult domainsToOptional(ExportVehicleCbuPoDetailResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportVehicleCbuPoDetailResp> sources);

    PageResult<ExportVehicleCbuPoDetailResp> domainsToRespPage(PageResult<ExportVehicleCbuPoDetailDo> sources);

    ExportVehicleCbuPoDetailQo paramToQuery(ExportVehicleCbuPoDetailParam source);

    ExportVehicleCbuPoDetailParam requestToParam(ExportVehicleCbuPoDetailRequest source);

    ExportVehicleCbuPoDetailAddOrUpdateReq dtoToReq(ExportVehicleCbuPoDetailRequest source);

    ExportVehicleCbuPoDetailResponse domainToResponse(ExportVehicleCbuPoDetailResp source);

    List<ExportVehicleCbuPoDetailResponse> domainsToResponses(List<ExportVehicleCbuPoDetailResp> sources);

    PageResult<ExportVehicleCbuPoDetailResponse> domainsToResponsePage(PageResult<ExportVehicleCbuPoDetailResp> sources);
}
