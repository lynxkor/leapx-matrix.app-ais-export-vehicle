package com.leapmotor.export.vehicle.payablelist.executor.integration;

import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.letterGuarantee.enums.RecordTypeEnum;
import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountReq;
import com.leapmotor.export.vehicle.payablelist.enums.IsReceivedEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.platform.ais.common.redis.BlockLock;
import com.leapmotor.platform.ais.common.redis.DistributedLockTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private VehicleCbuSoGateway vehicleCbuSoGateway;
    private ExportVehicleLetterGuaranteeRecordGateway letterGuaranteeRecordGateway;
    private ExportVehicleLetterGuaranteeGateway letterGuaranteeGateway;
    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;
    private DistributedLockTemplate distributedLockTemplate;
    private TransactionTemplate transactionTemplate;

    private static final String receiveReason = "SAP清账，自动释放应收";

    public void process(SapClearAccountReq req) {
        for (SapClearAccountReq.SapClearAccountItem item : req.getClearAccountList()) {
            // 按 SO 维度加分布式锁：同一SO的并发清账串行化，锁释放晚于事务提交
            String lockKey = "sap:clear:" + item.getSoNo();
            BlockLock lock;
            try {
                lock = distributedLockTemplate.blockLock(lockKey, 30000, 60000);
            } catch (Exception e) {
                throw new BizException("SAP清账获取锁失败，请稍后重试：" + e.getMessage());
            }
            try {
                transactionTemplate.execute(status -> {
                    processOne(item);
                    return null;
                });
            } finally {
                closeLock(lock, lockKey);
            }
        }

        // 更新抬头状态（幂等操作，不涉及资金扣减，无需加锁）
        transactionTemplate.execute(status -> {
            updateHeaderStatus(req);
            return null;
        });
    }

    /**
     * 单条 SO 清账处理（独立事务，锁内执行）
     */
    private void processOne(SapClearAccountReq.SapClearAccountItem item) {
        // 幂等检查：该 SO 已有应收释放流水则跳过，防止 SAP 重复推送导致保函应收/PO累计应收重复扣减
        ExportVehicleLetterGuaranteeRecordQo releasedQo = new ExportVehicleLetterGuaranteeRecordQo();
        releasedQo.setSoNo(item.getSoNo());
        releasedQo.setTransactionType(RecordTypeEnum.RELEASE_RECEIVABLE.getCode());
        List<ExportVehicleLetterGuaranteeRecordDo> releasedRecords =
                letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(releasedQo);
        if (releasedRecords != null && !releasedRecords.isEmpty()) {
            log.warn("SAP清账：SO单号{}已存在应收释放流水，跳过幂等处理", item.getSoNo());
            return;
        }
        // 按 soNo 查找明细
        PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSoNo());
        if (detail == null) {
            log.warn("SAP清账：SO单号{}不在清单明细中，跳过", item.getSoNo());
            return;
        }
        // 更新明细收款状态
        detail.setIsReceived(IsReceivedEnum.YES.getCode());
        detail.setReceiveTime(item.getReceiveTime());
        payableListDetailGateway.updatePayableListDetail(detail);

        // 更新 SO 状态为已收款
        VehicleCbuSoDo soDo = vehicleCbuSoGateway.getVehicleCbuSoById(detail.getSoId());
        if (soDo != null) {
            soDo.setOrderStatus(VehicleCbuSoStatusEnum.RECEIVED.getCode());
            soDo.setReceiveTime(item.getReceiveTime());
            vehicleCbuSoGateway.updateVehicleCbuSo(soDo);
        } else {
            log.warn("SAP清账：SO单号{}对应的SO主表不存在，soId={}", item.getSoNo(), detail.getSoId());
        }

        // 查询保函信息，释放应收
        // 查询该SO对应的保函转应收记录
        ExportVehicleLetterGuaranteeRecordQo recordQo = new ExportVehicleLetterGuaranteeRecordQo();
        recordQo.setSoNo(item.getSoNo());
        recordQo.setTransactionType(RecordTypeEnum.TRANSFER_RECEIVABLE.getCode());
        List<ExportVehicleLetterGuaranteeRecordDo> lgRecords = letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(recordQo);

        // 遍历每条转应收记录，执行应收释放
        for (ExportVehicleLetterGuaranteeRecordDo record : lgRecords) {
            boolean success = letterGuaranteeGateway.releaseReceivable(
                    record.getLgId(),
                    record.getTransactionAmount(),
                    detail.getSoId(),
                    record.getPoNo(),
                    item.getSoNo(),
                    receiveReason,
                    item.getVin()
            );

            if (success) {
                // 回写 PO 关联保函表：占用不变，累计应收减少
                reducePoReceivable(record.getPoNo(), record.getLgId(), record.getTransactionAmount());
                log.info("保函应收释放成功：lgId={}, soNo={}, amount={}",
                    record.getLgId(), item.getSoNo(), record.getTransactionAmount());
            } else {
                log.warn("保函应收释放失败：lgId={}, soNo={}", record.getLgId(), item.getSoNo());
            }
        }

        log.info("SAP清账处理完成：soNo={}, vin={}", item.getSoNo(), item.getVin());
    }

    /**
     * 释放分布式锁（参考 VehicleCbuSoCmdExe#closeLock）
     */
    private void closeLock(BlockLock lock, String lockKey) {
        if (lock == null) {
            return;
        }
        try {
            lock.close();
        } catch (Exception e) {
            log.warn("释放锁异常, lockKey={}, 可能已提前过期, msg={}", lockKey, e.getMessage());
        }
    }

    /**
     * 回写 PO 关联保函表：poCurrentOccupiedAmount 不变，poCurrentReceivableAmount -= amount。
     */
    private void reducePoReceivable(String poNo, String lgId, BigDecimal amount) {
        ExportVehicleCbuPoGuaranteeQo qo = new ExportVehicleCbuPoGuaranteeQo();
        qo.setPoNo(poNo);
        List<ExportVehicleCbuPoGuaranteeDo> list = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(qo);
        if (list == null) {
            log.warn("PO关联保函回写：PO单号{}无关联保函记录，跳过", poNo);
            return;
        }
        for (ExportVehicleCbuPoGuaranteeDo g : list) {
            if (lgId.equals(g.getGuaranteeId())) {
                BigDecimal receivable = g.getPoCurrentReceivableAmount() == null
                        ? BigDecimal.ZERO : g.getPoCurrentReceivableAmount();
                if (receivable.compareTo(amount) < 0) {
                    throw new BizException("PO关联保函应收不足：po单号=" + poNo + ", 当前累计应收=" + receivable + ", 需释放=" + amount);
                }
                ExportVehicleCbuPoGuaranteeDo update = new ExportVehicleCbuPoGuaranteeDo();
                update.setId(g.getId());
                update.setPoCurrentReceivableAmount(receivable.subtract(amount));
                purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(update);
                log.info("PO关联保函回写：poNo={}, lgId={}, poCurrentReceivableAmount {} -> {}",
                        poNo, lgId, receivable, update.getPoCurrentReceivableAmount());
                break;
            }
        }
    }

    private void updateHeaderStatus(SapClearAccountReq req) {
        // 同一请求可能涉及多个应付清单抬头，逐个统计收款情况并更新（同一抬头下多条明细去重）
        Set<String> processedHeaderIds = new HashSet<>();
        for (SapClearAccountReq.SapClearAccountItem item : req.getClearAccountList()) {
            PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSoNo());
            if (detail == null) {
                continue;
            }
            if (!processedHeaderIds.add(detail.getPayableListId())) {
                // 该抬头已处理过（同抬头下多条明细），跳过
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
        }
    }
}
