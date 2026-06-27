package com.leapmotor.export.vehicle.demo.convertor;

import com.leapmotor.export.vehicle.demo.model.DemoOrderDo;
import com.leapmotor.export.vehicle.demo.query.DemoOrderQo;
import com.leapmotor.export.vehicle.demo.req.DemoOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.demo.req.DemoOrderParam;
import com.leapmotor.export.vehicle.demo.resp.DemoOrderResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 整车订单示例 DTO 转换工具
 */
@Mapper
public interface DemoOrderDTOStructMapper {
    DemoOrderDTOStructMapper INSTANCE = Mappers.getMapper(DemoOrderDTOStructMapper.class);

    DemoOrderDo addCmdToDomain(DemoOrderAddOrUpdateReq source);

    DemoOrderResp domainToResp(DemoOrderDo source);

    List<DemoOrderResp> domainsToResps(List<DemoOrderDo> sources);

    PageResult<DemoOrderResp> domainsToRespPage(PageResult<DemoOrderDo> sources);

    DemoOrderQo paramToQuery(DemoOrderParam source);
}
