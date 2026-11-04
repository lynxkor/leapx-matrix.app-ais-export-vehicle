package com.leapmotor.export.vehicle.infrastructure.pricechangerecord.gatewayimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.convertor.PriceChangeRecordStructMapper;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.dao.PriceChangeRecordDao;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.entity.PriceChangeRecord;
import com.leapmotor.export.vehicle.infrastructure.pricechangerecord.mapper.PriceChangeRecordMapper;
import com.leapmotor.export.vehicle.pricechangerecord.gateway.PriceChangeRecordGateway;
import com.leapmotor.export.vehicle.pricechangerecord.model.PriceChangeRecordDo;
import com.leapmotor.export.vehicle.pricechangerecord.query.PriceChangeRecordQo;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.user.LoginUser;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PriceChangeRecordGatewayImpl implements PriceChangeRecordGateway {

    private final PriceChangeRecordMapper priceChangeRecordMapper;
    private final PriceChangeRecordDao priceChangeRecordDao;

    @Override
    public String addPriceChangeRecord(PriceChangeRecordDo recordDo) {
        PriceChangeRecord entity = PriceChangeRecordStructMapper.INSTANCE.domainToDataObject(recordDo);
        LoginUser user = LoginUserUtils.getCurrentUserOrSysDefault();
        entity.setCreator(user.getUserId());
        entity.setCreatorName(user.getUserName());
        int result = priceChangeRecordMapper.insert(entity);
        recordDo.setId(entity.getId());
        return result > 0 ? entity.getId() : null;
    }

    @Override
    public int addBatchPriceChangeRecord(List<PriceChangeRecordDo> recordDos) {
        LoginUser user = LoginUserUtils.getCurrentUserOrSysDefault();
        List<PriceChangeRecord> entities = PriceChangeRecordStructMapper.INSTANCE.domainToDataObjects(recordDos);
        for (PriceChangeRecord entity : entities) {
            entity.setCreator(user.getUserId());
            entity.setCreatorName(user.getUserName());
            if (entity.getOwnerUserId() == null) { entity.setOwnerUserId(0L); }
            if (entity.getOwnerOrgId() == null) { entity.setOwnerOrgId(0L); }
        }
        return priceChangeRecordDao.batchSaveSomeColumn(entities);
    }

    @Override
    public PriceChangeRecordDo getById(String id) {
        PriceChangeRecord entity = priceChangeRecordDao.getById(id);
        return entity != null ? PriceChangeRecordStructMapper.INSTANCE.dataObjectToDomain(entity) : null;
    }

    @Override
    public PageResult<PriceChangeRecordDo> pagePriceChangeRecord(PriceChangeRecordQo qo) {
        Page<PriceChangeRecord> page = new Page<>(qo.getPageNum(), qo.getPageSize());
        Page<PriceChangeRecord> result = priceChangeRecordMapper.pagePriceChangeRecord(page, qo);
        return PageUtils.transferPage(PriceChangeRecordStructMapper.INSTANCE::dataObjectsToDomains, result);
    }
}
