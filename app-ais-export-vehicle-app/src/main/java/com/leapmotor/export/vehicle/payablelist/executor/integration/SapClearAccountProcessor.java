package com.leapmotor.export.vehicle.payablelist.executor.integration;

import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.letterGuarantee.enums.RecordTypeEnum;
import com.leapmotor.export.vehicle.payablelist.dto.SapClearAccountItem;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    /**
     * 处理 SAP 清账回调。
     *
     * <p>先对整批记录做全量预校验：逐条检查入参与数据一致性，把所有错误聚合到一个错误原因抛出，
     * 任一条不合格即整批拒绝，不获取锁、不产生任何写入；预校验通过后逐条在分布式锁 + 独立事务中
     * 执行清账资金动作，最后统一更新应付清单抬头收款状态。
     */
    public void process(List<SapClearAccountItem> clearAccountList) {
        if (clearAccountList == null || clearAccountList.isEmpty()) {
            throw new BizException("清账车辆列表不能为空");
        }
        // 全量预校验：所有错误聚合返回；任一条不合格即整体拒绝，不进入锁与事务
        validateAll(clearAccountList);

        for (SapClearAccountItem item : clearAccountList) {
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
            updateHeaderStatus(clearAccountList);
            return null;
        });
    }

    /**
     * 单条 SO 清账处理（独立事务，锁内执行）
     *
     * 幂等粒度：应收释放按 (lgId, poNo) 逐条比对已释放流水，已释放的跳过、未释放的补齐，
     * 避免"部分释放成功后整单被跳过"导致应收永久残留；任一释放失败即抛异常回滚整个 SO 事务。
     *
     * 参数与数据不匹配（sorder/paynr 缺失、SO 不在明细中、paynr 与明细所属清单不一致、
     * SO 记录缺失、PO 关联保函缺失）一律抛业务异常，不再静默跳过，便于 SAP 侧感知并重推；
     * 这些静态校验已在 {@link #validateAll} 中按整批预校验，此处保留作为并发场景下的运行期兜底。
     */
    private void processOne(SapClearAccountItem item) {
        // 入参校验：sorder/paynr 是定位与校验的关键字段，缺失时明确报错而非静默跳过
        if (StringUtils.isBlank(item.getSorder())) {
            throw new BizException("sorder不能为空");
        }
        if (StringUtils.isBlank(item.getPaynr())) {
            throw new BizException("paynr不能为空");
        }
        // 按 sorder 查找明细
        PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSorder());
        if (detail == null) {
            throw new BizException("sorder=" + item.getSorder() + " 不在应付清单明细中");
        }
        // 校验明细所属应付清单编号与回调传入的 paynr 一致，防止跨清单误清账
        PayableListDo header = payableListGateway.getPayableListById(detail.getPayableListId());
        if (header == null) {
            throw new BizException("sorder=" + item.getSorder()
                    + " 对应的应付清单不存在（payableListId=" + detail.getPayableListId() + "）");
        }
        if (!item.getPaynr().equals(header.getPayableListNo())) {
            throw new BizException("应付清单编号不一致：sorder=" + item.getSorder()
                    + ", paynr=" + item.getPaynr() + ", 明细所属清单=" + header.getPayableListNo());
        }
        // 已释放流水集合（(lgId, poNo) 维度），用于逐条幂等比对
        Set<String> releasedKeys = listReleasedKeys(item.getSorder());

        // 更新明细收款状态与收款时间（只写收款字段，重复执行写入相同值）
        payableListDetailGateway.updateReceiveStatusById(detail.getId(), IsReceivedEnum.YES.getCode(), item.getAugdt());
        // 更新 SO 状态为已收款
        VehicleCbuSoDo soDo = vehicleCbuSoGateway.getVehicleCbuSoById(detail.getSoId());
        if (soDo == null) {
            throw new BizException("sorder=" + item.getSorder() + " 的SO记录不存在（soId=" + detail.getSoId() + "）");
        }
        soDo.setOrderStatus(VehicleCbuSoStatusEnum.RECEIVED.getCode());
        soDo.setReceiveTime(item.getAugdt());
        vehicleCbuSoGateway.updateVehicleCbuSo(soDo);

        // 查询该SO对应的保函转应收记录
        List<ExportVehicleLetterGuaranteeRecordDo> lgRecords = listTransferRecords(item.getSorder());

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
                throw new BizException("保函应收释放失败，需人工核查：sorder=" + item.getSorder()
                        + ", porder=" + record.getPoNo() + ", lgId=" + record.getLgId());
            }
            // 回写 PO 关联保函表：占用不变，累计应收减少
            reducePoReceivable(record.getPoNo(), record.getLgId(), record.getTransactionAmount());
            log.info("保函应收释放成功：lgId={}, soNo={}, amount={}",
                record.getLgId(), item.getSorder(), record.getTransactionAmount());
        }

        log.info("SAP清账处理完成：soNo={}, vin={}", item.getSorder(), item.getVhvin());
    }

    /**
     * 全量预校验：逐条校验入参与数据一致性，所有错误聚合后一次性抛出，实现"一条不合格即整批拒绝"。
     *
     * 只读检查，不获取锁、不产生任何写入；应收充足性按 (poNo, lgId) 汇总本批次待释放金额判断，
     * 避免同一保函被多条 SO 占用时只看单条而漏判；同一请求内 sorder 重复时按重复报错，
     * 且该条不再参与后续校验（避免同一保函待释放金额被重复累加）。
     */
    private void validateAll(List<SapClearAccountItem> items) {
        List<String> errors = new ArrayList<>();
        // PO关联保函当前累计应收（null 表示已判定缺失/未匹配并已记录错误）
        Map<String, BigDecimal> poReceivableCache = new HashMap<>();
        // 本批次待释放金额累计
        Map<String, BigDecimal> batchReleaseAmount = new HashMap<>();
        // 已记录"应收不足"的保函键，避免同一保函重复报错
        Set<String> insufficientReported = new HashSet<>();
        // 首次出现的下标，用于同一请求内重复 sorder 的报错定位
        Map<String, Integer> sorderFirstIndex = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            SapClearAccountItem item = items.get(i);
            String position = "第" + (i + 1) + "条";
            if (item == null) {
                errors.add(position + "：记录不能为空");
                continue;
            }
            // 字段缺失时无法定位数据，仅收集字段错误
            boolean sorderBlank = StringUtils.isBlank(item.getSorder());
            boolean paynrBlank = StringUtils.isBlank(item.getPaynr());
            if (sorderBlank || paynrBlank) {
                if (sorderBlank) {
                    errors.add(position + "：sorder不能为空");
                }
                if (paynrBlank) {
                    errors.add(position + "：paynr不能为空");
                }
                continue;
            }
            // 同一请求内 sorder 重复属报文异常：报重复并跳过该条后续校验，
            // 否则同一保函的待释放金额会被重复累加，误报"应收不足"
            Integer firstIndex = sorderFirstIndex.putIfAbsent(item.getSorder(), i);
            if (firstIndex != null) {
                errors.add(position + "：sorder=" + item.getSorder() + " 与第" + (firstIndex + 1) + "条重复");
                continue;
            }
            PayableListDetailDo detail = payableListDetailGateway.getBySoNo(item.getSorder());
            if (detail == null) {
                errors.add(position + "：sorder=" + item.getSorder() + " 不在应付清单明细中");
                continue;
            }
            PayableListDo header = payableListGateway.getPayableListById(detail.getPayableListId());
            if (header == null) {
                errors.add(position + "：sorder=" + item.getSorder()
                        + " 对应的应付清单不存在（payableListId=" + detail.getPayableListId() + "）");
                continue;
            }
            if (!item.getPaynr().equals(header.getPayableListNo())) {
                errors.add(position + "：应付清单编号不一致：sorder=" + item.getSorder()
                        + ", paynr=" + item.getPaynr() + ", 明细所属清单=" + header.getPayableListNo());
                continue;
            }
            if (vehicleCbuSoGateway.getVehicleCbuSoById(detail.getSoId()) == null) {
                errors.add(position + "：sorder=" + item.getSorder() + " 的SO记录不存在（soId="
                        + detail.getSoId() + "）");
                continue;
            }
            // 保函应收：校验 PO 关联保函记录存在且本批次累计释放金额不超过当前累计应收
            validateReceivable(item, position, errors, poReceivableCache, batchReleaseAmount, insufficientReported);
        }
        if (!errors.isEmpty()) {
            throw new BizException("清账请求校验失败（共" + errors.size() + "项）：" + String.join("；", errors));
        }
    }

    /**
     * 校验单条记录涉及的保函应收：PO 关联保函记录必须存在、能匹配到对应保函，
     * 且本批次内该保函累计待释放金额不超过其当前累计应收。
     */
    private void validateReceivable(SapClearAccountItem item, String position, List<String> errors,
                                    Map<String, BigDecimal> poReceivableCache, Map<String, BigDecimal> batchReleaseAmount,
                                    Set<String> insufficientReported) {
        Set<String> releasedKeys = listReleasedKeys(item.getSorder());
        for (ExportVehicleLetterGuaranteeRecordDo record : listTransferRecords(item.getSorder())) {
            String key = buildReleaseKey(record.getLgId(), record.getPoNo());
            if (releasedKeys.contains(key)) {
                // 已释放的幂等记录不参与本批次金额累计
                continue;
            }
            BigDecimal receivable = poReceivableCache.get(key);
            if (receivable == null) {
                if (poReceivableCache.containsKey(key)) {
                    // 该保函已判定为缺失/未匹配并记录错误，不重复报错
                    continue;
                }
                receivable = findPoCurrentReceivable(item, record, position, errors);
                poReceivableCache.put(key, receivable);
                if (receivable == null) {
                    continue;
                }
            }
            BigDecimal amount = record.getTransactionAmount() == null ? BigDecimal.ZERO : record.getTransactionAmount();
            BigDecimal accumulated = batchReleaseAmount.merge(key, amount, BigDecimal::add);
            if (accumulated.compareTo(receivable) > 0 && insufficientReported.add(key)) {
                errors.add(position + "：PO关联保函应收不足：sorder=" + item.getSorder()
                        + ", porder=" + record.getPoNo() + ", 当前累计应收=" + receivable
                        + ", 本批次需释放=" + accumulated + ", lgId=" + record.getLgId());
            }
        }
    }

    /**
     * 查找 PO 关联保函当前累计应收；记录不存在或未匹配到对应保函时记录错误并返回 null
     */
    private BigDecimal findPoCurrentReceivable(SapClearAccountItem item,
                                               ExportVehicleLetterGuaranteeRecordDo record,
                                               String position, List<String> errors) {
        ExportVehicleCbuPoGuaranteeQo qo = new ExportVehicleCbuPoGuaranteeQo();
        qo.setPoNo(record.getPoNo());
        List<ExportVehicleCbuPoGuaranteeDo> list = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(qo);
        if (list == null || list.isEmpty()) {
            errors.add(position + "：PO关联保函记录不存在：sorder=" + item.getSorder()
                    + ", porder=" + record.getPoNo() + ", lgId=" + record.getLgId());
            return null;
        }
        for (ExportVehicleCbuPoGuaranteeDo g : list) {
            if (record.getLgId().equals(g.getGuaranteeId())) {
                return g.getPoCurrentReceivableAmount() == null ? BigDecimal.ZERO : g.getPoCurrentReceivableAmount();
            }
        }
        errors.add(position + "：PO关联保函记录未匹配：sorder=" + item.getSorder()
                + ", porder=" + record.getPoNo() + ", lgId=" + record.getLgId());
        return null;
    }

    /**
     * 查询该 SO 对应的保函转应收记录（查询结果为空时返回空列表，便于调用方直接遍历）
     */
    private List<ExportVehicleLetterGuaranteeRecordDo> listTransferRecords(String soNo) {
        ExportVehicleLetterGuaranteeRecordQo qo = new ExportVehicleLetterGuaranteeRecordQo();
        qo.setSoNo(soNo);
        qo.setTransactionType(RecordTypeEnum.TRANSFER_RECEIVABLE.getCode());
        List<ExportVehicleLetterGuaranteeRecordDo> records =
                letterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(qo);
        return records == null ? Collections.emptyList() : records;
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
        if (list == null || list.isEmpty()) {
            throw new BizException("PO关联保函记录不存在：porder=" + poNo + ", lgId=" + lgId);
        }
        for (ExportVehicleCbuPoGuaranteeDo g : list) {
            if (lgId.equals(g.getGuaranteeId())) {
                BigDecimal receivable = g.getPoCurrentReceivableAmount() == null
                        ? BigDecimal.ZERO : g.getPoCurrentReceivableAmount();
                if (receivable.compareTo(amount) < 0) {
                    throw new BizException("PO关联保函应收不足：porder=" + poNo + ", 当前累计应收=" + receivable + ", 需释放=" + amount);
                }
                ExportVehicleCbuPoGuaranteeDo update = new ExportVehicleCbuPoGuaranteeDo();
                update.setId(g.getId());
                update.setPoCurrentReceivableAmount(receivable.subtract(amount));
                purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(update);
                log.info("PO关联保函回写：poNo={}, lgId={}, poCurrentReceivableAmount {} -> {}",
                        poNo, lgId, receivable, update.getPoCurrentReceivableAmount());
                return;
            }
        }
        throw new BizException("PO关联保函记录未匹配：porder=" + poNo + ", lgId=" + lgId);
    }

    private void updateHeaderStatus(List<SapClearAccountItem> clearAccountList) {
        // 同一请求可能涉及多个应付清单抬头，逐个统计收款情况并更新（同一抬头下多条明细去重）
        Set<String> processedHeaderIds = new HashSet<>();
        for (SapClearAccountItem item : clearAccountList) {
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
