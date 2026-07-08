package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.PurchaseOrderDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.PurchaseOrderMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrder;
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
public class PurchaseOrderDaoImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderDao {

    @Override
    public QueryWrapper<PurchaseOrder> buildQueryWrapper(PurchaseOrderQo query) {
        QueryWrapper<PurchaseOrder> queryWrapper = PurchaseOrderDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getPoNumber()),PurchaseOrder::getPoNumber,query.getPoNumber())
            .eq(StringUtils.isNotBlank(query.getOrderStatus()),PurchaseOrder::getOrderStatus,query.getOrderStatus())
            .eq(StringUtils.isNotBlank(query.getSalesCompany()),PurchaseOrder::getSalesCompany,query.getSalesCompany())
            .eq(StringUtils.isNotBlank(query.getCustomerCode()),PurchaseOrder::getCustomerCode,query.getCustomerCode())
            .eq(StringUtils.isNotBlank(query.getCustomerName()),PurchaseOrder::getCustomerName,query.getCustomerName())
            .eq(StringUtils.isNotBlank(query.getExternalCustomerCode()),PurchaseOrder::getExternalCustomerCode,query.getExternalCustomerCode())
            .eq(Objects.nonNull(query.getTotalAmount()),PurchaseOrder::getTotalAmount,query.getTotalAmount())
            .eq(Objects.nonNull(query.getSubmitTime()),PurchaseOrder::getSubmitTime,query.getSubmitTime())
            .eq(Objects.nonNull(query.getRejectTime()),PurchaseOrder::getRejectTime,query.getRejectTime())
            .eq(StringUtils.isNotBlank(query.getRejectUser()),PurchaseOrder::getRejectUser,query.getRejectUser())
            .eq(Objects.nonNull(query.getSupplyDemandApproveTime()),PurchaseOrder::getSupplyDemandApproveTime,query.getSupplyDemandApproveTime())
            .eq(StringUtils.isNotBlank(query.getSupplyDemandApprover()),PurchaseOrder::getSupplyDemandApprover,query.getSupplyDemandApprover())
            .eq(Objects.nonNull(query.getProductionSalesBalanceApproveTime()),PurchaseOrder::getProductionSalesBalanceApproveTime,query.getProductionSalesBalanceApproveTime())
            .eq(StringUtils.isNotBlank(query.getProductionSalesBalanceApprover()),PurchaseOrder::getProductionSalesBalanceApprover,query.getProductionSalesBalanceApprover())
            .eq(StringUtils.isNotBlank(query.getRejectReason()),PurchaseOrder::getRejectReason,query.getRejectReason())
            .eq(StringUtils.isNotBlank(query.getDataSource()),PurchaseOrder::getDataSource,query.getDataSource())
            .eq(StringUtils.isNotBlank(query.getOrderType()),PurchaseOrder::getOrderType,query.getOrderType())
            .eq(StringUtils.isNotBlank(query.getPlanVersion()),PurchaseOrder::getPlanVersion,query.getPlanVersion())
            .eq(Objects.nonNull(query.getAuditResultSyncTime()),PurchaseOrder::getAuditResultSyncTime,query.getAuditResultSyncTime())
            .eq(StringUtils.isNotBlank(query.getAuditResultSyncResult()),PurchaseOrder::getAuditResultSyncResult,query.getAuditResultSyncResult())
            .eq(StringUtils.isNotBlank(query.getCustomerSystemReturnInfo()),PurchaseOrder::getCustomerSystemReturnInfo,query.getCustomerSystemReturnInfo())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),PurchaseOrder::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),PurchaseOrder::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),PurchaseOrder::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

