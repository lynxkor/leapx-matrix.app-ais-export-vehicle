package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportVehicleCbuPoDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportVehicleCbuPoMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPo;
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
public class ExportVehicleCbuPoDaoImpl extends ServiceImpl<ExportVehicleCbuPoMapper, ExportVehicleCbuPo> implements ExportVehicleCbuPoDao {

    @Override
    public QueryWrapper<ExportVehicleCbuPo> buildQueryWrapper(ExportVehicleCbuPoQo query) {
        QueryWrapper<ExportVehicleCbuPo> queryWrapper = ExportVehicleCbuPoDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(ExportVehicleCbuPo::getStatus,1)
            .eq(StringUtils.isNotBlank(query.getPoNo()),ExportVehicleCbuPo::getPoNo,query.getPoNo())
            .eq(StringUtils.isNotBlank(query.getOrderStatus()),ExportVehicleCbuPo::getOrderStatus,query.getOrderStatus())
            .eq(StringUtils.isNotBlank(query.getSalesCompanyCode()),ExportVehicleCbuPo::getSalesCompanyCode,query.getSalesCompanyCode())
            .eq(StringUtils.isNotBlank(query.getCustomerCode()),ExportVehicleCbuPo::getCustomerCode,query.getCustomerCode())
            .like(StringUtils.isNotBlank(query.getCustomerName()),ExportVehicleCbuPo::getCustomerName,query.getCustomerName())
            .eq(StringUtils.isNotBlank(query.getExternalCustomerCode()),ExportVehicleCbuPo::getExternalCustomerCode,query.getExternalCustomerCode())
            .eq(Objects.nonNull(query.getTotalAmount()),ExportVehicleCbuPo::getTotalAmount,query.getTotalAmount())
            .ge(Objects.nonNull(query.getSubmitTimeStart()),ExportVehicleCbuPo::getSubmitTime,query.getSubmitTimeStart())
            .le(Objects.nonNull(query.getSubmitTimeEnd()),ExportVehicleCbuPo::getSubmitTime,query.getSubmitTimeEnd())
            .eq(Objects.nonNull(query.getRejectTime()),ExportVehicleCbuPo::getRejectTime,query.getRejectTime())
            .eq(StringUtils.isNotBlank(query.getRejectUser()),ExportVehicleCbuPo::getRejectUser,query.getRejectUser())
            .ge(Objects.nonNull(query.getSupplyDemandApproveTimeStart()),ExportVehicleCbuPo::getSupplyDemandApproveTime,query.getSupplyDemandApproveTimeStart())
            .le(Objects.nonNull(query.getSupplyDemandApproveTimeEnd()),ExportVehicleCbuPo::getSupplyDemandApproveTime,query.getSupplyDemandApproveTimeEnd())
            .eq(StringUtils.isNotBlank(query.getSupplyDemandApprover()),ExportVehicleCbuPo::getSupplyDemandApprover,query.getSupplyDemandApprover())
            .ge(Objects.nonNull(query.getProductionSalesBalanceApproveTimeStart()),ExportVehicleCbuPo::getProductionSalesBalanceApproveTime,query.getProductionSalesBalanceApproveTimeStart())
            .le(Objects.nonNull(query.getProductionSalesBalanceApproveTimeEnd()),ExportVehicleCbuPo::getProductionSalesBalanceApproveTime,query.getProductionSalesBalanceApproveTimeEnd())
            .eq(StringUtils.isNotBlank(query.getProductionSalesBalanceApprover()),ExportVehicleCbuPo::getProductionSalesBalanceApprover,query.getProductionSalesBalanceApprover())
            .eq(StringUtils.isNotBlank(query.getRejectReason()),ExportVehicleCbuPo::getRejectReason,query.getRejectReason())
            .eq(Objects.nonNull(query.getDataSource()),ExportVehicleCbuPo::getDataSource,query.getDataSource())
            .eq(Objects.nonNull(query.getOrderType()),ExportVehicleCbuPo::getOrderType,query.getOrderType())
            .eq(StringUtils.isNotBlank(query.getPlanVersion()),ExportVehicleCbuPo::getPlanVersion,query.getPlanVersion())
            .eq(Objects.nonNull(query.getAuditResultSyncTime()),ExportVehicleCbuPo::getAuditResultSyncTime,query.getAuditResultSyncTime())
            .eq(StringUtils.isNotBlank(query.getAuditResultSyncResult()),ExportVehicleCbuPo::getAuditResultSyncResult,query.getAuditResultSyncResult())
            .eq(StringUtils.isNotBlank(query.getCustomerSystemReturnInfo()),ExportVehicleCbuPo::getCustomerSystemReturnInfo,query.getCustomerSystemReturnInfo())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportVehicleCbuPo::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportVehicleCbuPo::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportVehicleCbuPo::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

