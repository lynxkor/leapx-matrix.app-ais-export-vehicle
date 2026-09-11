package com.leapmotor.export.vehicle.infrastructure.payablelist.gatewayimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.infrastructure.payablelist.convertor.PayableListDetailStructMapper;
import com.leapmotor.export.vehicle.infrastructure.payablelist.dao.PayableListDetailDao;
import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableListDetail;
import com.leapmotor.export.vehicle.infrastructure.payablelist.mapper.PayableListDetailMapper;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 应付清单明细领域网关实现
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@AllArgsConstructor
@Service
public class PayableListDetailGatewayImpl implements PayableListDetailGateway {

    private PayableListDetailMapper payableListDetailMapper;
    private PayableListDetailDao payableListDetailDao;

    @Override
    public String addPayableListDetail(PayableListDetailDo detailDo) {
        PayableListDetail detail = PayableListDetailStructMapper.INSTANCE.domainToDataObject(detailDo);
        int result = payableListDetailMapper.insert(detail);
        detailDo.setId(detail.getId());
        if (result > 0) {
            return detail.getId();
        } else {
            return null;
        }
    }

    @Override
    public int addBatchPayableListDetail(List<PayableListDetailDo> detailDos) {
        List<PayableListDetail> details = PayableListDetailStructMapper.INSTANCE.domainToDataObjects(detailDos);
        // 显式填充 owner_user_id / owner_org_id 默认值，避免 insertBatchSomeColumn 将 null 拼进 INSERT 触发 NOT NULL 报错
        for (PayableListDetail detail : details) {
            if (detail.getOwnerUserId() == null) {
                detail.setOwnerUserId(0L);
            }
            if (detail.getOwnerOrgId() == null) {
                detail.setOwnerOrgId(0L);
            }
        }
        return payableListDetailDao.batchSaveSomeColumn(details);
    }

    @Override
    public String updatePayableListDetail(PayableListDetailDo detailDo) {
        PayableListDetail detail = PayableListDetailStructMapper.INSTANCE.domainToDataObject(detailDo);
        int result = payableListDetailMapper.updateById(detail);
        if (result > 0) {
            return detail.getId();
        } else {
            return null;
        }
    }

    @Override
    public PayableListDetailDo getPayableListDetailById(String id) {
        PayableListDetail detail = payableListDetailMapper.selectById(id);
        return PayableListDetailStructMapper.INSTANCE.dataObjectToDomain(detail);
    }

    @Override
    public List<PayableListDetailDo> listPayableListDetail(PayableListDetailQo qo) {
        QueryWrapper<PayableListDetail> queryWrapper = payableListDetailDao.buildQueryWrapper(qo);
        List<PayableListDetail> details = payableListDetailMapper.selectList(queryWrapper);
        return PayableListDetailStructMapper.INSTANCE.dataObjectsToDomains(details);
    }

    @Override
    public PageResult<PayableListDetailDo> pagePayableListDetail(PayableListDetailQo qo) {
        Page<PayableListDetailDo> page = new Page<>(qo.getPageNum(), qo.getPageSize());
        Page<PayableListDetailDo> result = payableListDetailMapper.pageDetailWithListNo(page, qo);
        return PageUtils.transferPage(r -> r, result);
    }

