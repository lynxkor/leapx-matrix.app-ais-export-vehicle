package com.leapmotor.export.vehicle.infrastructure.po.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoDetail;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
public interface ExportVehicleCbuPoDetailMapper extends LpBaseMapper<ExportVehicleCbuPoDetail> {

    /**
     * 明细分页查询(LEFT JOIN 主表 export_vehicle_cbu_po):
     * 一条 SQL 同时取出明细列 + 主表的客户编码/客户名称/订单状态,并支持按主表字段(订单状态/客户编码)过滤,
     * 替代 buildQueryWrapper 里 EXISTS 跨表的字符串拼接写法.
     */
    IPage<ExportVehicleCbuPoDetail> pageDetailWithMain(IPage<ExportVehicleCbuPoDetail> page,
                                                       @Param("query") ExportVehicleCbuPoDetailQo query);

}
