package com.leapmotor.export.vehicle.infrastructure.po.dao;

import com.leapmotor.export.vehicle.po.query.PurchaseOrderGuaranteeQo;
import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderGuarantee;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseDao;

/**
 * DB数据交互抽象层
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface PurchaseOrderGuaranteeDao extends LpBaseDao<PurchaseOrderGuarantee, PurchaseOrderGuaranteeQo>  {

}
