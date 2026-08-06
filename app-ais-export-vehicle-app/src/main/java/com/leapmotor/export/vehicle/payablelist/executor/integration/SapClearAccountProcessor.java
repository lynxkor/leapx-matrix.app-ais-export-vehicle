package com.leapmotor.export.vehicle.payablelist.executor.integration;

import com.leapmotor.export.vehicle.payablelist.dto.LgBindInfoDto;
import com.leapmotor.export.vehicle.payablelist.dto.LgReleaseReceivableReq;
import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountReq;
import com.leapmotor.export.vehicle.payablelist.dto.SoUpdateStatusReq;
import com.leapmotor.export.vehicle.payablelist.enums.IsReceivedEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.LetterGuaranteeGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.SoOrderGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SAP清账结果处理器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class SapClearAccountProcessor {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private SoOrderGateway soOrderGateway;
    private LetterGuaranteeGateway letterGuaranteeGateway;

    @Transactional(rollbackFor = Exception.class)
    public void process(SapClearAccountReq req) {
        for (SapClearAccountReq.SapClearAccountItem item : req.getClearAccountList()) {
            // 按 soNo 查找明细
            PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSoNo());
            if (detail == null) {
                log.warn("SAP清账：SO单号{}不在清单明细中，跳过", item.getSoNo());
                continue;
            }
            // 更新明细收款状态
            detail.setIsReceived(IsReceivedEnum.YES.getCode());
            detail.setReceiveTime(item.getReceiveTime());
            payableListDetailGateway.updatePayableListDetail(detail);

            // 更新 SO 状态为已收款
            SoUpdateStatusReq statusReq = new SoUpdateStatusReq();
            statusReq.setSoId(detail.getSoId());
            statusReq.setReceiveTime(item.getReceiveTime());
            soOrderGateway.updateStatusReceived(statusReq);

            // 查询保函信息，释放应收
            List<LgBindInfoDto> lgList = letterGuaranteeGateway.queryBySoNo(item.getSoNo());
            for (LgBindInfoDto lg : lgList) {
                LgReleaseReceivableReq releaseReq = new LgReleaseReceivableReq();
                releaseReq.setSoNo(item.getSoNo());
                // releaseReq.setLetterGuaranteeId(lg.getLetterGuaranteeId());
                // releaseReq.setAmount(lg.getReceivableAmount());
                // releaseReq.setVin(item.getVin());
                // releaseReq.setPoNo(detail.getPoNo());
                letterGuaranteeGateway.releaseReceivable(releaseReq);
            }

            log.info("SAP清账处理完成：soNo={}, vin={}", item.getSoNo(), item.getVin());
        }

        // 更新抬头状态
        updateHeaderStatus(req);
    }

    private void updateHeaderStatus(SapClearAccountReq req) {
        // 根据 clearAccountList 中的任意 soNo 找到关联抬头
        for (SapClearAccountReq.SapClearAccountItem item : req.getClearAccountList()) {
            PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSoNo());
            if (detail == null) {
                continue;
            }
            PayableListDo header = payableListGateway.getPayableListById(detail.getPayableListId());
            if (header == null) {
                continue;
            }
            // 统计该抬头下全部明细的收款情况
            List<PayableListDetailDo> allDetails = payableListDetailGateway.listByPayableListId(header.getId());
            long receivedCount = allDetails.stream()
                    .filter(d -> IsReceivedEnum.YES.getCode().equals(d.getIsReceived()))
                    .count();

            if (receivedCount == allDetails.size()) {
                header.setBillStatus(PayableListStatusEnum.RECEIVED.getCode());
            } else if (receivedCount > 0) {
                header.setBillStatus(PayableListStatusEnum.PARTIAL_RECEIVED.getCode());
            }
            payableListGateway.updatePayableList(header);
            log.info("抬头状态更新：payableListId={}, billStatus={}", header.getId(), header.getBillStatus());
            break;
        }
    }
}
