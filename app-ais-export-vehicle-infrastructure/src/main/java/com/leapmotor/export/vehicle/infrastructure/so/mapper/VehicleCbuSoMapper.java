package com.leapmotor.export.vehicle.infrastructure.so.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseMapper;

import java.util.List;

/**
 * <p>
 * CBU整车销售订单主表 Mapper 接口
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Mapper
public interface VehicleCbuSoMapper extends LpBaseMapper<VehicleCbuSo> {

    /**
     * 分页查询 SO 主表与车辆明细聚合数据
     *
     * @param page 分页参数
     * @param qo   查询条件
     * @return 分页结果
     */
    Page<VehicleCbuSoDo> pageSoList(Page<VehicleCbuSoDo> page, @Param("qo") VehicleCbuSoQo qo);

    /**
     * 列表查询 SO 主表与车辆明细聚合数据
     *
     * @param qo 查询条件
     * @return 列表结果
     */
    List<VehicleCbuSoDo> listSoList(@Param("qo") VehicleCbuSoQo qo);

    /**
     * 根据主键查询 SO 聚合详情（含车辆明细）
     *
     * @param id SO 主键
     * @return SO 聚合详情
     */
    VehicleCbuSoDo getSoDetailById(@Param("id") String id);

}
