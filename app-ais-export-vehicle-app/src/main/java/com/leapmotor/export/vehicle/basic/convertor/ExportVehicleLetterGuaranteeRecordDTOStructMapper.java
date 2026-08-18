package com.leapmotor.export.vehicle.basic.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.request.ExportVehicleLetterGuaranteeRecordRequest;
import com.leapmotor.export.vehicle.basic.response.ExportVehicleLetterGuaranteeRecordResponse;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportVehicleLetterGuaranteeRecordDTO转换工具
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Mapper
public interface ExportVehicleLetterGuaranteeRecordDTOStructMapper {
    ExportVehicleLetterGuaranteeRecordDTOStructMapper INSTANCE = Mappers.getMapper(ExportVehicleLetterGuaranteeRecordDTOStructMapper.class);

    ExportVehicleLetterGuaranteeRecordDo addCmdToDomain(ExportVehicleLetterGuaranteeRecordAddOrUpdateReq source);

    ExportVehicleLetterGuaranteeRecordResp domainToResp(ExportVehicleLetterGuaranteeRecordDo source);

    List<ExportVehicleLetterGuaranteeRecordResp> domainsToResps(List<ExportVehicleLetterGuaranteeRecordDo> sources);

    LpOptionalResult domainsToOptional(ExportVehicleLetterGuaranteeRecordResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportVehicleLetterGuaranteeRecordResp> sources);

    PageResult<ExportVehicleLetterGuaranteeRecordResp> domainsToRespPage(PageResult<ExportVehicleLetterGuaranteeRecordDo> sources);

    ExportVehicleLetterGuaranteeRecordQo paramToQuery(ExportVehicleLetterGuaranteeRecordParam source);

    ExportVehicleLetterGuaranteeRecordParam requestToParam(ExportVehicleLetterGuaranteeRecordRequest source);

    ExportVehicleLetterGuaranteeRecordAddOrUpdateReq dtoToReq(ExportVehicleLetterGuaranteeRecordRequest source);

    ExportVehicleLetterGuaranteeRecordResponse domainToResponse(ExportVehicleLetterGuaranteeRecordResp source);

    List<ExportVehicleLetterGuaranteeRecordResponse> domainsToResponses(List<ExportVehicleLetterGuaranteeRecordResp> sources);

    PageResult<ExportVehicleLetterGuaranteeRecordResponse> domainsToResponsePage(PageResult<ExportVehicleLetterGuaranteeRecordResp> sources);
}
