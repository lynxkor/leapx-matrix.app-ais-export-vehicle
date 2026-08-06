package com.leapmotor.export.vehicle.payablelist.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leapmotor.export.vehicle.payablelist.dao.PayableListDetailDao;
import com.leapmotor.export.vehicle.payablelist.entity.PayableListDetail;
import com.leapmotor.export.vehicle.payablelist.mapper.PayableListDetailMapper;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 应付清单明细 DB数据交互实现
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@AllArgsConstructor
@Service
public class PayableListDetailDaoImpl extends ServiceImpl<PayableListDetailMapper, PayableListDetail> implements PayableListDetailDao {

    @Override
    public QueryWrapper<PayableListDetail> buildQueryWrapper(PayableListDetailQo query) {
        QueryWrapper<PayableListDetail> queryWrapper = PayableListDetailDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(query.getPayableListId()), PayableListDetail::getPayableListId, query.getPayableListId())
                .eq(StringUtils.isNotBlank(query.getSoNo()), PayableListDetail::getSoNo, query.getSoNo())
                .eq(StringUtils.isNotBlank(query.getPoNo()), PayableListDetail::getPoNo, query.getPoNo())
                .eq(StringUtils.isNotBlank(query.getVin()), PayableListDetail::getVin, query.getVin())
                .eq(Objects.nonNull(query.getIsReceived()), PayableListDetail::getIsReceived, query.getIsReceived())
                .like(StringUtils.isNotBlank(query.getVinLike()), PayableListDetail::getVin, query.getVinLike())
                .like(StringUtils.isNotBlank(query.getSoNoLike()), PayableListDetail::getSoNo, query.getSoNoLike())
                .ge(Objects.nonNull(query.getQcPassTimeStart()), PayableListDetail::getQcPassTime, query.getQcPassTimeStart())
                .le(Objects.nonNull(query.getQcPassTimeEnd()), PayableListDetail::getQcPassTime, query.getQcPassTimeEnd())
        ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }
}
