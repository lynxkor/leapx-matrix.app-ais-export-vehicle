package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoDetailResp;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoDetailRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoDetailResponse;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoDetailQo;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoDetailResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportCbuVehiclePoDetailDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportCbuVehiclePoDetailDTOStructMapper {
    ExportCbuVehiclePoDetailDTOStructMapper INSTANCE = Mappers.getMapper(ExportCbuVehiclePoDetailDTOStructMapper.class);

    ExportCbuVehiclePoDetailDo addCmdToDomain(ExportCbuVehiclePoDetailAddOrUpdateReq source);

    ExportCbuVehiclePoDetailResp domainToResp(ExportCbuVehiclePoDetailDo source);

    List<ExportCbuVehiclePoDetailResp> domainsToResps(List<ExportCbuVehiclePoDetailDo> sources);

    LpOptionalResult domainsToOptional(ExportCbuVehiclePoDetailResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportCbuVehiclePoDetailResp> sources);

    PageResult<ExportCbuVehiclePoDetailResp> domainsToRespPage(PageResult<ExportCbuVehiclePoDetailDo> sources);

    ExportCbuVehiclePoDetailQo paramToQuery(ExportCbuVehiclePoDetailParam source);

    ExportCbuVehiclePoDetailParam requestToParam(ExportCbuVehiclePoDetailRequest source);

    ExportCbuVehiclePoDetailAddOrUpdateReq dtoToReq(ExportCbuVehiclePoDetailRequest source);

    ExportCbuVehiclePoDetailResponse domainToResponse(ExportCbuVehiclePoDetailResp source);

    List<ExportCbuVehiclePoDetailResponse> domainsToResponses(List<ExportCbuVehiclePoDetailResp> sources);

    PageResult<ExportCbuVehiclePoDetailResponse> domainsToResponsePage(PageResult<ExportCbuVehiclePoDetailResp> sources);
}
