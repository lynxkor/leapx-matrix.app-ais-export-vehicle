package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderGuaranteeResp;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderGuaranteeRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderGuaranteeResponse;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderGuaranteeQo;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderGuaranteeResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * PurchaseOrderGuaranteeDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderGuaranteeDTOStructMapper {
    PurchaseOrderGuaranteeDTOStructMapper INSTANCE = Mappers.getMapper(PurchaseOrderGuaranteeDTOStructMapper.class);

    PurchaseOrderGuaranteeDo addCmdToDomain(PurchaseOrderGuaranteeAddOrUpdateReq source);

    PurchaseOrderGuaranteeResp domainToResp(PurchaseOrderGuaranteeDo source);

    List<PurchaseOrderGuaranteeResp> domainsToResps(List<PurchaseOrderGuaranteeDo> sources);

    LpOptionalResult domainsToOptional(PurchaseOrderGuaranteeResp sources);

    List<LpOptionalResult> domainsToOptionals(List<PurchaseOrderGuaranteeResp> sources);

    PageResult<PurchaseOrderGuaranteeResp> domainsToRespPage(PageResult<PurchaseOrderGuaranteeDo> sources);

    PurchaseOrderGuaranteeQo paramToQuery(PurchaseOrderGuaranteeParam source);

    PurchaseOrderGuaranteeParam requestToParam(PurchaseOrderGuaranteeRequest source);

    PurchaseOrderGuaranteeAddOrUpdateReq dtoToReq(PurchaseOrderGuaranteeRequest source);

    PurchaseOrderGuaranteeResponse domainToResponse(PurchaseOrderGuaranteeResp source);

    List<PurchaseOrderGuaranteeResponse> domainsToResponses(List<PurchaseOrderGuaranteeResp> sources);

    PageResult<PurchaseOrderGuaranteeResponse> domainsToResponsePage(PageResult<PurchaseOrderGuaranteeResp> sources);
}
