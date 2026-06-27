package com.leapmotor.export.vehicle.infrastructure.demo.dao;

import com.leapmotor.export.vehicle.demo.query.DemoOrderQo;
import com.leapmotor.export.vehicle.infrastructure.demo.entity.DemoOrder;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseDao;

/**
 * 整车订单示例 DB数据交互抽象层
 */
public interface DemoOrderDao extends LpBaseDao<DemoOrder, DemoOrderQo> {

}
