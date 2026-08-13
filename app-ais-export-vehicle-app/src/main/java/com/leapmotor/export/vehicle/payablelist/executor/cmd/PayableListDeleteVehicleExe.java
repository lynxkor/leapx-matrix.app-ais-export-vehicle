package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListDeleteVehicleReq;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 应付清单删除车辆执行器
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListDeleteVehicleExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;

    @Transactional(rollbackFor = Exception.class)
    public String execute(PayableListDeleteVehicleReq req) {
        // 查询应付清单
        PayableListDo payableList = payableListGateway.getPayableListById(req.getPayableListId());
        if (payableList == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }

        // 校验状态（只有草稿或驳回状态可以删除）
        PayableListStatusChecker.checkCanEdit(payableList);

        // 校验版本号（乐观锁）
        PayableListStatusChecker.checkVersion(payableList, req.getVersion());

        // 校验明细是否存在
        List<PayableListDetailDo> detailList = payableListDetailGateway.listByPayableListId(req.getPayableListId());
        if (detailList.isEmpty()) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单明细不存在");
        }

        // 校验要删除的明细ID是否属于该应付清单
        for (String detailId : req.getDetailIds()) {
            boolean exists = detailList.stream()
                    .anyMatch(detail -> detail.getId().equals(detailId));
            if (!exists) {
                throw new BizException(ResultEnum.BAD_REQUEST, "明细ID[" + detailId + "]不属于该应付清单");
            }
        }

        // 批量删除明细
        payableListDetailGateway.deleteBatchByIds(req.getDetailIds());

        // 重新计算统计信息并更新版本号
        updatePayableListStatistics(req.getPayableListId(), payableList);

        log.info("应付清单删除车辆成功，payableListId={}, deletedCount={}, totalVehicles={}, totalAmount={}",
                req.getPayableListId(), req.getDetailIds().size(), payableList.getTotalVehicles(), payableList.getTotalAmount());

        return req.getPayableListId();
    }

    /**
     * 更新应付清单统计信息
     */
    private void updatePayableListStatistics(String payableListId, PayableListDo payableList) {
        // 统计车辆数量
        int totalVehicles = payableListDetailGateway.countByPayableListId(payableListId);

        // 统计金额合计
        BigDecimal totalAmount = payableListDetailGateway.sumAmountByPayableListId(payableListId);
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        // 更新应付清单
        payableList.setTotalVehicles(totalVehicles);
        payableList.setTotalAmount(totalAmount);
        payableList.setVersion(payableList.getVersion() + 1);
        payableListGateway.updatePayableList(payableList);
    }
}
