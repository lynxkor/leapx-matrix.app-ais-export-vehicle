package com.leapmotor.export.vehicle.infrastructure.po.mapper;

import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderGuarantee;
import org.apache.ibatis.annotations.Mapper;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseMapper;

/**
 * <p>
 * PO-保函关联表(一个PO多保函) Mapper 接口
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Mapper
public interface PurchaseOrderGuaranteeMapper extends LpBaseMapper<PurchaseOrderGuarantee> {

}
