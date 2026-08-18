package com.leapmotor.export.vehicle.infrastructure.so.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.export.vehicle.infrastructure.so.dao.VehicleCbuSoDao;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoMapper;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSo;
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
public class VehicleCbuSoDaoImpl extends ServiceImpl<VehicleCbuSoMapper, VehicleCbuSo> implements VehicleCbuSoDao {

    @Override
    public QueryWrapper<VehicleCbuSo> buildQueryWrapper(VehicleCbuSoQo query) {
        QueryWrapper<VehicleCbuSo> queryWrapper = VehicleCbuSoDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(query.getStatusDesc()), VehicleCbuSo::getStatusDesc, query.getStatusDesc())
                .eq(StringUtils.isNotBlank(query.getSoNo()), VehicleCbuSo::getSoNo, query.getSoNo())
                .eq(StringUtils.isNotBlank(query.getPoId()), VehicleCbuSo::getPoId, query.getPoId())
                .eq(StringUtils.isNotBlank(query.getPoNo()), VehicleCbuSo::getPoNo, query.getPoNo())
                .eq(StringUtils.isNotBlank(query.getPoDetailId()), VehicleCbuSo::getPoDetailId, query.getPoDetailId())
                .eq(StringUtils.isNotBlank(query.getPoDetailNo()), VehicleCbuSo::getPoDetailNo, query.getPoDetailNo())
                .eq(Objects.nonNull(query.getLineNo()), VehicleCbuSo::getLineNo, query.getLineNo())
                .eq(StringUtils.isNotBlank(query.getSalesCompanyCode()), VehicleCbuSo::getSalesCompanyCode, query.getSalesCompanyCode())
                .eq(StringUtils.isNotBlank(query.getCustomerCode()), VehicleCbuSo::getCustomerCode, query.getCustomerCode())
                .eq(StringUtils.isNotBlank(query.getCustomerName()), VehicleCbuSo::getCustomerName, query.getCustomerName())
                .eq(StringUtils.isNotBlank(query.getExternalCustomerCode()), VehicleCbuSo::getExternalCustomerCode, query.getExternalCustomerCode())
                .eq(Objects.nonNull(query.getOrderType()), VehicleCbuSo::getOrderType, query.getOrderType())
                .eq(StringUtils.isNotBlank(query.getPlanVersion()), VehicleCbuSo::getPlanVersion, query.getPlanVersion())
                .eq(Objects.nonNull(query.getExpectedOfflineDate()), VehicleCbuSo::getExpectedOfflineDate, query.getExpectedOfflineDate())
                .eq(StringUtils.isNotBlank(query.getVinYear()), VehicleCbuSo::getVinYear, query.getVinYear())
                .eq(StringUtils.isNotBlank(query.getProductionRemark()), VehicleCbuSo::getProductionRemark, query.getProductionRemark())
                .eq(Objects.nonNull(query.getVehiclePrice()), VehicleCbuSo::getVehiclePrice, query.getVehiclePrice())
                .eq(Objects.nonNull(query.getFrozenAmount()), VehicleCbuSo::getFrozenAmount, query.getFrozenAmount())
                .eq(Objects.nonNull(query.getReceivableAmount()), VehicleCbuSo::getReceivableAmount, query.getReceivableAmount())
                .eq(Objects.nonNull(query.getOrderStatus()), VehicleCbuSo::getOrderStatus, query.getOrderStatus())
                .in(CollectionUtils.isNotEmpty(query.getOrderStatuses()), VehicleCbuSo::getOrderStatus, query.getOrderStatuses())
                .eq(Objects.nonNull(query.getIssueTime()), VehicleCbuSo::getIssueTime, query.getIssueTime())
                .eq(Objects.nonNull(query.getCancelTime()), VehicleCbuSo::getCancelTime, query.getCancelTime())
                .eq(Objects.nonNull(query.getQcResult()), VehicleCbuSo::getQcResult, query.getQcResult())
                .eq(Objects.nonNull(query.getQcResultTime()), VehicleCbuSo::getQcResultTime, query.getQcResultTime())
                .eq(StringUtils.isNotBlank(query.getQcRemark()), VehicleCbuSo::getQcRemark, query.getQcRemark())
                .eq(Objects.nonNull(query.getLastQcResult()), VehicleCbuSo::getLastQcResult, query.getLastQcResult())
                .eq(Objects.nonNull(query.getLastQcResultTime()), VehicleCbuSo::getLastQcResultTime, query.getLastQcResultTime())
                .eq(StringUtils.isNotBlank(query.getLastQcRemark()), VehicleCbuSo::getLastQcRemark, query.getLastQcRemark())
                .eq(Objects.nonNull(query.getReceiveTime()), VehicleCbuSo::getReceiveTime, query.getReceiveTime())
                .eq(StringUtils.isNotBlank(query.getRemark()), VehicleCbuSo::getRemark, query.getRemark())
                .like(StringUtils.isNotBlank(query.getCustomerNameLike()),
                        VehicleCbuSo::getCustomerName, query.getCustomerNameLike())
                .like(StringUtils.isNotBlank(query.getSoNoLike()), VehicleCbuSo::getSoNo, query.getSoNoLike())
                .like(StringUtils.isNotBlank(query.getPoNoLike()), VehicleCbuSo::getPoNo, query.getPoNoLike())
                .in(CollectionUtils.isNotEmpty(query.getSoNos()), VehicleCbuSo::getSoNo, query.getSoNos())
                .in(CollectionUtils.isNotEmpty(query.getPoNos()), VehicleCbuSo::getPoNo, query.getPoNos())
                .ge(Objects.nonNull(query.getCreateTimeStart()),
                        VehicleCbuSo::getCreateTime, query.getCreateTimeStart())
                .le(Objects.nonNull(query.getCreateTimeEnd()), VehicleCbuSo::getCreateTime, query.getCreateTimeEnd())
                .ge(Objects.nonNull(query.getIssueTimeStart()), VehicleCbuSo::getIssueTime, query.getIssueTimeStart())
                .le(Objects.nonNull(query.getIssueTimeEnd()), VehicleCbuSo::getIssueTime, query.getIssueTimeEnd())
                .ge(Objects.nonNull(query.getQcResultTimeStart()),
                        VehicleCbuSo::getQcResultTime, query.getQcResultTimeStart())
                .le(Objects.nonNull(query.getQcResultTimeEnd()),
                        VehicleCbuSo::getQcResultTime, query.getQcResultTimeEnd())
        ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

