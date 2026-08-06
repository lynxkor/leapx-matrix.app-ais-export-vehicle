package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListRemoveVehiclesReq;
import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 删除车辆明细执行器（软删除）
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListRemoveVehiclesExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;

    @Transactional(rollbackFor = Exception.class)
    public String execute(PayableListRemoveVehiclesReq req) {
        PayableListDo db = payableListGateway.getPayableListById(req.getPayableListId());
        if (db == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }
        // 乐观锁校验
        if (db.getVersion() == null || !db.getVersion().equals(req.getVersion())) {
            throw new BizException(ResultEnum.BAD_REQUEST, "数据已被他人修改，请刷新后重试");
        }
        // 状态前置校验
        PayableListStatusChecker.checkEditable(db);

        // 软删除明细
        for (String detailId : req.getDetailIds()) {
            payableListDetailGateway.deleteById(detailId);
        }

        // 重算汇总（查询剩余活动明细）
        List<PayableListDetailDo> remainDetails = payableListDetailGateway.listByPayableListId(db.getId());
        int totalVehicles = remainDetails.size();
        BigDecimal totalAmount = remainDetails.stream()
                .map(PayableListDetailDo::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        db.setTotalVehicles(totalVehicles);
        db.setTotalAmount(totalAmount);
        db.setVersion(db.getVersion() + 1);
        payableListGateway.updatePayableList(db);

        log.info("删除车辆明细成功，payableListId={}, count={}", db.getId(), req.getDetailIds().size());
        return db.getId();
    }
}
