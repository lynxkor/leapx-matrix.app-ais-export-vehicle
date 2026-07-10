package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoGuaranteeResp;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoGuaranteeDo;
import com.leapmotor.export.vehicle.po.request.ExportCbuVehiclePoGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.ExportCbuVehiclePoGuaranteeResponse;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoGuaranteeQo;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoGuaranteeResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * ExportCbuVehiclePoGuaranteeDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface ExportCbuVehiclePoGuaranteeDTOStructMapper {
    ExportCbuVehiclePoGuaranteeDTOStructMapper INSTANCE = Mappers.getMapper(ExportCbuVehiclePoGuaranteeDTOStructMapper.class);

    ExportCbuVehiclePoGuaranteeDo addCmdToDomain(ExportCbuVehiclePoGuaranteeAddOrUpdateReq source);

    ExportCbuVehiclePoGuaranteeResp domainToResp(ExportCbuVehiclePoGuaranteeDo source);

    List<ExportCbuVehiclePoGuaranteeResp> domainsToResps(List<ExportCbuVehiclePoGuaranteeDo> sources);

    LpOptionalResult domainsToOptional(ExportCbuVehiclePoGuaranteeResp sources);

    List<LpOptionalResult> domainsToOptionals(List<ExportCbuVehiclePoGuaranteeResp> sources);

    PageResult<ExportCbuVehiclePoGuaranteeResp> domainsToRespPage(PageResult<ExportCbuVehiclePoGuaranteeDo> sources);

    ExportCbuVehiclePoGuaranteeQo paramToQuery(ExportCbuVehiclePoGuaranteeParam source);

    ExportCbuVehiclePoGuaranteeParam requestToParam(ExportCbuVehiclePoGuaranteeRequest source);

    ExportCbuVehiclePoGuaranteeAddOrUpdateReq dtoToReq(ExportCbuVehiclePoGuaranteeRequest source);

    ExportCbuVehiclePoGuaranteeResponse domainToResponse(ExportCbuVehiclePoGuaranteeResp source);

    List<ExportCbuVehiclePoGuaranteeResponse> domainsToResponses(List<ExportCbuVehiclePoGuaranteeResp> sources);

    PageResult<ExportCbuVehiclePoGuaranteeResponse> domainsToResponsePage(PageResult<ExportCbuVehiclePoGuaranteeResp> sources);
}
