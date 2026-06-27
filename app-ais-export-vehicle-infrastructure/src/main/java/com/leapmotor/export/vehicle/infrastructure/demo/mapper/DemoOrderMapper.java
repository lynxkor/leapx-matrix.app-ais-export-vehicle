package com.leapmotor.export.vehicle.infrastructure.demo.mapper;

import com.leapmotor.export.vehicle.infrastructure.demo.entity.DemoOrder;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 整车订单示例 Mapper 接口
 */
@Mapper
public interface DemoOrderMapper extends LpBaseMapper<DemoOrder> {

    /**
     * 示例：按订单号精确查询（对应 mapper/demo/DemoOrderMapper.xml）
     */
    List<DemoOrder> selectByOrderNo(@Param("orderNo") String orderNo);
}
