package com.leapmotor.export.vehicle.infrastructure.payablelist.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leapmotor.export.vehicle.infrastructure.payablelist.dao.PayableListDao;
import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableList;
import com.leapmotor.export.vehicle.infrastructure.payablelist.mapper.PayableListMapper;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
                .like(StringUtils.isNotBlank(query.getPayableListNo()), PayableList::getPayableListNo, query.getPayableListNo())
                .like(StringUtils.isNotBlank(query.getPayableListNoLike()), PayableList::getPayableListNo, query.getPayableListNoLike())
                .in(query.getPayableListNos() != null && !query.getPayableListNos().isEmpty(),
                        PayableList::getPayableListNo, query.getPayableListNos())
                .in(query.getBillStatusList() != null && !query.getBillStatusList().isEmpty(),
                        PayableList::getBillStatus, query.getBillStatusList())
                .eq(StringUtils.isNotBlank(query.getCustomerCode()), PayableList::getCustomerCode, query.getCustomerCode())
                .like(StringUtils.isNotBlank(query.getCustomerNameLike()), PayableList::getCustomerName, query.getCustomerNameLike())
                .eq(StringUtils.isNotBlank(query.getGenerateMonth()), PayableList::getGenerateMonth, query.getGenerateMonth())
                .ge(Objects.nonNull(query.getCreateTimeStart()), PayableList::getCreateTime, query.getCreateTimeStart())
                .le(Objects.nonNull(query.getCreateTimeEnd()), PayableList::getCreateTime, query.getCreateTimeEnd())
                .ge(Objects.nonNull(query.getSubmitTimeStart()), PayableList::getSubmitTime, query.getSubmitTimeStart())
                .le(Objects.nonNull(query.getSubmitTimeEnd()), PayableList::getSubmitTime, query.getSubmitTimeEnd())
                .ge(Objects.nonNull(query.getConfirmTimeStart()), PayableList::getConfirmTime, query.getConfirmTimeStart())
                .le(Objects.nonNull(query.getConfirmTimeEnd()), PayableList::getConfirmTime, query.getConfirmTimeEnd());
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }
}
