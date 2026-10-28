package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 同步SAP执行器（已确认后主动推送，事件驱动重试场景）
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListSyncToSapExe {

    private PayableListGateway payableListGateway;
    private EventHandlerHelper eventHandlerHelper;

    @Transactional(rollbackFor = Exception.class)
    public boolean execute(String payableListId) {
        PayableListDo db = payableListGateway.getPayableListById(payableListId);
        if (db == null) {
            throw new BizException("应付清单不存在");
        }
        // 仅已确认状态可推送 SAP
        if (!PayableListStatusEnum.CONFIRMED.getCode().equals(db.getBillStatus())) {
            throw new BizException("仅已确认状态的单据可推送SAP");
        }

        // 发布 SAP 同步事件（异步，事务提交后执行）
        eventHandlerHelper.publishAsync(
            ThirdTargetPlatformEnum.SAP,
            SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT,
            db.getPayableListNo(),
            (Object) null
        );

        log.info("推送SAP事件已发布，payableListId={}", payableListId);
        return true;
    }
}
