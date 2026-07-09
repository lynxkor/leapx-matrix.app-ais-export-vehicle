package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoGuaranteeQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportCbuVehiclePoGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportCbuVehiclePoGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePoGuarantee;
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
public class ExportCbuVehiclePoGuaranteeDaoImpl extends ServiceImpl<ExportCbuVehiclePoGuaranteeMapper, ExportCbuVehiclePoGuarantee> implements ExportCbuVehiclePoGuaranteeDao {

    @Override
    public QueryWrapper<ExportCbuVehiclePoGuarantee> buildQueryWrapper(ExportCbuVehiclePoGuaranteeQo query) {
        QueryWrapper<ExportCbuVehiclePoGuarantee> queryWrapper = ExportCbuVehiclePoGuaranteeDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNo()),ExportCbuVehiclePoGuarantee::getPoNo,query.getPoNo())
            .eq(StringUtils.isNotBlank(query.getGuaranteeId()),ExportCbuVehiclePoGuarantee::getGuaranteeId,query.getGuaranteeId())
            .eq(StringUtils.isNotBlank(query.getGuaranteeNumber()),ExportCbuVehiclePoGuarantee::getGuaranteeNumber,query.getGuaranteeNumber())
            .eq(Objects.nonNull(query.getPoCurrentOccupiedAmount()),ExportCbuVehiclePoGuarantee::getPoCurrentOccupiedAmount,query.getPoCurrentOccupiedAmount())
            .eq(Objects.nonNull(query.getPoCurrentReceivableAmount()),ExportCbuVehiclePoGuarantee::getPoCurrentReceivableAmount,query.getPoCurrentReceivableAmount())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportCbuVehiclePoGuarantee::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportCbuVehiclePoGuarantee::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportCbuVehiclePoGuarantee::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

