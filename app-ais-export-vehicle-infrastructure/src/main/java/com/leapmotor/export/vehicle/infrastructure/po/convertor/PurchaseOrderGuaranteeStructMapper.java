package com.leapmotor.export.vehicle.infrastructure.po.convertor;

import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;

import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderGuarantee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 *
 * PurchaseOrderGuarantee领域转换工具
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderGuaranteeStructMapper {
    PurchaseOrderGuaranteeStructMapper INSTANCE = Mappers.getMapper(PurchaseOrderGuaranteeStructMapper.class);

    PurchaseOrderGuarantee domainToDataObject(PurchaseOrderGuaranteeDo source);

    List<PurchaseOrderGuarantee> domainToDataObjects(List<PurchaseOrderGuaranteeDo> sources);

    PurchaseOrderGuaranteeDo dataObjectToDomain(PurchaseOrderGuarantee source);

    List<PurchaseOrderGuaranteeDo> dataObjectsToDomains(List<PurchaseOrderGuarantee> sources);

}
