package com.leapmotor.export.vehicle.po.convertor;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.response.LpOptionalResult;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;
import com.leapmotor.export.vehicle.po.request.PurchaseOrderRequest;
import com.leapmotor.export.vehicle.po.response.PurchaseOrderResponse;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderQo;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * PurchaseOrderDTO转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderDTOStructMapper {
    PurchaseOrderDTOStructMapper INSTANCE = Mappers.getMapper(PurchaseOrderDTOStructMapper.class);

    PurchaseOrderDo addCmdToDomain(PurchaseOrderAddOrUpdateReq source);

    PurchaseOrderResp domainToResp(PurchaseOrderDo source);

    List<PurchaseOrderResp> domainsToResps(List<PurchaseOrderDo> sources);

    LpOptionalResult domainsToOptional(PurchaseOrderResp sources);

    List<LpOptionalResult> domainsToOptionals(List<PurchaseOrderResp> sources);

    PageResult<PurchaseOrderResp> domainsToRespPage(PageResult<PurchaseOrderDo> sources);

    PurchaseOrderQo paramToQuery(PurchaseOrderParam source);

    PurchaseOrderParam requestToParam(PurchaseOrderRequest source);

    PurchaseOrderAddOrUpdateReq dtoToReq(PurchaseOrderRequest source);

    PurchaseOrderResponse domainToResponse(PurchaseOrderResp source);

    List<PurchaseOrderResponse> domainsToResponses(List<PurchaseOrderResp> sources);

    PageResult<PurchaseOrderResponse> domainsToResponsePage(PageResult<PurchaseOrderResp> sources);
}
