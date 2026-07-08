package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * PurchaseOrderDetail领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderDetailStructMapper {
    PurchaseOrderDetailStructMapper INSTANCE = Mappers.getMapper(PurchaseOrderDetailStructMapper.class);

    PurchaseOrderDetail domainToDataObject(PurchaseOrderDetailDo source);

    List<PurchaseOrderDetail> domainToDataObjects(List<PurchaseOrderDetailDo> sources);

    PurchaseOrderDetailDo dataObjectToDomain(PurchaseOrderDetail source);

    List<PurchaseOrderDetailDo> dataObjectsToDomains(List<PurchaseOrderDetail> sources);

}
