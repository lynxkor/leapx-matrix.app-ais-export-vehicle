package com.leapmotor.export.vehicle.infrastructure.pricechangerecord.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.entity.PriceChangeRecord;
import com.leapmotor.export.vehicle.pricechangerecord.query.PriceChangeRecordQo;
import com.leapmotor.platform.ais.common.mybatis.support.LpBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PriceChangeRecordMapper extends LpBaseMapper<PriceChangeRecord> {

    Page<PriceChangeRecord> pagePriceChangeRecord(Page<PriceChangeRecord> page, @Param("qo") PriceChangeRecordQo qo);
}
