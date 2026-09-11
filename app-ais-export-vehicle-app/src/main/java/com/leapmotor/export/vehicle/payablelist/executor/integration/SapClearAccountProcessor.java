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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * 允许被清账推进收款状态的抬头状态：仅已确认(30)/部分收款(50)/已收款(60)。
     *
     * <p>草稿(10)/已提交待确认(20)尚未确认收款，驳回(40)是分支态而非进度态，均不允许被清账推进，
     * 避免状态编码比较（40 < 50/60）把驳回态直接推成部分/已收款而绕过状态机。
     */
    private static final Set<String> PROMOTABLE_HEADER_STATUS = Set.of(
            PayableListStatusEnum.CONFIRMED.getCode(),
            PayableListStatusEnum.PARTIAL_RECEIVED.getCode(),
            PayableListStatusEnum.RECEIVED.getCode());

    public void process(SapClearAccountReq req) {
        for (SapClearAccountReq.SapClearAccountItem item : req.getClearAccountList()) {
            // 按 SO 维度加分布式锁：同一SO的并发清账串行化，锁释放晚于事务提交
            String lockKey = "sap:clear:" + item.getSorder();
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
     *
     * 幂等粒度：应收释放按 (lgId, poNo) 逐条比对已释放流水，已释放的跳过、未释放的补齐，
     * 避免"部分释放成功后整单被跳过"导致应收永久残留；任一释放失败即抛异常回滚整个 SO 事务。
     */
    private void processOne(SapClearAccountReq.SapClearAccountItem item) {
        // 按 soNo 查找明细
        PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSorder());
        if (detail == null) {
            log.warn("SAP清账：SO单号{}不在清单明细中，跳过", item.getSorder());
            return;
        }
        // 已释放流水集合（(lgId, poNo) 维度），用于逐条幂等比对
        Set<String> releasedKeys = listReleasedKeys(item.getSorder());

        // 更新明细收款状态与收款时间（只写收款字段，重复执行写入相同值）
        payableListDetailGateway.updateReceiveStatusById(detail.getId(), IsReceivedEnum.YES.getCode(), item.getAugdt());
        // 更新 SO 状态为已收款
        VehicleCbuSoDo soDo = vehicleCbuSoGateway.getVehicleCbuSoById(detail.getSoId());
        if (soDo != null) {
            soDo.setOrderStatus(VehicleCbuSoStatusEnum.RECEIVED.getCode());
            soDo.setReceiveTime(item.getAugdt());
            vehicleCbuSoGateway.updateVehicleCbuSo(soDo);
        } else {
            log.warn("SAP清账：SO单号{}对应的SO记录不存在，soId={}", item.getSorder(), detail.getSoId());

        }

        // 查询该SO对应的保函转应收记录
        ExportVehicleLetterGuaranteeRecordQo recordQo = new ExportVehicleLetterGuaranteeRecordQo();
        recordQo.setSoNo(item.getSorder());
        recordQo.setTransactionType(RecordTypeEnum.TRANSFER_RECEIVABLE.getCode());
        List<ExportVehicleLetterGuaranteeRecordDo> lgRecords = letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(recordQo);

        // 遍历每条转应收记录，执行应收释放（按 (lgId, poNo) 幂等，已释放的跳过）
        for (ExportVehicleLetterGuaranteeRecordDo record : lgRecords) {
            if (releasedKeys.contains(buildReleaseKey(record.getLgId(), record.getPoNo()))) {
                log.info("保函应收已释放，幂等跳过：lgId={}, poNo={}, soNo={}",
                        record.getLgId(), record.getPoNo(), item.getSorder());
                continue;
            }
            boolean success = letterGuaranteeGateway.releaseReceivable(
                    record.getLgId(),
                    record.getTransactionAmount(),
                    detail.getSoId(),
                    record.getPoNo(),
                    item.getSorder(),
                    receiveReason,
                    item.getVhvin()
            );
            if (!success) {
                // 释放失败必须中断并回滚，否则会留下"部分释放"状态并可能被后续幂等短路跳过
                throw new BizException("保函应收释放失败，需人工核查：soNo=" + item.getSorder()
                        + ", lgId=" + record.getLgId() + ", poNo=" + record.getPoNo());
            }
            // 回写 PO 关联保函表：占用不变，累计应收减少
            reducePoReceivable(record.getPoNo(), record.getLgId(), record.getTransactionAmount());
            log.info("保函应收释放成功：lgId={}, soNo={}, amount={}",
                record.getLgId(), item.getSorder(), record.getTransactionAmount());
        }

        log.info("SAP清账处理完成：soNo={}, vin={}", item.getSorder(), item.getVhvin());
    }

    /**
     * 查询该 SO 已释放的应收流水，构建 (lgId, poNo) 幂等键集合
     */
    private Set<String> listReleasedKeys(String soNo) {
        ExportVehicleLetterGuaranteeRecordQo releasedQo = new ExportVehicleLetterGuaranteeRecordQo();
        releasedQo.setSoNo(soNo);
        releasedQo.setTransactionType(RecordTypeEnum.RELEASE_RECEIVABLE.getCode());
        List<ExportVehicleLetterGuaranteeRecordDo> releasedRecords =
                letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(releasedQo);
        if (releasedRecords == null || releasedRecords.isEmpty()) {
            return Collections.emptySet();
        }
        return releasedRecords.stream()
                .map(record -> buildReleaseKey(record.getLgId(), record.getPoNo()))
                .collect(Collectors.toSet());
    }

    /**
     * 应收释放幂等键：保函ID + PO单号
     */
    private String buildReleaseKey(String lgId, String poNo) {
        return lgId + "#" + poNo;
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
            PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSorder());
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
            // 前置校验：仅已确认/部分收款/已收款状态允许推进，驳回等分支态不推进状态，避免绕过状态机
            if (!PROMOTABLE_HEADER_STATUS.contains(header.getBillStatus())) {
                log.warn("SAP清账：抬头状态{}不允许推进收款状态，跳过状态更新：payableListId={}",
                        header.getBillStatus(), header.getId());
                continue;
            }
            // 统计该抬头下全部明细的收款情况
            List<PayableListDetailDo> allDetails = payableListDetailGateway.listByPayableListId(header.getId());
            if (allDetails.isEmpty()) {
                // 抬头下无明细时不统计，避免 0==0 被误判为"全部收款"
                continue;
            }
            long receivedCount = allDetails.stream()
                    .filter(d -> IsReceivedEnum.YES.getCode().equals(d.getIsReceived()))
                    .count();

            String targetStatus = null;
            if (receivedCount == allDetails.size()) {
                targetStatus = PayableListStatusEnum.RECEIVED.getCode();
            } else if (receivedCount > 0) {
                targetStatus = PayableListStatusEnum.PARTIAL_RECEIVED.getCode();
            }
            if (targetStatus == null) {
                log.info("抬头状态无需变更（无收款）：payableListId={}", header.getId());
                continue;
            }
            // 状态更新：并发下已收款(60)不会被后算出的部分收款(50)覆盖
            payableListGateway.advanceBillStatus(header.getId(), targetStatus);
            // 同步明细冗余单据状态
            payableListDetailGateway.advanceBillStatusByPayableListId(header.getId(), targetStatus);
            log.info("抬头状态更新：payableListId={}, targetStatus={}", header.getId(), targetStatus);
        }
    }
}
