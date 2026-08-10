package com.leapmotor.export.vehicle.infrastructure.payablelist.dao;

import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableList;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseDao;

/**
 * 应付清单抬头 DB数据交互抽象层
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public interface PayableListDao extends LpBaseDao<PayableList, PayableListQo> {

}
