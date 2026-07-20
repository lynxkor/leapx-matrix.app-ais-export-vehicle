package com.leapmotor.export.vehicle.infrastructure.po.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportVehicleCbuPoDetailDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportVehicleCbuPoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoDetail;
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
public class ExportVehicleCbuPoDetailDaoImpl extends ServiceImpl<ExportVehicleCbuPoDetailMapper, ExportVehicleCbuPoDetail> implements ExportVehicleCbuPoDetailDao {

    @Override
    public QueryWrapper<ExportVehicleCbuPoDetail> buildQueryWrapper(ExportVehicleCbuPoDetailQo query) {
        QueryWrapper<ExportVehicleCbuPoDetail> queryWrapper = ExportVehicleCbuPoDetailDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            // PO单号: 精确 / 模糊 / 批量
            .eq(StringUtils.isNotBlank(query.getPoNo()), ExportVehicleCbuPoDetail::getPoNo, query.getPoNo())
            .like(StringUtils.isNotBlank(query.getPoNoLike()), ExportVehicleCbuPoDetail::getPoNo, query.getPoNoLike())
            .in(query.getPoNos() != null && !query.getPoNos().isEmpty(), ExportVehicleCbuPoDetail::getPoNo, query.getPoNos())
            .eq(StringUtils.isNotBlank(query.getPoDetailNo()), ExportVehicleCbuPoDetail::getPoDetailNo, query.getPoDetailNo())
            .eq(StringUtils.isNotBlank(query.getOrderDetailNumber()), ExportVehicleCbuPoDetail::getOrderDetailNumber, query.getOrderDetailNumber())
            // 物料编码: 精确 / 模糊 / 批量
            .eq(StringUtils.isNotBlank(query.getMaterialCode()), ExportVehicleCbuPoDetail::getMaterialCode, query.getMaterialCode())
            .like(StringUtils.isNotBlank(query.getMaterialCodeLike()), ExportVehicleCbuPoDetail::getMaterialCode, query.getMaterialCodeLike())
            .in(query.getMaterialCodes() != null && !query.getMaterialCodes().isEmpty(), ExportVehicleCbuPoDetail::getMaterialCode, query.getMaterialCodes())
            .eq(StringUtils.isNotBlank(query.getSyncMaterialCode()), ExportVehicleCbuPoDetail::getSyncMaterialCode, query.getSyncMaterialCode())
            // 物料描述: 精确 / 模糊 / 批量
            .eq(StringUtils.isNotBlank(query.getMaterialDescription()), ExportVehicleCbuPoDetail::getMaterialDescription, query.getMaterialDescription())
            .like(StringUtils.isNotBlank(query.getMaterialDescriptionLike()), ExportVehicleCbuPoDetail::getMaterialDescription, query.getMaterialDescriptionLike())
            .in(query.getMaterialDescriptions() != null && !query.getMaterialDescriptions().isEmpty(), ExportVehicleCbuPoDetail::getMaterialDescription, query.getMaterialDescriptions())
            .eq(StringUtils.isNotBlank(query.getSalesMarket()), ExportVehicleCbuPoDetail::getSalesMarket, query.getSalesMarket())
            .eq(StringUtils.isNotBlank(query.getModelYear()), ExportVehicleCbuPoDetail::getModelYear, query.getModelYear())
            .eq(StringUtils.isNotBlank(query.getOptionalPackage()), ExportVehicleCbuPoDetail::getOptionalPackage, query.getOptionalPackage())
            .eq(Objects.nonNull(query.getDemandQuantity()), ExportVehicleCbuPoDetail::getDemandQuantity, query.getDemandQuantity())
            .eq(Objects.nonNull(query.getExpectedOfflineDate()), ExportVehicleCbuPoDetail::getExpectedOfflineDate, query.getExpectedOfflineDate())
            .eq(StringUtils.isNotBlank(query.getVinYear()), ExportVehicleCbuPoDetail::getVinYear, query.getVinYear())
            .eq(StringUtils.isNotBlank(query.getProductionRemark()), ExportVehicleCbuPoDetail::getProductionRemark, query.getProductionRemark())
            .eq(Objects.nonNull(query.getUnitPrice()), ExportVehicleCbuPoDetail::getUnitPrice, query.getUnitPrice())
            .eq(Objects.nonNull(query.getTotalAmount()), ExportVehicleCbuPoDetail::getTotalAmount, query.getTotalAmount())
            // 创建时间范围
            .ge(Objects.nonNull(query.getCreateTimeStart()), ExportVehicleCbuPoDetail::getCreateTime, query.getCreateTimeStart())
            .le(Objects.nonNull(query.getCreateTimeEnd()), ExportVehicleCbuPoDetail::getCreateTime, query.getCreateTimeEnd())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()), ExportVehicleCbuPoDetail::getStatusDesc, query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()), ExportVehicleCbuPoDetail::getOwnerUserId, query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()), ExportVehicleCbuPoDetail::getOwnerOrgId, query.getOwnerOrgId())
            ;

        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}
