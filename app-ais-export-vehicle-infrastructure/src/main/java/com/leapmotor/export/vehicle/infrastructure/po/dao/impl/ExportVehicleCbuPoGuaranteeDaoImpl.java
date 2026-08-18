package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportVehicleCbuPoGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportVehicleCbuPoGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoGuarantee;
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
public class ExportVehicleCbuPoGuaranteeDaoImpl extends ServiceImpl<ExportVehicleCbuPoGuaranteeMapper, ExportVehicleCbuPoGuarantee> implements ExportVehicleCbuPoGuaranteeDao {

    @Override
    public QueryWrapper<ExportVehicleCbuPoGuarantee> buildQueryWrapper(ExportVehicleCbuPoGuaranteeQo query) {
        QueryWrapper<ExportVehicleCbuPoGuarantee> queryWrapper = ExportVehicleCbuPoGuaranteeDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNo()),ExportVehicleCbuPoGuarantee::getPoNo,query.getPoNo())
            .eq(StringUtils.isNotBlank(query.getGuaranteeId()),ExportVehicleCbuPoGuarantee::getGuaranteeId,query.getGuaranteeId())
            .eq(StringUtils.isNotBlank(query.getGuaranteeNumber()),ExportVehicleCbuPoGuarantee::getGuaranteeNumber,query.getGuaranteeNumber())
            .eq(Objects.nonNull(query.getPoCurrentOccupiedAmount()),ExportVehicleCbuPoGuarantee::getPoCurrentOccupiedAmount,query.getPoCurrentOccupiedAmount())
            .eq(Objects.nonNull(query.getPoCurrentReceivableAmount()),ExportVehicleCbuPoGuarantee::getPoCurrentReceivableAmount,query.getPoCurrentReceivableAmount())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportVehicleCbuPoGuarantee::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportVehicleCbuPoGuarantee::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportVehicleCbuPoGuarantee::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

