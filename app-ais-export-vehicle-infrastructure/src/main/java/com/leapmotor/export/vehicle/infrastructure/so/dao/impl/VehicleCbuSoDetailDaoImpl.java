package com.leapmotor.export.vehicle.infrastructure.so.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;
import com.leapmotor.export.vehicle.infrastructure.so.dao.VehicleCbuSoDetailDao;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoDetail;
import lombok.AllArgsConstructor;

import java.util.Objects;

/**
 * DB数据交互实现
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoDetailDaoImpl extends ServiceImpl<VehicleCbuSoDetailMapper, VehicleCbuSoDetail> implements VehicleCbuSoDetailDao {

    @Override
    public QueryWrapper<VehicleCbuSoDetail> buildQueryWrapper(VehicleCbuSoDetailQo query) {
        QueryWrapper<VehicleCbuSoDetail> queryWrapper = VehicleCbuSoDetailDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(query.getSoNo()), VehicleCbuSoDetail::getSoNo, query.getSoNo())
                .in(query.getSoNos() != null && !query.getSoNos().isEmpty(), VehicleCbuSoDetail::getSoNo, query.getSoNos())
                .eq(StringUtils.isNotBlank(query.getInternalVehicleNo()), VehicleCbuSoDetail::getInternalVehicleNo, query.getInternalVehicleNo())
                .eq(StringUtils.isNotBlank(query.getProductionIdentifyCode()), VehicleCbuSoDetail::getProductionIdentifyCode, query.getProductionIdentifyCode())
                .eq(StringUtils.isNotBlank(query.getVin()), VehicleCbuSoDetail::getVin, query.getVin())
                .eq(StringUtils.isNotBlank(query.getMaterialCode()), VehicleCbuSoDetail::getMaterialCode, query.getMaterialCode())
                .eq(StringUtils.isNotBlank(query.getMaterialDescription()), VehicleCbuSoDetail::getMaterialDescription, query.getMaterialDescription())
                .eq(StringUtils.isNotBlank(query.getSalesMarket()), VehicleCbuSoDetail::getSalesMarket, query.getSalesMarket())
                .eq(StringUtils.isNotBlank(query.getModelYear()), VehicleCbuSoDetail::getModelYear, query.getModelYear())
                .eq(StringUtils.isNotBlank(query.getOptionalPackage()), VehicleCbuSoDetail::getOptionalPackage, query.getOptionalPackage())
                .eq(Objects.nonNull(query.getScheduleTime()), VehicleCbuSoDetail::getScheduleTime, query.getScheduleTime())
                .eq(Objects.nonNull(query.getBarcodePrintTime()), VehicleCbuSoDetail::getBarcodePrintTime, query.getBarcodePrintTime())
                .eq(Objects.nonNull(query.getBodyInTime()), VehicleCbuSoDetail::getBodyInTime, query.getBodyInTime())
                .eq(Objects.nonNull(query.getPaintInTime()), VehicleCbuSoDetail::getPaintInTime, query.getPaintInTime())
                .eq(Objects.nonNull(query.getAssemblyInTime()), VehicleCbuSoDetail::getAssemblyInTime, query.getAssemblyInTime())
                .eq(Objects.nonNull(query.getAssemblyOutTime()), VehicleCbuSoDetail::getAssemblyOutTime, query.getAssemblyOutTime())
                .eq(StringUtils.isNotBlank(query.getRemark()), VehicleCbuSoDetail::getRemark, query.getRemark())
        ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

