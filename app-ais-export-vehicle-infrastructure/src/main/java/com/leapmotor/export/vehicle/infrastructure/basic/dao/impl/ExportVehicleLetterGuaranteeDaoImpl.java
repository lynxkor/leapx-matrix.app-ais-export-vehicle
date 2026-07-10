package com.leapmotor.export.vehicle.infrastructure.basic.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import com.leapmotor.export.vehicle.infrastructure.basic.dao.ExportVehicleLetterGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.basic.mapper.ExportVehicleLetterGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.basic.entity.ExportVehicleLetterGuarantee;
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
public class ExportVehicleLetterGuaranteeDaoImpl extends ServiceImpl<ExportVehicleLetterGuaranteeMapper, ExportVehicleLetterGuarantee> implements ExportVehicleLetterGuaranteeDao {

    @Override
    public QueryWrapper<ExportVehicleLetterGuarantee> buildQueryWrapper(ExportVehicleLetterGuaranteeQo query) {
        QueryWrapper<ExportVehicleLetterGuarantee> queryWrapper = ExportVehicleLetterGuaranteeDao.super.buildBasicQueryWrapper(query);
        queryWrapper.lambda()
            .eq(StringUtils.isNotBlank(query.getLgCode()),ExportVehicleLetterGuarantee::getLgCode,query.getLgCode())
            .like(StringUtils.isNotBlank(query.getLgCodeLike()),ExportVehicleLetterGuarantee::getLgCode,query.getLgCodeLike())
            .eq(StringUtils.isNotBlank(query.getCustomerId()),ExportVehicleLetterGuarantee::getCustomerId,query.getCustomerId())
            .eq(StringUtils.isNotBlank(query.getLgType()),ExportVehicleLetterGuarantee::getLgType,query.getLgType())
            .eq(StringUtils.isNotBlank(query.getOrderType()),ExportVehicleLetterGuarantee::getOrderType,query.getOrderType())
            .eq(StringUtils.isNotBlank(query.getBankName()),ExportVehicleLetterGuarantee::getBankName,query.getBankName())
            .eq(Objects.nonNull(query.getTotalAmount()),ExportVehicleLetterGuarantee::getTotalAmount,query.getTotalAmount())
            .eq(Objects.nonNull(query.getConversionRatio()),ExportVehicleLetterGuarantee::getConversionRatio,query.getConversionRatio())
            .eq(Objects.nonNull(query.getAvailableAmount()),ExportVehicleLetterGuarantee::getAvailableAmount,query.getAvailableAmount())
            .eq(Objects.nonNull(query.getOccupiedAmount()),ExportVehicleLetterGuarantee::getOccupiedAmount,query.getOccupiedAmount())
            .eq(Objects.nonNull(query.getReceivableAmount()),ExportVehicleLetterGuarantee::getReceivableAmount,query.getReceivableAmount())
            .eq(Objects.nonNull(query.getEffectiveDate()),ExportVehicleLetterGuarantee::getEffectiveDate,query.getEffectiveDate())
            .ge(Objects.nonNull(query.getEffectiveDateStart()),ExportVehicleLetterGuarantee::getEffectiveDate,query.getEffectiveDateStart())
            .le(Objects.nonNull(query.getEffectiveDateEnd()),ExportVehicleLetterGuarantee::getEffectiveDate,query.getEffectiveDateEnd())
            .le(Objects.nonNull(query.getEffectiveDateLe()),ExportVehicleLetterGuarantee::getEffectiveDate,query.getEffectiveDateLe())
            .eq(Objects.nonNull(query.getExpiryDate()),ExportVehicleLetterGuarantee::getExpiryDate,query.getExpiryDate())
            .ge(Objects.nonNull(query.getExpiryDateStart()),ExportVehicleLetterGuarantee::getExpiryDate,query.getExpiryDateStart())
            .le(Objects.nonNull(query.getExpiryDateEnd()),ExportVehicleLetterGuarantee::getExpiryDate,query.getExpiryDateEnd())
            .ge(Objects.nonNull(query.getExpiryDateGe()),ExportVehicleLetterGuarantee::getExpiryDate,query.getExpiryDateGe())
            .eq(StringUtils.isNotBlank(query.getApprovalStatus()),ExportVehicleLetterGuarantee::getApprovalStatus,query.getApprovalStatus())
            .eq(StringUtils.isNotBlank(query.getLgStatus()),ExportVehicleLetterGuarantee::getLgStatus,query.getLgStatus())
            .eq(Objects.nonNull(query.getSubmitterId()),ExportVehicleLetterGuarantee::getSubmitterId,query.getSubmitterId())
            .eq(StringUtils.isNotBlank(query.getSubmitterName()),ExportVehicleLetterGuarantee::getSubmitterName,query.getSubmitterName())
            .eq(Objects.nonNull(query.getSubmitTime()),ExportVehicleLetterGuarantee::getSubmitTime,query.getSubmitTime())
            .ge(Objects.nonNull(query.getSubmitTimeStart()),ExportVehicleLetterGuarantee::getSubmitTime,query.getSubmitTimeStart())
            .le(Objects.nonNull(query.getSubmitTimeEnd()),ExportVehicleLetterGuarantee::getSubmitTime,query.getSubmitTimeEnd())
            .eq(Objects.nonNull(query.getApproverId()),ExportVehicleLetterGuarantee::getApproverId,query.getApproverId())
            .eq(StringUtils.isNotBlank(query.getApproverName()),ExportVehicleLetterGuarantee::getApproverName,query.getApproverName())
            .eq(Objects.nonNull(query.getApproveTime()),ExportVehicleLetterGuarantee::getApproveTime,query.getApproveTime())
            .ge(Objects.nonNull(query.getApproveTimeStart()),ExportVehicleLetterGuarantee::getApproveTime,query.getApproveTimeStart())
            .le(Objects.nonNull(query.getApproveTimeEnd()),ExportVehicleLetterGuarantee::getApproveTime,query.getApproveTimeEnd())
            .eq(StringUtils.isNotBlank(query.getApproveRemark()),ExportVehicleLetterGuarantee::getApproveRemark,query.getApproveRemark())
            .eq(StringUtils.isNotBlank(query.getRemark()),ExportVehicleLetterGuarantee::getRemark,query.getRemark())
            .ge(Objects.nonNull(query.getCreateTimeStart()),ExportVehicleLetterGuarantee::getCreateTime,query.getCreateTimeStart())
            .le(Objects.nonNull(query.getCreateTimeEnd()),ExportVehicleLetterGuarantee::getCreateTime,query.getCreateTimeEnd())
            .eq(StringUtils.isNotBlank(query.getStatusDesc()),ExportVehicleLetterGuarantee::getStatusDesc,query.getStatusDesc())
            .eq(Objects.nonNull(query.getOwnerUserId()),ExportVehicleLetterGuarantee::getOwnerUserId,query.getOwnerUserId())
            .eq(Objects.nonNull(query.getOwnerOrgId()),ExportVehicleLetterGuarantee::getOwnerOrgId,query.getOwnerOrgId())
            ;
        queryWrapper.orderByDesc(STATUS_COLUMN).orderByDesc(CREATED_TIME_COLUMN).orderByDesc(ID_COLUMN);
        return queryWrapper;
    }

}

