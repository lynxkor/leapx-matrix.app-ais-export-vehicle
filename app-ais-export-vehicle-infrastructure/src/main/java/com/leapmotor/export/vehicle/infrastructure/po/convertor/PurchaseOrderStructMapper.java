package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * PurchaseOrder领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderStructMapper {
    PurchaseOrderStructMapper INSTANCE = Mappers.getMapper(PurchaseOrderStructMapper.class);

    PurchaseOrder domainToDataObject(PurchaseOrderDo source);

    List<PurchaseOrder> domainToDataObjects(List<PurchaseOrderDo> sources);

    PurchaseOrderDo dataObjectToDomain(PurchaseOrder source);

    List<PurchaseOrderDo> dataObjectsToDomains(List<PurchaseOrder> sources);

}
