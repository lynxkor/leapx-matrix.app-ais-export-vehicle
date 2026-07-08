package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderGuaranteeQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.PurchaseOrderGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.PurchaseOrderGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderGuarantee;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * DB数据交互实现
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class PurchaseOrderGuaranteeDaoImpl extends ServiceImpl<PurchaseOrderGuaranteeMapper, PurchaseOrderGuarantee> implements PurchaseOrderGuaranteeDao {

    @Override
    public QueryWrapper<PurchaseOrderGuarantee> buildQueryWrapper(PurchaseOrderGuaranteeQo query) {
        QueryWrapper<PurchaseOrderGuarantee> queryWrapper = PurchaseOrderGuaranteeDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNumber()),PurchaseOrderGuarantee::getPoNumber,query.getPoNumber())
            .eq(StringUtils.isNotBlank(query.getGuaranteeId()),PurchaseOrderGuarantee::getGuaranteeId,query.getGuaranteeId())
            .eq(StringUtils.isNotBlank(query.getGuaranteeNumber()),PurchaseOrderGuarantee::getGuaranteeNumber,query.getGuaranteeNumber())
            .eq(Objects.nonNull(query.getPoCurrentOccupiedAmount()),PurchaseOrderGuarantee::getPoCurrentOccupiedAmount,query.getPoCurrentOccupiedAmount())
            .eq(Objects.nonNull(query.getPoCurrentReceivableAmount()),PurchaseOrderGuarantee::getPoCurrentReceivableAmount,query.getPoCurrentReceivableAmount())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),PurchaseOrderGuarantee::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),PurchaseOrderGuarantee::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),PurchaseOrderGuarantee::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

