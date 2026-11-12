package com.leapmotor.export.vehicle.infrastructure.pricechangerecord.dao.impl;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.dao.PriceChangeRecordDao;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.entity.PriceChangeRecord;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.mapper.PriceChangeRecordMapper;
import com.leapmotor.export.vehicle.pricechangerecord.query.PriceChangeRecordQo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PriceChangeRecordDaoImpl extends ServiceImpl<PriceChangeRecordMapper, PriceChangeRecord> implements PriceChangeRecordDao {

    @Override
    public QueryWrapper<PriceChangeRecord> buildQueryWrapper(PriceChangeRecordQo query) {
        QueryWrapper<PriceChangeRecord> queryWrapper = PriceChangeRecordDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
                .in(query.getVins() != null && !query.getVins().isEmpty(),
                        PriceChangeRecord::getVin, query.getVins())
                .in(query.getSoNos() != null && !query.getSoNos().isEmpty(),
                        PriceChangeRecord::getSoNo, query.getSoNos())
                .ge(Objects.nonNull(query.getCreateTimeStart()), PriceChangeRecord::getCreateTime, query.getCreateTimeStart())
                .le(Objects.nonNull(query.getCreateTimeEnd()), PriceChangeRecord::getCreateTime, query.getCreateTimeEnd());
        queryWrapper.orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }
}
