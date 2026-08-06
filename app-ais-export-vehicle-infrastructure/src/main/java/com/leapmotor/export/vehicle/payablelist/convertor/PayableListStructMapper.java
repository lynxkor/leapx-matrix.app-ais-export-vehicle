package com.leapmotor.export.vehicle.payablelist.convertor;

import com.leapmotor.export.vehicle.payablelist.entity.PayableList;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应付清单领域转换工具
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Mapper
public interface PayableListStructMapper {

    PayableListStructMapper INSTANCE = Mappers.getMapper(PayableListStructMapper.class);

    /**
     * 领域对象转实体对象
     */
    PayableList domainToDataObject(PayableListDo source);

    /**
     * 领域对象列表转实体对象列表
     */
    List<PayableList> domainToDataObjects(List<PayableListDo> sources);

    /**
     * 实体对象转领域对象
     */
    PayableListDo dataObjectToDomain(PayableList source);

    /**
     * 实体对象列表转领域对象列表
     */
    List<PayableListDo> dataObjectsToDomains(List<PayableList> sources);
}