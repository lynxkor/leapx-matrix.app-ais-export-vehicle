package com.leapmotor.export.vehicle.infrastructure.so.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.infrastructure.so.dao.VehicleCbuSoInterfaceSyncDao;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoInterfaceSyncMapper;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoInterfaceSync;
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
public class VehicleCbuSoInterfaceSyncDaoImpl extends ServiceImpl<VehicleCbuSoInterfaceSyncMapper, VehicleCbuSoInterfaceSync> implements VehicleCbuSoInterfaceSyncDao {

    @Override
    public QueryWrapper<VehicleCbuSoInterfaceSync> buildQueryWrapper(VehicleCbuSoInterfaceSyncQo query) {
        QueryWrapper<VehicleCbuSoInterfaceSync> queryWrapper = VehicleCbuSoInterfaceSyncDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(query.getSoId()), VehicleCbuSoInterfaceSync::getSoId, query.getSoId())
                .eq(Objects.nonNull(query.getInterfaceType()), VehicleCbuSoInterfaceSync::getInterfaceType, query.getInterfaceType())
                .eq(StringUtils.isNotBlank(query.getInterfaceDesc()), VehicleCbuSoInterfaceSync::getInterfaceDesc, query.getInterfaceDesc())
                .eq(Objects.nonNull(query.getCallTime()), VehicleCbuSoInterfaceSync::getCallTime, query.getCallTime())
                .eq(StringUtils.isNotBlank(query.getRequestInfo()), VehicleCbuSoInterfaceSync::getRequestInfo, query.getRequestInfo())
                .eq(Objects.nonNull(query.getResultStatus()), VehicleCbuSoInterfaceSync::getResultStatus, query.getResultStatus())
                .eq(StringUtils.isNotBlank(query.getResponseInfo()), VehicleCbuSoInterfaceSync::getResponseInfo, query.getResponseInfo())
                .eq(StringUtils.isNotBlank(query.getRemark()), VehicleCbuSoInterfaceSync::getRemark, query.getRemark())
        ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

