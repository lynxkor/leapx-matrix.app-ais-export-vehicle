package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportVehicleCbuPoDetailDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportVehicleCbuPoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoDetail;
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
public class ExportVehicleCbuPoDetailDaoImpl extends ServiceImpl<ExportVehicleCbuPoDetailMapper, ExportVehicleCbuPoDetail> implements ExportVehicleCbuPoDetailDao {

    @Override
    public QueryWrapper<ExportVehicleCbuPoDetail> buildQueryWrapper(ExportVehicleCbuPoDetailQo query) {
        QueryWrapper<ExportVehicleCbuPoDetail> queryWrapper = ExportVehicleCbuPoDetailDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNo()),ExportVehicleCbuPoDetail::getPoNo,query.getPoNo())
            .eq(StringUtils.isNotBlank(query.getPoDetailNo()),ExportVehicleCbuPoDetail::getPoDetailNo,query.getPoDetailNo())
            .eq(StringUtils.isNotBlank(query.getOrderDetailNumber()),ExportVehicleCbuPoDetail::getOrderDetailNumber,query.getOrderDetailNumber())
            .eq(StringUtils.isNotBlank(query.getMaterialCode()),ExportVehicleCbuPoDetail::getMaterialCode,query.getMaterialCode())
            .eq(StringUtils.isNotBlank(query.getSyncMaterialCode()),ExportVehicleCbuPoDetail::getSyncMaterialCode,query.getSyncMaterialCode())
            .eq(StringUtils.isNotBlank(query.getMaterialDescription()),ExportVehicleCbuPoDetail::getMaterialDescription,query.getMaterialDescription())
            .eq(StringUtils.isNotBlank(query.getSalesMarket()),ExportVehicleCbuPoDetail::getSalesMarket,query.getSalesMarket())
            .eq(StringUtils.isNotBlank(query.getModelYear()),ExportVehicleCbuPoDetail::getModelYear,query.getModelYear())
            .eq(StringUtils.isNotBlank(query.getOptionalPackage()),ExportVehicleCbuPoDetail::getOptionalPackage,query.getOptionalPackage())
            .eq(Objects.nonNull(query.getDemandQuantity()),ExportVehicleCbuPoDetail::getDemandQuantity,query.getDemandQuantity())
            .eq(Objects.nonNull(query.getExpectedOfflineDate()),ExportVehicleCbuPoDetail::getExpectedOfflineDate,query.getExpectedOfflineDate())
            .eq(StringUtils.isNotBlank(query.getVinYear()),ExportVehicleCbuPoDetail::getVinYear,query.getVinYear())
            .eq(StringUtils.isNotBlank(query.getProductionRemark()),ExportVehicleCbuPoDetail::getProductionRemark,query.getProductionRemark())
            .eq(Objects.nonNull(query.getUnitPrice()),ExportVehicleCbuPoDetail::getUnitPrice,query.getUnitPrice())
            .eq(Objects.nonNull(query.getTotalAmount()),ExportVehicleCbuPoDetail::getTotalAmount,query.getTotalAmount())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportVehicleCbuPoDetail::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportVehicleCbuPoDetail::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportVehicleCbuPoDetail::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

