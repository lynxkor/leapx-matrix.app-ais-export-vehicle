package com.leapmotor.export.vehicle.infrastructure.payablelist.dao;

import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableListDetail;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseDao;

/**
 * 应付清单明细 DB数据交互抽象层
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
public interface PayableListDetailDao extends LpBaseDao<PayableListDetail, PayableListDetailQo> {

}