    @Override
    public List<PayableListDetailDo> listByPayableListId(String payableListId) {
        QueryWrapper<PayableListDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PayableListDetail::getPayableListId, payableListId);
        List<PayableListDetail> details = payableListDetailMapper.selectList(queryWrapper);
        return PayableListDetailStructMapper.INSTANCE.dataObjectsToDomains(details);
    }

    @Override
    public int updateBatchByIds(Collection<String> ids, PayableListDetailDo update) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        UpdateWrapper<PayableListDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(PayableListDetail::getId, ids);
        PayableListDetail entity = PayableListDetailStructMapper.INSTANCE.domainToDataObject(update);
        return payableListDetailMapper.update(entity, updateWrapper);
    }

    @Override
    public int deleteById(String id) {
        return payableListDetailMapper.deleteById(id);
    }

    @Override
    public int deleteBatchByIds(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        return payableListDetailMapper.deleteBatchIds(ids);
    }

    @Override
    public PayableListDetailDo getBySoNo(String soNo) {
        QueryWrapper<PayableListDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PayableListDetail::getSoNo, soNo);
        PayableListDetail detail = payableListDetailMapper.selectOne(queryWrapper);
        return PayableListDetailStructMapper.INSTANCE.dataObjectToDomain(detail);
    }

    @Override
    public List<PayableListDetailDo> listBySoNos(Collection<String> soNos) {
        if (CollectionUtils.isEmpty(soNos)) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper<PayableListDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(PayableListDetail::getSoNo, soNos);
        List<PayableListDetail> details = payableListDetailMapper.selectList(queryWrapper);
        return PayableListDetailStructMapper.INSTANCE.dataObjectsToDomains(details);
    }

    @Override
    public List<String> listAllBoundSoIds() {
        QueryWrapper<PayableListDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT so_id");
        List<PayableListDetail> details = payableListDetailMapper.selectList(queryWrapper);
        return details.stream()
                .map(PayableListDetail::getSoId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public int countByPayableListId(String payableListId) {
        QueryWrapper<PayableListDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PayableListDetail::getPayableListId, payableListId);
        return Math.toIntExact(payableListDetailMapper.selectCount(queryWrapper));
    }

    @Override
    public BigDecimal sumAmountByPayableListId(String payableListId) {
        QueryWrapper<PayableListDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("COALESCE(SUM(price), 0) as price")
                .lambda()
                .eq(PayableListDetail::getPayableListId, payableListId);
        PayableListDetail result = payableListDetailMapper.selectOne(queryWrapper);
        return result != null && result.getPrice() != null ? result.getPrice() : BigDecimal.ZERO;
    }

    @Override
    public int updateBillStatusByPayableListId(String payableListId, String billStatus, LocalDateTime submitDate, LocalDateTime confirmTime) {
        PayableListDetailDo redundantFields = new PayableListDetailDo();
        redundantFields.setBillStatus(billStatus);
        redundantFields.setSubmitDate(submitDate);
        redundantFields.setConfirmTime(confirmTime);
        return updateRedundantFieldsByPayableListId(payableListId, redundantFields);
    }

    @Override
    public int updateReceiveStatusById(String id, Integer isReceived, LocalDateTime receiveTime) {
        // 只更新收款维度的两个字段，避免全量回写快照覆盖明细其它列（尤其是冗余列）
        LambdaUpdateWrapper<PayableListDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayableListDetail::getId, id)
                .set(PayableListDetail::getIsReceived, isReceived)
                .set(receiveTime != null, PayableListDetail::getReceiveTime, receiveTime);
        return payableListDetailMapper.update(null, updateWrapper);
    }

    @Override
    public int updateRedundantFieldsByPayableListId(String payableListId, PayableListDetailDo redundantFields) {
        // 只更新本次业务实际变更的字段：入参为 null 表示本次不涉及该字段，明细表对应列保持原值
        if (redundantFields == null || isAllNull(redundantFields)) {
            return 0;
        }
        LambdaUpdateWrapper<PayableListDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayableListDetail::getPayableListId, payableListId)
                .set(redundantFields.getBillStatus() != null, PayableListDetail::getBillStatus, redundantFields.getBillStatus())
                .set(redundantFields.getCustomerCode() != null, PayableListDetail::getCustomerCode, redundantFields.getCustomerCode())
                .set(redundantFields.getCustomerName() != null, PayableListDetail::getCustomerName, redundantFields.getCustomerName())
                .set(redundantFields.getGenerateMonth() != null, PayableListDetail::getGenerateMonth, redundantFields.getGenerateMonth())
                .set(redundantFields.getSubmitDate() != null, PayableListDetail::getSubmitDate, redundantFields.getSubmitDate())
                .set(redundantFields.getConfirmTime() != null, PayableListDetail::getConfirmTime, redundantFields.getConfirmTime());

        return payableListDetailMapper.update(null, updateWrapper);
    }

    @Override
    public int advanceBillStatusByPayableListId(String payableListId, String billStatus) {
        // 仅当目标状态编码大于明细当前状态时才更新，避免明细冗余单据状态回退
        LambdaUpdateWrapper<PayableListDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayableListDetail::getPayableListId, payableListId)
                .lt(PayableListDetail::getBillStatus, billStatus)
                .set(PayableListDetail::getBillStatus, billStatus);
        return payableListDetailMapper.update(null, updateWrapper);
    }

    /**
     * 待同步的冗余字段是否全部为 null（无字段可更新）
     */
    private boolean isAllNull(PayableListDetailDo redundantFields) {
        return redundantFields.getBillStatus() == null && redundantFields.getCustomerCode() == null
                && redundantFields.getCustomerName() == null && redundantFields.getGenerateMonth() == null
                && redundantFields.getSubmitDate() == null && redundantFields.getConfirmTime() == null;
    }
}
