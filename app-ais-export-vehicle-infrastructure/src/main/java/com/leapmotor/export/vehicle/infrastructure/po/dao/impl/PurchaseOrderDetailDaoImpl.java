package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderDetailQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.PurchaseOrderDetailDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.PurchaseOrderDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderDetail;
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
public class PurchaseOrderDetailDaoImpl extends ServiceImpl<PurchaseOrderDetailMapper, PurchaseOrderDetail> implements PurchaseOrderDetailDao {

    @Override
    public QueryWrapper<PurchaseOrderDetail> buildQueryWrapper(PurchaseOrderDetailQo query) {
        QueryWrapper<PurchaseOrderDetail> queryWrapper = PurchaseOrderDetailDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNumber()),PurchaseOrderDetail::getPoNumber,query.getPoNumber())
            .eq(StringUtils.isNotBlank(query.getPoDetailNumber()),PurchaseOrderDetail::getPoDetailNumber,query.getPoDetailNumber())
            .eq(StringUtils.isNotBlank(query.getOrderDetailNumber()),PurchaseOrderDetail::getOrderDetailNumber,query.getOrderDetailNumber())
            .eq(StringUtils.isNotBlank(query.getMaterialCode()),PurchaseOrderDetail::getMaterialCode,query.getMaterialCode())
            .eq(StringUtils.isNotBlank(query.getSyncMaterialCode()),PurchaseOrderDetail::getSyncMaterialCode,query.getSyncMaterialCode())
            .eq(StringUtils.isNotBlank(query.getMaterialDescription()),PurchaseOrderDetail::getMaterialDescription,query.getMaterialDescription())
            .eq(StringUtils.isNotBlank(query.getSalesMarket()),PurchaseOrderDetail::getSalesMarket,query.getSalesMarket())
            .eq(StringUtils.isNotBlank(query.getYearModel()),PurchaseOrderDetail::getYearModel,query.getYearModel())
            .eq(StringUtils.isNotBlank(query.getOptionalPackage()),PurchaseOrderDetail::getOptionalPackage,query.getOptionalPackage())
            .eq(Objects.nonNull(query.getDemandQuantity()),PurchaseOrderDetail::getDemandQuantity,query.getDemandQuantity())
            .eq(Objects.nonNull(query.getExpectedOfflineDate()),PurchaseOrderDetail::getExpectedOfflineDate,query.getExpectedOfflineDate())
            .eq(StringUtils.isNotBlank(query.getVinYear()),PurchaseOrderDetail::getVinYear,query.getVinYear())
            .eq(StringUtils.isNotBlank(query.getProductionRemark()),PurchaseOrderDetail::getProductionRemark,query.getProductionRemark())
            .eq(Objects.nonNull(query.getUnitPrice()),PurchaseOrderDetail::getUnitPrice,query.getUnitPrice())
            .eq(Objects.nonNull(query.getTotalAmount()),PurchaseOrderDetail::getTotalAmount,query.getTotalAmount())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),PurchaseOrderDetail::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),PurchaseOrderDetail::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),PurchaseOrderDetail::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

