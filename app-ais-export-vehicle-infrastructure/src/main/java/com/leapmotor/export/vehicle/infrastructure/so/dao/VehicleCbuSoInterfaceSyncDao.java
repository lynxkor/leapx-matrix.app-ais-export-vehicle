package com.leapmotor.export.vehicle.infrastructure.so.dao;

import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoInterfaceSync;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseDao;

/**
 * DB数据交互抽象层
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface VehicleCbuSoInterfaceSyncDao extends LpBaseDao<VehicleCbuSoInterfaceSync, VehicleCbuSoInterfaceSyncQo> {

}
