package com.leapmotor.export.vehicle.infrastructure.payablelist.gatewayimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.infrastructure.payablelist.convertor.PayableListStructMapper;
import com.leapmotor.export.vehicle.infrastructure.payablelist.dao.PayableListDao;
import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableList;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.infrastructure.payablelist.mapper.PayableListMapper;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListQo;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 应付清单抬头领域网关实现
 *
 * @author 赵吴涯
 * @since 2026-07-20
 */
@AllArgsConstructor
@Service
public class PayableListGatewayImpl implements PayableListGateway {

    private PayableListMapper payableListMapper;
    private PayableListDao payableListDao;

    @Override
    public String addPayableList(PayableListDo payableListDo) {
        PayableList payableList = PayableListStructMapper.INSTANCE.domainToDataObject(payableListDo);
        int result = payableListMapper.insert(payableList);
        payableListDo.setId(payableList.getId());
        if (result > 0) {
            return payableList.getId();
        } else {
            return null;
        }
    }

    @Override
    public int addBatchPayableList(List<PayableListDo> payableListDos) {
        List<PayableList> payableLists = PayableListStructMapper.INSTANCE.domainToDataObjects(payableListDos);
        return payableListDao.batchSaveSomeColumn(payableLists);
    }

    @Override
    public String updatePayableList(PayableListDo payableListDo) {
        PayableList payableList = PayableListStructMapper.INSTANCE.domainToDataObject(payableListDo);
        int result = payableListMapper.updateById(payableList);
        if (result > 0) {
            return payableList.getId();
        } else {
            return null;
        }
    }

    @Override
    public PayableListDo getPayableListById(String id) {
        PayableList payableList = payableListMapper.selectById(id);
        return PayableListStructMapper.INSTANCE.dataObjectToDomain(payableList);
    }

    @Override
    public PayableListDo getPayableListByNo(String payableListNo) {
        QueryWrapper<PayableList> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PayableList::getPayableListNo, payableListNo);
        PayableList payableList = payableListMapper.selectOne(queryWrapper);
        return PayableListStructMapper.INSTANCE.dataObjectToDomain(payableList);
    }

    @Override
    public PayableListDo getOnePayableList(PayableListQo payableListQo) {
        QueryWrapper<PayableList> queryWrapper = payableListDao.buildQueryWrapper(payableListQo);
        queryWrapper.last("LIMIT 1");
        PayableList payableList = payableListMapper.selectOne(queryWrapper);
        return PayableListStructMapper.INSTANCE.dataObjectToDomain(payableList);
    }

    @Override
    public List<PayableListDo> listPayableList(PayableListQo payableListQo) {
        QueryWrapper<PayableList> queryWrapper = payableListDao.buildQueryWrapper(payableListQo);
        List<PayableList> payableLists = payableListMapper.selectList(queryWrapper);
        return PayableListStructMapper.INSTANCE.dataObjectsToDomains(payableLists);
    }

    @Override
    public PageResult<PayableListDo> pagePayableList(PayableListQo payableListQo) {
        Page<PayableList> page = new Page<>(payableListQo.getPageNum(), payableListQo.getPageSize());
        QueryWrapper<PayableList> queryWrapper = payableListDao.buildQueryWrapper(payableListQo);
        Page<PayableList> result = payableListMapper.selectPage(page, queryWrapper);
        return PageUtils.transferPage(PayableListStructMapper.INSTANCE::dataObjectsToDomains, result);
    }

    @Override
    public int updateBatchByIds(Collection<String> ids, PayableListDo update) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        UpdateWrapper<PayableList> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(PayableList::getId, ids);
        PayableList entity = PayableListStructMapper.INSTANCE.domainToDataObject(update);
        return payableListMapper.update(entity, updateWrapper);
    }

    @Override
    public int advanceBillStatus(String payableListId, String billStatus) {
        // 仅当目标状态编码大于当前状态时才更新
        LambdaUpdateWrapper<PayableList> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayableList::getId, payableListId)
                .lt(PayableList::getBillStatus, billStatus)
                .set(PayableList::getBillStatus, billStatus);
        return payableListMapper.update(null, updateWrapper);
    }

    @Override
    public int updateSyncHqResultById(String payableListId, LocalDateTime syncHqTime, String syncHqResult, String syncHqMessage) {
        LambdaUpdateWrapper<PayableList> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayableList::getId, payableListId)
                .set(syncHqTime != null, PayableList::getSyncHqTime, syncHqTime)
                .set(syncHqResult != null, PayableList::getSyncHqResult, syncHqResult)
                .set(syncHqMessage != null, PayableList::getSyncHqMessage, syncHqMessage);
        return payableListMapper.update(null, updateWrapper);
    }

    @Override
    public int updateSyncSapResultById(String payableListId, LocalDateTime syncSapTime, String syncSapResult, String syncSapMessage) {
        // 只写同步结果三列，避免查询快照全量回写把并发变更的单据状态、版本号等字段覆盖回旧值
        LambdaUpdateWrapper<PayableList> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayableList::getId, payableListId)
                .set(syncSapTime != null, PayableList::getSyncSapTime, syncSapTime)
                .set(syncSapResult != null, PayableList::getSyncSapResult, syncSapResult)
                .set(syncSapMessage != null, PayableList::getSyncSapMessage, syncSapMessage);
        return payableListMapper.update(null, updateWrapper);
    }

}
