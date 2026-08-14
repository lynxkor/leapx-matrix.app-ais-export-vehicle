package com.leapmotor.export.vehicle.infrastructure.payablelist.mapper;

import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableList;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应付清单抬头表 Mapper 接口
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Mapper
public interface PayableListMapper extends LpBaseMapper<PayableList> {

}
