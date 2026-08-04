package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.request.ExportVehicleCbuPoGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.ExportVehicleCbuPoGuaranteeResponse;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleCbuPoGuaranteeDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportVehicleCbuPoGuaranteeDTOStructMapper {
    ExportVehicleCbuPoGuaranteeDTOStructMapper INSTANCE = Mappers.getMapper(ExportVehicleCbuPoGuaranteeDTOStructMapper.class);

    ExportVehicleCbuPoGuaranteeDo addCmdToDomain(ExportVehicleCbuPoGuaranteeAddOrUpdateReq source);

    ExportVehicleCbuPoGuaranteeResp domainToResp(ExportVehicleCbuPoGuaranteeDo source);

    List<ExportVehicleCbuPoGuaranteeResp> domainsToResps(List<ExportVehicleCbuPoGuaranteeDo> sources);

    LpOptionalResult domainsToOptional(ExportVehicleCbuPoGuaranteeResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportVehicleCbuPoGuaranteeResp> sources);

    PageResult<ExportVehicleCbuPoGuaranteeResp> domainsToRespPage(PageResult<ExportVehicleCbuPoGuaranteeDo> sources);

    ExportVehicleCbuPoGuaranteeQo paramToQuery(ExportVehicleCbuPoGuaranteeParam source);

    ExportVehicleCbuPoGuaranteeParam requestToParam(ExportVehicleCbuPoGuaranteeRequest source);

    ExportVehicleCbuPoGuaranteeAddOrUpdateReq dtoToReq(ExportVehicleCbuPoGuaranteeRequest source);

    ExportVehicleCbuPoGuaranteeResponse domainToResponse(ExportVehicleCbuPoGuaranteeResp source);

    List<ExportVehicleCbuPoGuaranteeResponse> domainsToResponses(List<ExportVehicleCbuPoGuaranteeResp> sources);

    PageResult<ExportVehicleCbuPoGuaranteeResponse> domainsToResponsePage(PageResult<ExportVehicleCbuPoGuaranteeResp> sources);
}
