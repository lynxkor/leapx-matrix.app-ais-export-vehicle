package com.leapmotor.export.vehicle.payablelist.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leapmotor.export.vehicle.payablelist.entity.PayableList;
import com.leapmotor.export.vehicle.payablelist.mapper.PayableListMapper;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 应付清单抬头 DB数据交互实现
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@AllArgsConstructor
@Service
public class PayableListDaoImpl extends ServiceImpl<PayableListMapper, PayableList> implements PayableListDao {

    @Override
    public QueryWrapper<PayableList> buildQueryWrapper(PayableListQo query) {
        QueryWrapper<PayableList> queryWrapper = PayableListDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
                // TODO: 根据实际需求添加查询条件
        ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }
}
