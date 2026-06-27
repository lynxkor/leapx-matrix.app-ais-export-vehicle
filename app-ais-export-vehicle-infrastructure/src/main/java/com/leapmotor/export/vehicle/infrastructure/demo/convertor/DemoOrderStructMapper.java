package com.leapmotor.export.vehicle.infrastructure.demo.convertor;

import com.leapmotor.export.vehicle.demo.model.DemoOrderDo;
import com.leapmotor.export.vehicle.infrastructure.demo.entity.DemoOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 整车订单示例 领域对象 ↔ 实体 转换工具
 */
@Mapper
public interface DemoOrderStructMapper {
    DemoOrderStructMapper INSTANCE = Mappers.getMapper(DemoOrderStructMapper.class);

    DemoOrder domainToDataObject(DemoOrderDo source);

    List<DemoOrder> domainToDataObjects(List<DemoOrderDo> sources);

    DemoOrderDo dataObjectToDomain(DemoOrder source);

    List<DemoOrderDo> dataObjectsToDomains(List<DemoOrder> sources);
}
