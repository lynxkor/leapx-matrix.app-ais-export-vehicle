package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportCbuVehiclePoDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportCbuVehiclePoMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePo;
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
public class ExportCbuVehiclePoDaoImpl extends ServiceImpl<ExportCbuVehiclePoMapper, ExportCbuVehiclePo> implements ExportCbuVehiclePoDao {

    @Override
    public QueryWrapper<ExportCbuVehiclePo> buildQueryWrapper(ExportCbuVehiclePoQo query) {
        QueryWrapper<ExportCbuVehiclePo> queryWrapper = ExportCbuVehiclePoDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNo()),ExportCbuVehiclePo::getPoNo,query.getPoNo())
            .eq(StringUtils.isNotBlank(query.getOrderStatus()),ExportCbuVehiclePo::getOrderStatus,query.getOrderStatus())
            .eq(StringUtils.isNotBlank(query.getSalesCompanyCode()),ExportCbuVehiclePo::getSalesCompanyCode,query.getSalesCompanyCode())
            .eq(StringUtils.isNotBlank(query.getCustomerCode()),ExportCbuVehiclePo::getCustomerCode,query.getCustomerCode())
            .eq(StringUtils.isNotBlank(query.getCustomerName()),ExportCbuVehiclePo::getCustomerName,query.getCustomerName())
            .eq(StringUtils.isNotBlank(query.getExternalCustomerCode()),ExportCbuVehiclePo::getExternalCustomerCode,query.getExternalCustomerCode())
            .eq(Objects.nonNull(query.getTotalAmount()),ExportCbuVehiclePo::getTotalAmount,query.getTotalAmount())
            .eq(Objects.nonNull(query.getSubmitTime()),ExportCbuVehiclePo::getSubmitTime,query.getSubmitTime())
            .eq(Objects.nonNull(query.getRejectTime()),ExportCbuVehiclePo::getRejectTime,query.getRejectTime())
            .eq(StringUtils.isNotBlank(query.getRejectUser()),ExportCbuVehiclePo::getRejectUser,query.getRejectUser())
            .eq(Objects.nonNull(query.getSupplyDemandApproveTime()),ExportCbuVehiclePo::getSupplyDemandApproveTime,query.getSupplyDemandApproveTime())
            .eq(StringUtils.isNotBlank(query.getSupplyDemandApprover()),ExportCbuVehiclePo::getSupplyDemandApprover,query.getSupplyDemandApprover())
            .eq(Objects.nonNull(query.getProductionSalesBalanceApproveTime()),ExportCbuVehiclePo::getProductionSalesBalanceApproveTime,query.getProductionSalesBalanceApproveTime())
            .eq(StringUtils.isNotBlank(query.getProductionSalesBalanceApprover()),ExportCbuVehiclePo::getProductionSalesBalanceApprover,query.getProductionSalesBalanceApprover())
            .eq(StringUtils.isNotBlank(query.getRejectReason()),ExportCbuVehiclePo::getRejectReason,query.getRejectReason())
            .eq(StringUtils.isNotBlank(query.getDataSource()),ExportCbuVehiclePo::getDataSource,query.getDataSource())
            .eq(Objects.nonNull(query.getOrderType()),ExportCbuVehiclePo::getOrderType,query.getOrderType())
            .eq(StringUtils.isNotBlank(query.getPlanVersion()),ExportCbuVehiclePo::getPlanVersion,query.getPlanVersion())
            .eq(Objects.nonNull(query.getAuditResultSyncTime()),ExportCbuVehiclePo::getAuditResultSyncTime,query.getAuditResultSyncTime())
            .eq(StringUtils.isNotBlank(query.getAuditResultSyncResult()),ExportCbuVehiclePo::getAuditResultSyncResult,query.getAuditResultSyncResult())
            .eq(StringUtils.isNotBlank(query.getCustomerSystemReturnInfo()),ExportCbuVehiclePo::getCustomerSystemReturnInfo,query.getCustomerSystemReturnInfo())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportCbuVehiclePo::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportCbuVehiclePo::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportCbuVehiclePo::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

