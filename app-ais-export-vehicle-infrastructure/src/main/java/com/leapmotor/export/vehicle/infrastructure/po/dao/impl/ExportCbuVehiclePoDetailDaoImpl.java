package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoDetailQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportCbuVehiclePoDetailDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportCbuVehiclePoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePoDetail;
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
public class ExportCbuVehiclePoDetailDaoImpl extends ServiceImpl<ExportCbuVehiclePoDetailMapper, ExportCbuVehiclePoDetail> implements ExportCbuVehiclePoDetailDao {

    @Override
    public QueryWrapper<ExportCbuVehiclePoDetail> buildQueryWrapper(ExportCbuVehiclePoDetailQo query) {
        QueryWrapper<ExportCbuVehiclePoDetail> queryWrapper = ExportCbuVehiclePoDetailDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNo()),ExportCbuVehiclePoDetail::getPoNo,query.getPoNo())
            .eq(StringUtils.isNotBlank(query.getPoDetailNumber()),ExportCbuVehiclePoDetail::getPoDetailNumber,query.getPoDetailNumber())
            .eq(StringUtils.isNotBlank(query.getOrderDetailNumber()),ExportCbuVehiclePoDetail::getOrderDetailNumber,query.getOrderDetailNumber())
            .eq(StringUtils.isNotBlank(query.getMaterialCode()),ExportCbuVehiclePoDetail::getMaterialCode,query.getMaterialCode())
            .eq(StringUtils.isNotBlank(query.getSyncMaterialCode()),ExportCbuVehiclePoDetail::getSyncMaterialCode,query.getSyncMaterialCode())
            .eq(StringUtils.isNotBlank(query.getMaterialDescription()),ExportCbuVehiclePoDetail::getMaterialDescription,query.getMaterialDescription())
            .eq(StringUtils.isNotBlank(query.getSalesMarket()),ExportCbuVehiclePoDetail::getSalesMarket,query.getSalesMarket())
            .eq(StringUtils.isNotBlank(query.getYearModel()),ExportCbuVehiclePoDetail::getYearModel,query.getYearModel())
            .eq(StringUtils.isNotBlank(query.getOptionalPackage()),ExportCbuVehiclePoDetail::getOptionalPackage,query.getOptionalPackage())
            .eq(Objects.nonNull(query.getDemandQuantity()),ExportCbuVehiclePoDetail::getDemandQuantity,query.getDemandQuantity())
            .eq(Objects.nonNull(query.getExpectedOfflineDate()),ExportCbuVehiclePoDetail::getExpectedOfflineDate,query.getExpectedOfflineDate())
            .eq(StringUtils.isNotBlank(query.getVinYear()),ExportCbuVehiclePoDetail::getVinYear,query.getVinYear())
            .eq(StringUtils.isNotBlank(query.getProductionRemark()),ExportCbuVehiclePoDetail::getProductionRemark,query.getProductionRemark())
            .eq(Objects.nonNull(query.getUnitPrice()),ExportCbuVehiclePoDetail::getUnitPrice,query.getUnitPrice())
            .eq(Objects.nonNull(query.getTotalAmount()),ExportCbuVehiclePoDetail::getTotalAmount,query.getTotalAmount())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportCbuVehiclePoDetail::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportCbuVehiclePoDetail::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportCbuVehiclePoDetail::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

