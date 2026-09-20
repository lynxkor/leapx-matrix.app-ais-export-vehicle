package com.leapmotor.export.vehicle.infrastructure.payablelist.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableListDetail;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 应付清单明细表 Mapper 接口
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@Mapper
public interface PayableListDetailMapper extends LpBaseMapper<PayableListDetail> {

    /**
     * 分页查询明细
     *
     * @param page 分页参数
     * @param qo   查询条件
     * @return 分页结果
     */
    Page<PayableListDetailDo> pageDetailWithListNo(Page<PayableListDetailDo> page, @Param("qo") PayableListDetailQo qo);
}
