package com.leapmotor.export.vehicle.infrastructure.po.mapper;

import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseMapper;

/**
 * <p>
 * PO订单明细表 Mapper 接口
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderDetailMapper extends LpBaseMapper<PurchaseOrderDetail> {

}
