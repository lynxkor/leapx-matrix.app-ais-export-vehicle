package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderDetailResp;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderDetailRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderDetailResponse;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderDetailQo;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderDetailResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * PurchaseOrderDetailDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderDetailDTOStructMapper {
    PurchaseOrderDetailDTOStructMapper INSTANCE = Mappers.getMapper(PurchaseOrderDetailDTOStructMapper.class);

    PurchaseOrderDetailDo addCmdToDomain(PurchaseOrderDetailAddOrUpdateReq source);

    PurchaseOrderDetailResp domainToResp(PurchaseOrderDetailDo source);

    List<PurchaseOrderDetailResp> domainsToResps(List<PurchaseOrderDetailDo> sources);

    LpOptionalResult domainsToOptional(PurchaseOrderDetailResp sources);

    List<LpOptionalResult> domainsToOptionals(List<PurchaseOrderDetailResp> sources);

    PageResult<PurchaseOrderDetailResp> domainsToRespPage(PageResult<PurchaseOrderDetailDo> sources);

    PurchaseOrderDetailQo paramToQuery(PurchaseOrderDetailParam source);

    PurchaseOrderDetailParam requestToParam(PurchaseOrderDetailRequest source);

    PurchaseOrderDetailAddOrUpdateReq dtoToReq(PurchaseOrderDetailRequest source);

    PurchaseOrderDetailResponse domainToResponse(PurchaseOrderDetailResp source);

    List<PurchaseOrderDetailResponse> domainsToResponses(List<PurchaseOrderDetailResp> sources);

    PageResult<PurchaseOrderDetailResponse> domainsToResponsePage(PageResult<PurchaseOrderDetailResp> sources);
}
