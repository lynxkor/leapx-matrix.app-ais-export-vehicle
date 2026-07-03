package com.leapmotor.export.vehicle.basic.executor.cmd;


import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.req.*;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeBatchFreezeResp;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

    // ============== 基础 CRUD ==============

    @Transactional(rollbackFor = Exception.class)
    public String addOrUpdateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo) {
        return exportVehicleLetterGuaranteeDo.getId() == null
                ? exportVehicleLetterGuaranteeGateway.addExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeDo)
                : exportVehicleLetterGuaranteeGateway.updateExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeDo);
    }

    public boolean deleteExportVehicleLetterGuaranteeByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeGateway.deleteExportVehicleLetterGuaranteeByIds(ids);
    }

    public boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeGateway.enableOrDisableExportVehicleLetterGuaranteeByIds(param);
    }

    // ============== 状态机 ==============

    @Transactional(rollbackFor = Exception.class)
    public boolean submit(String id) {
        log.info("保函提交: id={}", id);
        return exportVehicleLetterGuaranteeGateway.submit(id);
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
        return exportVehicleLetterGuaranteeGateway.expireEarly(id, reason);
    }

    // ============== 资金流 ==============

    @Transactional(rollbackFor = Exception.class)
    public boolean freeze(ExportVehicleLetterGuaranteeFreezeReq req) {
        log.info("保函冻结: lgId={}, amount={}, poNo={}",
                req.getLgId(), req.getAmount(), req.getPoNo());
        return exportVehicleLetterGuaranteeGateway.freeze(req.getLgId(), req.getAmount(),
                req.getOrderId(), req.getOrderNo(), req.getPoNo(), req.getSoNo(), req.getVin(),
                req.getRemark());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean unfreeze(ExportVehicleLetterGuaranteeUnfreezeReq req) {
        log.info("保函解冻: lgId={}, amount={}, poNo={}",
                req.getLgId(), req.getAmount(), req.getPoNo());
        return exportVehicleLetterGuaranteeGateway.unfreeze(req.getLgId(), req.getAmount(),
                req.getOrderId(), req.getPoNo(), req.getReason());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean transferToReceivable(ExportVehicleLetterGuaranteeTransferReq req) {
        log.info("保函转应收: lgId={}, amount={}, soNo={}, vin={}",
                req.getLgId(), req.getAmount(), req.getSoNo(), req.getVin());
        return exportVehicleLetterGuaranteeGateway.transferToReceivable(req.getLgId(), req.getAmount(),
                req.getOrderId(), req.getSoNo(), req.getVin(), req.getReason());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean releaseReceivable(ExportVehicleLetterGuaranteeReleaseReq req) {
        log.info("保函释放应收: lgId={}, amount={}, soNo={}",
                req.getLgId(), req.getAmount(), req.getSoNo());
        return exportVehicleLetterGuaranteeGateway.releaseReceivable(req.getLgId(), req.getAmount(),
                req.getOrderId(), req.getSoNo(), req.getReason());
    }

    /**
     * 批量冻结 — 整体事务,任一失败回滚(设计文档 §3.4.3)
     */
    @Transactional(rollbackFor = Exception.class)
    public ExportVehicleLetterGuaranteeBatchFreezeResp batchFreeze(ExportVehicleLetterGuaranteeBatchFreezeReq req) {
        log.info("保函批量冻结: customer={}, orderType={}, required={}, candidates={}",
                req.getCustomerId(), req.getOrderType(), req.getRequiredAmount(),
                req.getCandidateLgIds());

        ExportVehicleLetterGuaranteeBatchFreezeResp resp = new ExportVehicleLetterGuaranteeBatchFreezeResp();
        List<ExportVehicleLetterGuaranteeBatchFreezeResp.FrozenItem> frozen = new ArrayList<>();
        List<ExportVehicleLetterGuaranteeBatchFreezeResp.FailedItem> failed = new ArrayList<>();
        AtomicLong totalFrozen = new AtomicLong(0);

        // 1. 选取候选保函
        List<String> candidates = req.getCandidateLgIds();
        if (candidates == null || candidates.isEmpty()) {
            // 自动按可用金额降序选 — 这里仅做一轮 lookup;若不足一次性补齐
            List<ExportVehicleLetterGuaranteeDo> auto = exportVehicleLetterGuaranteeGateway.listAvailableForCustomer(
                    req.getCustomerId(), req.getOrderType(), null);
            if (auto != null) {
                candidates = auto.stream().map(ExportVehicleLetterGuaranteeDo::getId).toList();
            } else {
                candidates = List.of();
            }
        }
        if (candidates.isEmpty()) {
            resp.setSuccess(false);
            resp.setFailCode("LG_NO_AVAILABLE");
            resp.setFailMessage("客户无可用保函");
            return resp;
        }

        // 2. 逐步拆冻,任一不可用 → 全部回滚(@Transactional)
        BigDecimal remaining = req.getRequiredAmount();
        for (String lgId : candidates) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            // 单张最大可冻金额由 Service 内询 Gateway 后传入;此处简化用 remaining
            BigDecimal amount = remaining;
            ExportVehicleLetterGuaranteeFreezeReq single = new ExportVehicleLetterGuaranteeFreezeReq();
            single.setLgId(lgId);
            single.setAmount(amount);
            single.setOrderId(req.getOrderId());
            single.setOrderNo(req.getOrderNo());
            single.setPoNo(req.getPoNo());
            single.setRemark(req.getRemark());
            try {
                boolean ok = freeze(single);
                if (!ok) {
                    throw new BizException("保函冻结失败: " + lgId);
                }
                ExportVehicleLetterGuaranteeDo lg = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(lgId);
                ExportVehicleLetterGuaranteeBatchFreezeResp.FrozenItem item = new ExportVehicleLetterGuaranteeBatchFreezeResp.FrozenItem();
                item.setLgId(lgId);
                item.setLgCode(lg == null ? null : lg.getLgCode());
                item.setFrozenAmount(amount);
                item.setPoNo(req.getPoNo());
                frozen.add(item);
                totalFrozen.addAndGet(amount.longValue());
                remaining = remaining.subtract(amount);
            } catch (BizException e) {
                ExportVehicleLetterGuaranteeDo lg = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(lgId);
                ExportVehicleLetterGuaranteeBatchFreezeResp.FailedItem item = new ExportVehicleLetterGuaranteeBatchFreezeResp.FailedItem();
                item.setLgId(lgId);
                item.setLgCode(lg == null ? null : lg.getLgCode());
                item.setRequiredAmount(amount);
                item.setAvailableAmount(lg == null ? null : lg.getAvailableAmount());
                item.setReason(e.getMessage());
                failed.add(item);
                throw e; // 任一失败整体回滚
            }
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new BizException("可用保函总金额不足以覆盖需求,缺 " + remaining);
        }
        resp.setSuccess(true);
        resp.setTotalFrozenAmount(BigDecimal.valueOf(totalFrozen.get()));
        resp.setFrozenItems(frozen);
        resp.setFailedItems(failed);
        return resp;
    }
}
