package com.leapmotor.export.vehicle.infrastructure.po.dao;

import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPo;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseDao;

/**
 * DB数据交互抽象层
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoDao extends LpBaseDao<ExportVehicleCbuPo, ExportVehicleCbuPoQo>  {

}
