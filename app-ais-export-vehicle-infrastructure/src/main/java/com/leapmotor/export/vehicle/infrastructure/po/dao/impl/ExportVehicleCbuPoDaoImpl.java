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
import java.util.Collection;
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
            .like(StringUtils.isNotBlank(query.getPoNoLike()),ExportVehicleCbuPo::getPoNo,query.getPoNoLike())
            .in(query.getPoNos() != null && !query.getPoNos().isEmpty(),ExportVehicleCbuPo::getPoNo,query.getPoNos())
            .eq(StringUtils.isNotBlank(query.getOrderStatus()),ExportVehicleCbuPo::getOrderStatus,query.getOrderStatus())
            .in(query.getOrderStatuses() != null && !query.getOrderStatuses().isEmpty(),ExportVehicleCbuPo::getOrderStatus,query.getOrderStatuses())
            .eq(StringUtils.isNotBlank(query.getSalesCompanyCode()),ExportVehicleCbuPo::getSalesCompanyCode,query.getSalesCompanyCode())
            .eq(StringUtils.isNotBlank(query.getCustomerCode()),ExportVehicleCbuPo::getCustomerCode,query.getCustomerCode())
            .like(StringUtils.isNotBlank(query.getCustomerName()),ExportVehicleCbuPo::getCustomerName,query.getCustomerName())
            .like(StringUtils.isNotBlank(query.getCustomerNameLike()),ExportVehicleCbuPo::getCustomerName,query.getCustomerNameLike())
            .eq(StringUtils.isNotBlank(query.getExternalCustomerCode()),ExportVehicleCbuPo::getExternalCustomerCode,query.getExternalCustomerCode())
            .ge(Objects.nonNull(query.getSubmitTimeStart()),ExportVehicleCbuPo::getSubmitTime,query.getSubmitTimeStart())
            .le(Objects.nonNull(query.getSubmitTimeEnd()),ExportVehicleCbuPo::getSubmitTime,query.getSubmitTimeEnd())
            .ge(Objects.nonNull(query.getCreateTimeStart()),ExportVehicleCbuPo::getCreateTime,query.getCreateTimeStart())
            .le(Objects.nonNull(query.getCreateTimeEnd()),ExportVehicleCbuPo::getCreateTime,query.getCreateTimeEnd())
            .ge(Objects.nonNull(query.getSupplyDemandApproveTimeStart()),ExportVehicleCbuPo::getSupplyDemandApproveTime,query.getSupplyDemandApproveTimeStart())
            .le(Objects.nonNull(query.getSupplyDemandApproveTimeEnd()),ExportVehicleCbuPo::getSupplyDemandApproveTime,query.getSupplyDemandApproveTimeEnd())
            .ge(Objects.nonNull(query.getProductionSalesBalanceApproveTimeStart()),ExportVehicleCbuPo::getProductionSalesBalanceApproveTime,query.getProductionSalesBalanceApproveTimeStart())
            .le(Objects.nonNull(query.getProductionSalesBalanceApproveTimeEnd()),ExportVehicleCbuPo::getProductionSalesBalanceApproveTime,query.getProductionSalesBalanceApproveTimeEnd())
            .eq(Objects.nonNull(query.getDataSource()),ExportVehicleCbuPo::getDataSource,query.getDataSource())
            .eq(Objects.nonNull(query.getOrderType()),ExportVehicleCbuPo::getOrderType,query.getOrderType())
            ;
        queryWrapper.orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

