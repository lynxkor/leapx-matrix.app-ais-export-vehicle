package com.leapmotor.export.vehicle.infrastructure.po.dao;

import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoDetailQo;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePoDetail;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseDao;

/**
 * DB数据交互抽象层
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportCbuVehiclePoDetailDao extends LpBaseDao<ExportCbuVehiclePoDetail, ExportCbuVehiclePoDetailQo>  {

}
