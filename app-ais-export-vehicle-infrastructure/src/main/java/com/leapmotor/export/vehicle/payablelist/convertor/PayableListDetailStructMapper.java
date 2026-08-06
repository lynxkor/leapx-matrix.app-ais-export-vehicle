package com.leapmotor.export.vehicle.payablelist.convertor;

import com.leapmotor.export.vehicle.payablelist.entity.PayableListDetail;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 应付清单明细领域转换工具
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Mapper
public interface PayableListDetailStructMapper {

    PayableListDetailStructMapper INSTANCE = Mappers.getMapper(PayableListDetailStructMapper.class);

    /**
     * 领域对象转实体对象
     */
    PayableListDetail domainToDataObject(PayableListDetailDo source);

    /**
     * 领域对象列表转实体对象列表
     */
    List<PayableListDetail> domainToDataObjects(List<PayableListDetailDo> sources);

    /**
     * 实体对象转领域对象
     */
    PayableListDetailDo dataObjectToDomain(PayableListDetail source);

    /**
     * 实体对象列表转领域对象列表
     */
    List<PayableListDetailDo> dataObjectsToDomains(List<PayableListDetail> sources);
}