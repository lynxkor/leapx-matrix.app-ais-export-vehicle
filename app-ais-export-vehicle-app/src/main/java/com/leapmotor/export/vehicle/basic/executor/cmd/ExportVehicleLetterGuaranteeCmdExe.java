package com.leapmotor.export.vehicle.basic.executor.cmd;


import com.alibaba.fastjson.JSON;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.basic.req.*;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeAdjustUnfreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeFreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeTransferItem;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeUnfreezeResp;
import com.leapmotor.export.vehicle.letterGuarantee.enums.RecordTypeEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 写操作执行器 - 所有状态机 + 资金流方法统一在此声明 @Transactional
 * <p>
 * 设计原则 (设计文档 §6.3.2 + §11.1):
 * - 写操作(状态机 + 资金流)主表+流水表同事务,任何失败整体回滚
 * - 由 Gateway 完成状态校验与金额计算(业务封装),本类仅做事务边界与日志
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Slf4j
@AllArgsConstructor
@Component
public class ExportVehicleLetterGuaranteeCmdExe {

    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;
    private ExportVehicleLetterGuaranteeRecordGateway exportVehicleLetterGuaranteeRecordGateway;
    private ApplicationContext applicationContext;

    // ============== 基础 CRUD ==============

    @Transactional(rollbackFor = Exception.class)
    public String addOrUpdateExportVehicleLetterGuarantee(
            ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo) {
        return exportVehicleLetterGuaranteeDo.getId() == null
                ? exportVehicleLetterGuaranteeGateway.addExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeDo)
                : exportVehicleLetterGuaranteeGateway.updateExportVehicleLetterGuarantee(
                exportVehicleLetterGuaranteeDo);
    }

    public boolean deleteExportVehicleLetterGuaranteeByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeGateway.deleteExportVehicleLetterGuaranteeByIds(ids);
    }

    public boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeGateway.enableOrDisableExportVehicleLetterGuaranteeByIds(param);
    }

    // ============== 状态机 ==============

    @Transactional(rollbackFor = Exception.class)
    public boolean submit(String id, String remark) {
        log.info("保函提交: id={}, remark={}", id, remark);
        return exportVehicleLetterGuaranteeGateway.submit(id, remark);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean approve(String id, boolean approved, String approveRemark) {
        log.info("保函审核: id={}, approved={}", id, approved);
        return exportVehicleLetterGuaranteeGateway.approve(id, approved, approveRemark);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(String id, String remark) {
        log.info("保函作废: id={}", id);
        return exportVehicleLetterGuaranteeGateway.cancel(id, remark);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean expireEarly(String id, String reason) {
        log.info("保函手动失效: id={}, reason={}", id, reason);
        return exportVehicleLetterGuaranteeGateway.expireEarly(id, "手动操作保函失效");
    }

    // ============== 资金流 ==============

    /**
     * 单 PO 冻结 — 整体事务 all-or-nothing:任一保函冻结失败立即抛异常,事务整体回滚(
     * 设计文档 §3.4.3 / §8.5.3)
     * <p>候选保函由服务端按 {@code listAvailableForCustomer} 自动选取(
     * 失效日期升序 + 同日按可用金额倒序 + 完全相同随机)。
     * <p>无「单张保函失败」状态 — 任一失败抛 BizException,由 @Transactional 整体回滚。
     * <p>成功时每次 {@code gateway.freeze} 会写一条 {@code record_type=10(冻结)} 的保函交易记录。
     */
    @Transactional(rollbackFor = Exception.class)
    public ExportVehicleLetterGuaranteeFreezeResp freeze(ExportVehicleLetterGuaranteeFreezeReq req) {
        log.info("保函单 PO 冻结: req={}", JSON.toJSONString(req));

        // 1. 自动选保函(透传 orderCreateTime 用于 coverDays SQL 过滤)
        List<ExportVehicleLetterGuaranteeDo> auto = exportVehicleLetterGuaranteeGateway.listAvailableForCustomer(req
                        .getCustomerId(),
                req.getOrderType(), req.getOrderCreateTime());
        if (auto == null || auto.isEmpty()) {
            throw new BizException("客户无可用保函");
        }
        // 总可用 ≥ 申请冻结金额,否则整体回滚
        BigDecimal totalAvailable = auto.stream().map(ExportVehicleLetterGuaranteeDo::getAvailableAmount).filter(
                Objects::nonNull).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        if (totalAvailable.compareTo(req.getRequiredAmount()) < 0) {
            log.error("可用保函总金额不足以覆盖需求:可用=" + totalAvailable + ", 申请=" + req.getRequiredAmount());
            throw new BizException("提交失败，资金校验未通过，请联系商务");
        }

        // 排序规则(业务规则,设计文档 §8.5.3):
        // a. 失效日期升序 — 优先冻结最先到期的保函
        // b. 失效日期相同时,可用订单金额倒序 — 同日到期时先用大额
        // c. 两者均相同则随机 — 先 shuffle 再稳定排序,相等的元素保留随机相对顺序
        List<ExportVehicleLetterGuaranteeDo> shuffled = new ArrayList<>(auto);
        Collections.shuffle(shuffled, ThreadLocalRandom.current());
        List<ExportVehicleLetterGuaranteeDo> sorted = shuffled.stream()
                .sorted(Comparator
                        .comparing((ExportVehicleLetterGuaranteeDo d) -> d.getExpiryDate(),
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing((ExportVehicleLetterGuaranteeDo d) -> d.getAvailableAmount(),
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        // 2. 逐步拆冻,任一失败 → 抛 BizException → @Transactional 整体回滚
        ExportVehicleLetterGuaranteeFreezeResp resp = new ExportVehicleLetterGuaranteeFreezeResp();
        resp.setPoNo(req.getPoNo());
        List<ExportVehicleLetterGuaranteeFreezeResp.FrozenItem> frozen = new ArrayList<>();
        BigDecimal totalFrozen = BigDecimal.ZERO;
        BigDecimal remaining = req.getRequiredAmount();
        for (ExportVehicleLetterGuaranteeDo candidate : sorted) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal available = candidate.getAvailableAmount() == null ? BigDecimal.ZERO : candidate
                    .getAvailableAmount();
            if (available.compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal amount = remaining.compareTo(available) <= 0 ? remaining : available;
            boolean ok = exportVehicleLetterGuaranteeGateway.freeze(candidate.getId(), amount, req.getOrderId(),
                    req.getOrderNo(), req.getPoNo(), null, null, req.getRemark());
            if (!ok) {
                throw new BizException("保函冻结失败: " + candidate.getId());
            }
            ExportVehicleLetterGuaranteeFreezeResp.FrozenItem item = new ExportVehicleLetterGuaranteeFreezeResp
                    .FrozenItem();
            item.setLgId(candidate.getId());
            item.setLgCode(candidate.getLgCode());
            item.setFrozenAmount(amount);
            item.setPoNo(req.getPoNo());
            frozen.add(item);
            totalFrozen = totalFrozen.add(amount);
            remaining = remaining.subtract(amount);
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            log.error("可用保函总金额不足以覆盖需求,缺 " + remaining);
            throw new BizException("提交失败，资金校验未通过，请联系商务");
        }
        resp.setSuccess(true);
        resp.setTotalFrozenAmount(totalFrozen);
        resp.setFrozenItems(frozen);
        return resp;
    }

    /**
     * 批量 PO 冻结 — 每个 PO 独立事务,任一 PO 失败不影响其他 PO。
     * <p>每个 PO 通过 cross-bean 调用 {@link #freeze(
     *ExportVehicleLetterGuaranteeFreezeReq)} 进入各自事务。
     */
    public List<ExportVehicleLetterGuaranteeFreezeResp> batchFreeze(List<ExportVehicleLetterGuaranteeFreezeReq> list) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        log.info("保函批量 PO 冻结: 共 {} 个 PO", list.size());
        List<ExportVehicleLetterGuaranteeFreezeResp> results = new ArrayList<>();
        for (ExportVehicleLetterGuaranteeFreezeReq singlePo : list) {
            ExportVehicleLetterGuaranteeFreezeResp poResp;
            try {
                poResp = applicationContext.getBean(ExportVehicleLetterGuaranteeCmdExe.class).freeze(singlePo);
            } catch (BizException e) {
                poResp = new ExportVehicleLetterGuaranteeFreezeResp();
                poResp.setPoNo(singlePo.getPoNo());
                poResp.setSuccess(false);
                poResp.setFailMessage(e.getMessage());
            } catch (Exception e) {
                poResp = new ExportVehicleLetterGuaranteeFreezeResp();
                poResp.setPoNo(singlePo.getPoNo());
                poResp.setSuccess(false);
                poResp.setFailMessage(e.getMessage());
            }
            results.add(poResp);
        }
        return results;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean unfreeze(ExportVehicleLetterGuaranteeUnfreezeReq req) {
        log.info("保函 PO 解冻: req={}", JSON.toJSONString(req));
        // 1. 查该 PO 下冻结/解冻/转应收明细,按 lgId 汇总 PO 当前净占用(冻结 - 解冻 - 转应收),逐张全额解冻
        Map<String, BigDecimal> occupiedByLgId = loadNetOccupiedByLgId(req.getPoNo());
        if (occupiedByLgId.isEmpty()) {
            log.info("PO {} 下无冻结记录或全部已解冻,无需解冻", req.getPoNo());
            return true;
        }
        int unfrozenCount = 0;
        for (Map.Entry<String, BigDecimal> e : occupiedByLgId.entrySet()) {
            BigDecimal amt = e.getValue() == null ? BigDecimal.ZERO : e.getValue();
            if (amt.compareTo(BigDecimal.ZERO) <= 0) continue;
            boolean ok = exportVehicleLetterGuaranteeGateway.unfreeze(e.getKey(), amt,
                    req.getOrderId(), req.getPoNo(), req.getReason());
            if (!ok) {
                throw new BizException("保函解冻失败: lgId=" + e.getKey());
            }
            unfrozenCount++;
        }
        log.info("PO {} 解冻完成: 共解冻 {} 张保函", req.getPoNo(), unfrozenCount);
        return true;
    }

    /**
     * 批量 PO 解冻 — 每个 PO 独立事务,任一 PO 失败不影响其他 PO。
     * <p>每个 PO 通过 cross-bean 调用 {@link #unfreeze(
     *ExportVehicleLetterGuaranteeUnfreezeReq)} 进入各自事务。
     */
    public List<ExportVehicleLetterGuaranteeUnfreezeResp> batchUnfreeze(
            List<ExportVehicleLetterGuaranteeUnfreezeReq> list) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        log.info("保函批量 PO 解冻: 共 {} 个 PO", list.size());
        List<ExportVehicleLetterGuaranteeUnfreezeResp> results = new ArrayList<>();
        for (ExportVehicleLetterGuaranteeUnfreezeReq singlePo : list) {
            ExportVehicleLetterGuaranteeUnfreezeResp poResp;
            try {
                applicationContext.getBean(ExportVehicleLetterGuaranteeCmdExe.class).unfreeze(singlePo);
                poResp = new ExportVehicleLetterGuaranteeUnfreezeResp();
                poResp.setPoNo(singlePo.getPoNo());
                poResp.setSuccess(true);
            } catch (BizException e) {
                poResp = new ExportVehicleLetterGuaranteeUnfreezeResp();
                poResp.setPoNo(singlePo.getPoNo());
                poResp.setSuccess(false);
                poResp.setFailMessage(e.getMessage());
            } catch (Exception e) {
                poResp = new ExportVehicleLetterGuaranteeUnfreezeResp();
                poResp.setPoNo(singlePo.getPoNo());
                poResp.setSuccess(false);
                poResp.setFailMessage(e.getMessage());
            }
            results.add(poResp);
        }
        return results;
    }

    /**
     * 单 PO 转应收(
     * 质检通过) — 整体事务 all-or-nothing:任一保函转应收失败立即抛异常,事务整体回滚(
     * 设计文档 §3.4.3)
     *
     * <p>输入是 SO 单粒度(poNo + soNo + frozenAmount + vin),服务端:
     * <ol>
     *   <li>查该 PO 下冻结+解冻+转应收明细(transaction_type=10/20/30),按 lgId 汇总 PO 当前净占用金额(冻结 - 解冻 - 转应收)</li>
     *   <li>校验累计占用 ≥ SO冻结金额(否则整体回滚)</li>
     *   <li>排序规则:
     *     <ul>
     *       <li>a. 失效日期升序 — 优先使用最早到期的保函</li>
     *       <li>b. 同日时,PO当前累计占用金额倒序 — 优先用大额</li>
     *       <li>c. 两者均相同 → 随机</li>
     *     </ul>
     *   </li>
     *   <li>按排序依次转应收,每张金额 = min(剩余 SO冻结金额,
     * 该保函 PO累计占用金额),直至累计 = SO冻结金额</li>
     * </ol>
     *
     * <p>每次 {@code gateway.transferToReceivable} 会写一条 {@code record_type=30(
     * 转应收)} 的保函交易记录。
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ExportVehicleLetterGuaranteeTransferItem> transferToReceivable(
            ExportVehicleLetterGuaranteeTransferReq req) {
        log.info("保函单 PO 转应收: req={} ", JSON.toJSONString(req));

        // 1. 查该 PO 下冻结/解冻/转应收明细,按 lgId 汇总 PO 当前净占用金额(冻结 - 解冻 - 转应收)
        Map<String, BigDecimal> occupiedByLgId = loadNetOccupiedByLgId(req.getPoNo());
        if (occupiedByLgId.isEmpty()) {
            throw new BizException("PO " + req.getPoNo() + " 下无冻结记录,无法转应收");
        }

        // 2. 校验累计占用 ≥ SO冻结金额
        BigDecimal totalOccupied = occupiedByLgId.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalOccupied.compareTo(req.getFrozenAmount()) < 0) {
            throw new BizException("PO 累计占用金额不足以转应收:占用=" +
                    totalOccupied + ", SO冻结金额=" + req.getFrozenAmount());
        }

        // 3. 加载保函主表获取 expiryDate
        List<ExportVehicleLetterGuaranteeDo> candidates = new ArrayList<>();
        for (String lgId : occupiedByLgId.keySet()) {
            ExportVehicleLetterGuaranteeDo lg = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(
                    lgId);
            if (lg == null) {
                throw new BizException("保函不存在: " + lgId);
            }
            candidates.add(lg);
        }

        // 4. 排序:失效日期 ASC → PO累计占用 DESC → 都相同随机
        Map<String, BigDecimal> finalOccMap = occupiedByLgId;
        List<ExportVehicleLetterGuaranteeDo> shuffled = new ArrayList<>(candidates);
        Collections.shuffle(shuffled, ThreadLocalRandom.current());
        List<ExportVehicleLetterGuaranteeDo> sorted = shuffled.stream()
                .sorted(Comparator
                        .comparing((ExportVehicleLetterGuaranteeDo d) -> d.getExpiryDate(),
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing((ExportVehicleLetterGuaranteeDo d) -> finalOccMap.get(d.getId()),
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        // 5. 逐步转应收,任一失败 → 抛 BizException → @Transactional 整体回滚
        BigDecimal remaining = req.getFrozenAmount();
        List<ExportVehicleLetterGuaranteeTransferItem> items = new ArrayList<>();
        for (ExportVehicleLetterGuaranteeDo lg : sorted) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal lgOcc = occupiedByLgId.get(lg.getId());
            BigDecimal transferAmount = remaining.compareTo(lgOcc) <= 0 ? remaining : lgOcc;
            boolean ok = exportVehicleLetterGuaranteeGateway.transferToReceivable(lg.getId(), transferAmount,
                    req.getOrderId(), req.getPoNo(), req.getSoNo(), req.getVin(), req.getReason());
            if (!ok) {
                throw new BizException("保函转应收失败: lgId=" + lg.getId());
            }
            ExportVehicleLetterGuaranteeTransferItem item = new ExportVehicleLetterGuaranteeTransferItem();
            item.setLgId(lg.getId());
            item.setLgCode(lg.getLgCode());
            item.setTransferAmount(transferAmount);
            item.setPoNo(req.getPoNo());
            items.add(item);
            remaining = remaining.subtract(transferAmount);
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new BizException("转应收累计金额不足,缺 " + remaining);
        }
        log.info("PO {} 转应收完成: SO冻结金额={}, 处理保函 {} 张", req.getPoNo(), req.getFrozenAmount(),
                items.size());
        return items;
    }

    /**
     * 单 PO 应收释放(
     * SAP 清账) — 整体事务 all-or-nothing:任一保函释放失败立即抛异常,事务整体回滚
     *
     * <p>输入是 PO 单粒度(poNo + soNo),服务端:
     * <ol>
     *   <li>查该 PO 下所有转应收明细(transaction_type=30),按 lgId 汇总应收金额</li>
     *   <li>逐张保函调用 {@code gateway.releaseReceivable} 释放对应金额</li>
     *   <li>每张保函单独写一条 {@code record_type=40(应收释放)} 流水</li>
     * </ol>
     *
     * <p>释放后保函不补回(应收冲销完毕),可用/占用金额不变,只减少 receivable。
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseReceivable(ExportVehicleLetterGuaranteeReleaseReq req) {
        log.info("保函单 PO 应收释放: req={} ", JSON.toJSONString(req));

        // 1. 查该 PO + SO 下所有转应收明细(transaction_type=30),按 lgId 汇总应收金额
        ExportVehicleLetterGuaranteeRecordQo qo = new ExportVehicleLetterGuaranteeRecordQo();
        qo.setPoNo(req.getPoNo());
        qo.setSoNo(req.getSoNo());
        qo.setTransactionType(RecordTypeEnum.TRANSFER_RECEIVABLE.getCode());
        List<ExportVehicleLetterGuaranteeRecordDo> receivableRecords = exportVehicleLetterGuaranteeRecordGateway
                .listExportVehicleLetterGuaranteeRecord(qo);
        if (receivableRecords == null || receivableRecords.isEmpty()) {
            throw new BizException("PO " + req.getPoNo() + " / SO " + req.getSoNo() +
                    " 下无转应收记录,无法应收释放");
        }
        Map<String, BigDecimal> receivableByLgId = new LinkedHashMap<>();
        for (ExportVehicleLetterGuaranteeRecordDo rec : receivableRecords) {
            BigDecimal amt = rec.getTransactionAmount() == null ? BigDecimal.ZERO : rec.getTransactionAmount();
            receivableByLgId.merge(rec.getLgId(), amt, BigDecimal::add);
        }

        // 2. 逐张保函释放,任一失败 → 抛 BizException → @Transactional 整体回滚
        int releasedCount = 0;
        for (Map.Entry<String, BigDecimal> e : receivableByLgId.entrySet()) {
            String lgId = e.getKey();
            BigDecimal amount = e.getValue();
            if (amount.compareTo(BigDecimal.ZERO) <= 0) continue;
            boolean ok = exportVehicleLetterGuaranteeGateway.releaseReceivable(lgId, amount, req.getOrderId(),
                    req.getPoNo(), req.getSoNo(), req.getReason());
            if (!ok) {
                throw new BizException("保函应收释放失败: lgId=" + lgId);
            }
            releasedCount++;
        }
        log.info("PO:{} SO:{} 应收释放完成: 共处理 {} 张保函", req.getPoNo(), req.getSoNo(), releasedCount);
        return true;
    }

    // ============== 私有助手方法 ==============

    /**
     * 查 PO 下所有冻结/解冻/转应收明细,按 lgId 汇总 PO 当前净占用(冻结 - 解冻 - 转应收)。
     * <p>用于避免「同一 PO 多次资金操作时,占用基于历史冻结总额而非净占用」的 bug:
     * 流水一旦写入便不会改写/删除,故必须显式减去所有「会减少 occupied 字段」的后续流水才能拿到当前真实占用。
     * <p>净占用 ≤ 0 的 lgId 会被剔除,返回空 Map 等价于「PO 下无任何可用净占用」。
     */
    private Map<String, BigDecimal> loadNetOccupiedByLgId(String poNo) {
        Map<String, BigDecimal> net = new LinkedHashMap<>();
        accumulateByLgId(net, queryRecordsByType(poNo, RecordTypeEnum.FREEZE.getCode()), BigDecimal::add);
        accumulateByLgId(net, queryRecordsByType(poNo, RecordTypeEnum.UNFREEZE.getCode()), BigDecimal::subtract);
        accumulateByLgId(net, queryRecordsByType(poNo, RecordTypeEnum.TRANSFER_RECEIVABLE.getCode()), BigDecimal::subtract);
        // 仅保留净占用 > 0 的项,空 Map 表示「PO 下无任何净占用」
        net.entrySet().removeIf(e -> e.getValue() == null || e.getValue().compareTo(BigDecimal.ZERO) <= 0);
        return net;
    }

    private List<ExportVehicleLetterGuaranteeRecordDo> queryRecordsByType(String poNo, String transactionType) {
        ExportVehicleLetterGuaranteeRecordQo qo = new ExportVehicleLetterGuaranteeRecordQo();
        qo.setPoNo(poNo);
        qo.setTransactionType(transactionType);
        return exportVehicleLetterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(qo);
    }

    private void accumulateByLgId(Map<String, BigDecimal> net,
                                  List<ExportVehicleLetterGuaranteeRecordDo> recs,
                                  java.util.function.BinaryOperator<BigDecimal> op) {
        if (recs == null) return;
        for (ExportVehicleLetterGuaranteeRecordDo rec : recs) {
            BigDecimal amt = rec.getTransactionAmount() == null ? BigDecimal.ZERO : rec.getTransactionAmount();
            net.merge(rec.getLgId(), amt, op);
        }
    }

    // ============== PO 刷新订单价格联动(§3.5) ==============

    /**
     * 调减退差(
     * §3.5.3):部分解冻 — 按失效日期倒序循环解冻多张保函,直到累计解冻金额 = amountDiff。
     *
     * <p>算法(单一 {@code @Transactional(rollbackFor=Exception.class)} 事务,all-or-nothing):
     * <ol>
     *   <li>校验 amountDiff > 0</li>
     *   <li>查该 PO 下冻结+解冻+转应收明细(transaction_type=10/20/30),按 lgId 汇总 PO 当前净占用(冻结 - 解冻 - 转应收)</li>
     *   <li>取主表校验保函存在 → 形成候选集(失效/作废由 gateway
     * .unfreeze 内 ensureLgStatus 兜底)</li>
     *   <li>排序:失效日期 DESC(最晚优先,nullsLast 保证 null 排末位)</li>
     *   <li>空候选集 → 抛 BizException("无可解冻保函")</li>
     *   <li>循环:对每张候选执行 gateway.unfreeze(本张占用 / 剩余金额,取小),
     *       累计解冻金额;remaining 归零即终止</li>
     *   <li>循环结束若 remaining > 0 → 抛 BizException("累计占用不足")整体回滚</li>
     * </ol>
     *
     * <p>与 {@link #unfreeze(ExportVehicleLetterGuaranteeUnfreezeReq)} 区别:这里是部分解冻(退金额差,
     * 可能跨多张保函),
     * {@code unfreeze} 是全解冻(逐张全额释放)。
     *
     * <p>响应含所有被解冻保函的明细(按失效日期倒序,
     * 可多张),便于 PO 模块回写关联保函信息。
     */
    @Transactional(rollbackFor = Exception.class)
    public ExportVehicleLetterGuaranteeAdjustUnfreezeResp adjustUnfreeze(
            ExportVehicleLetterGuaranteeAdjustUnfreezeReq req) {
        log.info("保函 PO 刷新价格调减: req={}", JSON.toJSONString(req));

        if (req.getAmountDiff() == null || req.getAmountDiff().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("amountDiff 必须大于 0");
        }

        // 1. 查该 PO 下冻结/解冻/转应收明细,按 lgId 汇总 PO 当前净占用(冻结 - 解冻 - 转应收)
        Map<String, BigDecimal> occupiedByLgId = loadNetOccupiedByLgId(req.getPoNo());
        if (occupiedByLgId.isEmpty()) {
            throw new BizException("PO " + req.getPoNo() + " 下无冻结记录,无法调减");
        }

        // 2. 取主表形成候选集(失效/作废 由 gateway.unfreeze 内 ensureLgStatus 兜底)
        List<ExportVehicleLetterGuaranteeDo> candidates = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : occupiedByLgId.entrySet()) {
            ExportVehicleLetterGuaranteeDo lg = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(
                    e.getKey());
            if (lg == null) {
                continue;
            }
            candidates.add(lg);
        }

        // 3. 排序:失效日期 DESC(最晚优先,nullsLast 保证 null 排末位)
        candidates.sort(Comparator
                .comparing((ExportVehicleLetterGuaranteeDo d) -> d.getExpiryDate(),
                        Comparator.nullsLast(Comparator.reverseOrder())));

        // 4. 空候选集 → 抛错(整体回滚,无副作用)
        if (candidates.isEmpty()) {
            throw new BizException("无可解冻保函(PO 下有冻结记录但主表缺失)");
        }

        // 5. 循环解冻:每张解冻 min(本张 PO 占用, remaining),remaining 归零即终止
        BigDecimal remaining = req.getAmountDiff();
        List<ExportVehicleLetterGuaranteeAdjustUnfreezeResp.UnfreezeItem> items = new ArrayList<>();
        for (ExportVehicleLetterGuaranteeDo lg : candidates) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal lgOccupied = occupiedByLgId.getOrDefault(lg.getId(), BigDecimal.ZERO);
            if (lgOccupied.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal unfreezeAmount = remaining.compareTo(lgOccupied) <= 0 ? remaining : lgOccupied;
            boolean ok = exportVehicleLetterGuaranteeGateway.unfreeze(
                    lg.getId(), unfreezeAmount,
                    req.getOrderId(), req.getPoNo(), req.getReason());
            if (!ok) {
                throw new BizException("保函调减解冻失败: lgId=" + lg.getId());
            }
            ExportVehicleLetterGuaranteeAdjustUnfreezeResp
                    .UnfreezeItem item = new ExportVehicleLetterGuaranteeAdjustUnfreezeResp.UnfreezeItem();
            item.setLgId(lg.getId());
            item.setLgCode(lg.getLgCode());
            item.setUnfrozenAmount(unfreezeAmount);
            item.setPoNo(req.getPoNo());
            items.add(item);
            remaining = remaining.subtract(unfreezeAmount);
            log.info("PO {} 调减退差部分解冻: 选中保函={}, 本次解冻={}, 剩余={}", req.getPoNo(), lg
                            .getLgCode(), unfreezeAmount,
                    remaining);
        }

        // 6. 累计占用不足 → 抛错整体回滚
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new BizException("累计占用不足: amountDiff=" + req.getAmountDiff() + ", 已解冻=" + req
                    .getAmountDiff()
                    .subtract(remaining) + ", 剩余=" + remaining);
        }

        // 7. 组装响应
        ExportVehicleLetterGuaranteeAdjustUnfreezeResp resp = new ExportVehicleLetterGuaranteeAdjustUnfreezeResp();
        resp.setSuccess(true);
        resp.setPoNo(req.getPoNo());
        resp.setTotalUnfrozenAmount(req.getAmountDiff());
        resp.setUnfreezeItems(items);
        log.info("PO {} 调减退差完成: 共解冻 {} 张保函, 累计金额={}", req.getPoNo(), items.size(), req
                .getAmountDiff());
        return resp;
    }
}

