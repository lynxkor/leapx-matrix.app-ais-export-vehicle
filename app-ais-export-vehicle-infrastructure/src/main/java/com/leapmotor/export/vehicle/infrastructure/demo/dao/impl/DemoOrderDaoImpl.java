package com.leapmotor.export.vehicle.infrastructure.demo.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leapmotor.export.vehicle.demo.query.DemoOrderQo;
import com.leapmotor.export.vehicle.infrastructure.demo.dao.DemoOrderDao;
import com.leapmotor.export.vehicle.infrastructure.demo.entity.DemoOrder;
import com.leapmotor.export.vehicle.infrastructure.demo.mapper.DemoOrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 整车订单示例 DB数据交互实现
 */
@AllArgsConstructor
@Service
public class DemoOrderDaoImpl extends ServiceImpl<DemoOrderMapper, DemoOrder> implements DemoOrderDao {

    @Override
    public QueryWrapper<DemoOrder> buildQueryWrapper(DemoOrderQo query) {
        QueryWrapper<DemoOrder> queryWrapper = DemoOrderDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .like(StringUtils.isNotBlank(query.getOrderNo()), DemoOrder::getOrderNo, query.getOrderNo())
            .eq(StringUtils.isNotBlank(query.getOrderType()), DemoOrder::getOrderType, query.getOrderType())
            .like(StringUtils.isNotBlank(query.getRemark()), DemoOrder::getRemark, query.getRemark())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN);
        return queryWrapper;
    }

}
