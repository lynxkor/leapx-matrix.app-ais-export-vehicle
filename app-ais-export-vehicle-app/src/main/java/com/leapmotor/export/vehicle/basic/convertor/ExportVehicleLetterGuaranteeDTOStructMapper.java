package com.leapmotor.export.vehicle.basic.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRequest;
import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeResponse;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleLetterGuaranteeDTO转换工具
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Mapper
public interface ExportVehicleLetterGuaranteeDTOStructMapper {
    ExportVehicleLetterGuaranteeDTOStructMapper INSTANCE = Mappers.getMapper(ExportVehicleLetterGuaranteeDTOStructMapper.class);

    ExportVehicleLetterGuaranteeDo addCmdToDomain(ExportVehicleLetterGuaranteeAddOrUpdateReq source);

    ExportVehicleLetterGuaranteeResp domainToResp(ExportVehicleLetterGuaranteeDo source);

    List<ExportVehicleLetterGuaranteeResp> domainsToResps(List<ExportVehicleLetterGuaranteeDo> sources);

    LpOptionalResult domainsToOptional(ExportVehicleLetterGuaranteeResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportVehicleLetterGuaranteeResp> sources);

    PageResult<ExportVehicleLetterGuaranteeResp> domainsToRespPage(PageResult<ExportVehicleLetterGuaranteeDo> sources);

    ExportVehicleLetterGuaranteeQo paramToQuery(ExportVehicleLetterGuaranteeParam source);

    ExportVehicleLetterGuaranteeParam requestToParam(ExportVehicleLetterGuaranteeRequest source);

    ExportVehicleLetterGuaranteeAddOrUpdateReq dtoToReq(ExportVehicleLetterGuaranteeRequest source);

    ExportVehicleLetterGuaranteeResponse domainToResponse(ExportVehicleLetterGuaranteeResp source);

    List<ExportVehicleLetterGuaranteeResponse> domainsToResponses(List<ExportVehicleLetterGuaranteeResp> sources);

    PageResult<ExportVehicleLetterGuaranteeResponse> domainsToResponsePage(PageResult<ExportVehicleLetterGuaranteeResp> sources);
}
