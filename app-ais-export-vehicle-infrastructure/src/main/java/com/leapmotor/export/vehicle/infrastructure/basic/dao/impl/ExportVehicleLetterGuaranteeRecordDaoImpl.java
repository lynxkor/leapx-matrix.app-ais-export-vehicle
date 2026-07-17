package com.leapmotor.export.vehicle.infrastructure.basic.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.infrastructure.basic.dao.ExportVehicleLetterGuaranteeRecordDao;
import com.leapmotor.export.vehicle.infrastructure.basic.mapper.ExportVehicleLetterGuaranteeRecordMapper;
import com.leapmotor.export.vehicle.infrastructure.basic.entity.ExportVehicleLetterGuaranteeRecord;
import lombok.AllArgsConstructor;
import java.util.Objects;

/**
 * DB数据交互实现
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeRecordDaoImpl extends ServiceImpl<ExportVehicleLetterGuaranteeRecordMapper, ExportVehicleLetterGuaranteeRecord> implements ExportVehicleLetterGuaranteeRecordDao {

    @Override
    public QueryWrapper<ExportVehicleLetterGuaranteeRecord> buildQueryWrapper(ExportVehicleLetterGuaranteeRecordQo query) {
        QueryWrapper<ExportVehicleLetterGuaranteeRecord> queryWrapper = ExportVehicleLetterGuaranteeRecordDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getLgId()),ExportVehicleLetterGuaranteeRecord::getLgId,query.getLgId())
            .eq(StringUtils.isNotBlank(query.getLgType()),ExportVehicleLetterGuaranteeRecord::getLgType,query.getLgType())
            .eq(StringUtils.isNotBlank(query.getCustomerId()),ExportVehicleLetterGuaranteeRecord::getCustomerId,query.getCustomerId())
            .eq(StringUtils.isNotBlank(query.getTransactionType()),ExportVehicleLetterGuaranteeRecord::getTransactionType,query.getTransactionType())
            .eq(StringUtils.isNotBlank(query.getLgRecordCode()),ExportVehicleLetterGuaranteeRecord::getLgRecordCode,query.getLgRecordCode())
            .like(StringUtils.isNotBlank(query.getLgRecordCodeLike()),ExportVehicleLetterGuaranteeRecord::getLgRecordCode,query.getLgRecordCodeLike())
            .in(CollectionUtils.isNotEmpty(query.getLgRecordCodes()),ExportVehicleLetterGuaranteeRecord::getLgRecordCode,query.getLgRecordCodes())
            .like(StringUtils.isNotBlank(query.getLgCodeLike()),ExportVehicleLetterGuaranteeRecord::getLgCode,query.getLgCodeLike())
            .in(CollectionUtils.isNotEmpty(query.getLgCodes()),ExportVehicleLetterGuaranteeRecord::getLgCode,query.getLgCodes())
            .eq(StringUtils.isNotBlank(query.getLgCode()),ExportVehicleLetterGuaranteeRecord::getLgCode,query.getLgCode())
            .like(StringUtils.isNotBlank(query.getPoNoLike()),ExportVehicleLetterGuaranteeRecord::getPoNo,query.getPoNoLike())
            .in(CollectionUtils.isNotEmpty(query.getPoNos()),ExportVehicleLetterGuaranteeRecord::getPoNo,query.getPoNos())
            .eq(StringUtils.isNotBlank(query.getPoNo()),ExportVehicleLetterGuaranteeRecord::getPoNo,query.getPoNo())
            .like(StringUtils.isNotBlank(query.getSoNoLike()),ExportVehicleLetterGuaranteeRecord::getSoNo,query.getSoNoLike())
            .in(CollectionUtils.isNotEmpty(query.getSoNos()),ExportVehicleLetterGuaranteeRecord::getSoNo,query.getSoNos())
            .eq(StringUtils.isNotBlank(query.getSoNo()),ExportVehicleLetterGuaranteeRecord::getSoNo,query.getSoNo())
            .like(StringUtils.isNotBlank(query.getVinLike()),ExportVehicleLetterGuaranteeRecord::getVin,query.getVinLike())
            .in(CollectionUtils.isNotEmpty(query.getVins()),ExportVehicleLetterGuaranteeRecord::getVin,query.getVins())
            .eq(StringUtils.isNotBlank(query.getVin()),ExportVehicleLetterGuaranteeRecord::getVin,query.getVin())
            .in(CollectionUtils.isNotEmpty(query.getTransactionTypes()),ExportVehicleLetterGuaranteeRecord::getTransactionType,query.getTransactionTypes())
            .ge(Objects.nonNull(query.getCreateTimeStart()),ExportVehicleLetterGuaranteeRecord::getCreateTime,query.getCreateTimeStart())
            .le(Objects.nonNull(query.getCreateTimeEnd()),ExportVehicleLetterGuaranteeRecord::getCreateTime,query.getCreateTimeEnd())
            .eq(Objects.nonNull(query.getTransactionAmount()),ExportVehicleLetterGuaranteeRecord::getTransactionAmount,query.getTransactionAmount())
            .eq(StringUtils.isNotBlank(query.getTransactionRemark()),ExportVehicleLetterGuaranteeRecord::getTransactionRemark,query.getTransactionRemark())
            .eq(Objects.nonNull(query.getBeforeAvailable()),ExportVehicleLetterGuaranteeRecord::getBeforeAvailable,query.getBeforeAvailable())
            .eq(Objects.nonNull(query.getAfterAvailable()),ExportVehicleLetterGuaranteeRecord::getAfterAvailable,query.getAfterAvailable())
            .eq(Objects.nonNull(query.getBeforeOccupied()),ExportVehicleLetterGuaranteeRecord::getBeforeOccupied,query.getBeforeOccupied())
            .eq(Objects.nonNull(query.getAfterOccupied()),ExportVehicleLetterGuaranteeRecord::getAfterOccupied,query.getAfterOccupied())
            .eq(Objects.nonNull(query.getBeforeReceivable()),ExportVehicleLetterGuaranteeRecord::getBeforeReceivable,query.getBeforeReceivable())
            .eq(Objects.nonNull(query.getAfterReceivable()),ExportVehicleLetterGuaranteeRecord::getAfterReceivable,query.getAfterReceivable())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportVehicleLetterGuaranteeRecord::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportVehicleLetterGuaranteeRecord::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportVehicleLetterGuaranteeRecord::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}