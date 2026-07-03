package com.leapmotor.export.vehicle.basic.service.impl;

import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.basic.executor.cmd.ExportVehicleLetterGuaranteeCmdExe;
import com.leapmotor.export.vehicle.basic.executor.query.ExportVehicleLetterGuaranteeQryExe;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import com.leapmotor.export.vehicle.basic.req.*;
import com.leapmotor.export.vehicle.basic.resp.*;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 保函主表 服务实现类
 * </p>
 * <p>
 * 业务规则:Service 层只做编排,委托 CmdExe(写)/QryExe(读)/Gateway。
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Slf4j
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeServiceImpl implements ExportVehicleLetterGuaranteeService {

    private ExportVehicleLetterGuaranteeCmdExe exportVehicleLetterGuaranteeCmdExe;
    private ExportVehicleLetterGuaranteeQryExe exportVehicleLetterGuaranteeQryExe;
    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;

    // ============== 基础 CRUD ==============

    @Override
    public ExportVehicleLetterGuaranteeResp getExportVehicleLetterGuaranteeById(String id) {
        ExportVehicleLetterGuaranteeDo d = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(id);
        return ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainToResp(d);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeResp> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam param) {
        ExportVehicleLetterGuaranteeQo qo = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.paramToQuery(param);
        List<ExportVehicleLetterGuaranteeDo> list = exportVehicleLetterGuaranteeGateway.listExportVehicleLetterGuarantee(qo);
        return ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToResps(list);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeResp> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam param) {
        PageResult<ExportVehicleLetterGuaranteeDo> page = exportVehicleLetterGuaranteeQryExe.pageExportVehicleLetterGuarantee(param);
        return ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeAddOrUpdateReq req) {
        ExportVehicleLetterGuaranteeDo d = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.addCmdToDomain(req);
        return exportVehicleLetterGuaranteeCmdExe.addOrUpdateExportVehicleLetterGuarantee(d);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeCmdExe.deleteExportVehicleLetterGuaranteeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeCmdExe.enableOrDisableExportVehicleLetterGuaranteeByIds(param);
    }

    // ============== 状态机(单条/批量统一入口) ==============

    @Override
    public ExportVehicleLetterGuaranteeBatchStateMachineResp submit(ExportVehicleLetterGuaranteeSubmitReq req) {
        List<String> ids = resolveIds(req.getId(), req.getIds());
        return runBatch(ids, id -> exportVehicleLetterGuaranteeCmdExe.submit(id));
    }

    @Override
    public ExportVehicleLetterGuaranteeBatchStateMachineResp approve(ExportVehicleLetterGuaranteeApproveReq req) {
        List<String> ids = resolveIds(req.getId(), req.getIds());
        boolean approved = Boolean.TRUE.equals(req.getApproved());
        return runBatch(ids, id -> exportVehicleLetterGuaranteeCmdExe.approve(id, approved, req.getApproveRemark()));
    }

    @Override
    public ExportVehicleLetterGuaranteeBatchStateMachineResp cancel(ExportVehicleLetterGuaranteeCancelReq req) {
        List<String> ids = resolveIds(req.getId(), req.getIds());
        return runBatch(ids, id -> exportVehicleLetterGuaranteeCmdExe.cancel(id, req.getRemark()));
    }

    /**
     * 把单条 {@code id} 或批量 {@code ids} 统一收敛为 List<String>,并校验二选一约束。
     * 空集合抛 BizException(由控制器层校验更友好,但 Service 层兜底)。
     */
    private List<String> resolveIds(String id, List<String> ids) {
        boolean hasId = id != null && !id.isEmpty();
        boolean hasIds = ids != null && !ids.isEmpty();
        if (hasId && hasIds) {
            throw new BizException("id 与 ids 不可同时提供");
        }
        if (!hasId && !hasIds) {
            throw new BizException("id 或 ids 至少需要一个");
        }
        if (hasId) {
            return List.of(id);
        }
        return ids;
    }

    @Override
    public boolean expireEarly(ExportVehicleLetterGuaranteeExpireEarlyReq req) {
        // 操作人由 Gateway 内部通过 LoginUserUtils.getCurrentUser() 获取
        return exportVehicleLetterGuaranteeCmdExe.expireEarly(req.getId(), req.getReason());
    }

    // ============== 状态机批量编排 ==============

    /**
     * 批量状态机编排 — 遍历 ids 逐条调用 action,任一条失败不影响其他条。
     * 每条通过 cross-bean 调用(commandExecutor 是 Spring Bean)进入各自 @Transactional,失败回滚单条。
     */
    private ExportVehicleLetterGuaranteeBatchStateMachineResp runBatch(
            List<String> ids, java.util.function.Function<String, Boolean> action) {
        ExportVehicleLetterGuaranteeBatchStateMachineResp resp = new ExportVehicleLetterGuaranteeBatchStateMachineResp();
        List<ExportVehicleLetterGuaranteeBatchStateMachineResp.ItemResult> items = new ArrayList<>();
        int success = 0;
        int failure = 0;
        if (ids == null || ids.isEmpty()) {
            resp.setTotal(0);
            resp.setSuccessCount(0);
            resp.setFailureCount(0);
            resp.setAllSuccess(true);
            resp.setItems(items);
            return resp;
        }
        for (String id : ids) {
            ExportVehicleLetterGuaranteeBatchStateMachineResp.ItemResult item =
                    new ExportVehicleLetterGuaranteeBatchStateMachineResp.ItemResult();
            item.setId(id);
            try {
                Boolean ok = action.apply(id);
                if (Boolean.FALSE.equals(ok)) {
                    throw new BizException("操作返回 false");
                }
                ExportVehicleLetterGuaranteeDo lg = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(id);
                item.setLgCode(lg == null ? null : lg.getLgCode());
                item.setSuccess(true);
                success++;
            } catch (BizException e) {
                log.warn("保函批量状态机单条失败: id={}, err={}", id, e.getMessage());
                item.setSuccess(false);
                item.setErrorCode(e.getCode());
                item.setErrorMessage(e.getMessage());
                failure++;
            } catch (Exception e) {
                log.error("保函批量状态机单条异常: id={}, err={}", id, e.getMessage(), e);
                item.setSuccess(false);
                item.setErrorCode(ResultEnum.BAD_REQUEST.getCode());
                item.setErrorMessage(e.getMessage());
                failure++;
            }
            items.add(item);
        }
        resp.setTotal(ids.size());
        resp.setSuccessCount(success);
        resp.setFailureCount(failure);
        resp.setAllSuccess(failure == 0);
        resp.setItems(items);
        return resp;
    }

    // ============== 资金流 ==============

    @Override
    public boolean freeze(ExportVehicleLetterGuaranteeFreezeReq req) {
        return exportVehicleLetterGuaranteeCmdExe.freeze(req);
    }

    @Override
    public ExportVehicleLetterGuaranteeBatchFreezeResp batchFreeze(ExportVehicleLetterGuaranteeBatchFreezeReq req) {
        return exportVehicleLetterGuaranteeCmdExe.batchFreeze(req);
    }

    @Override
    public boolean unfreeze(ExportVehicleLetterGuaranteeUnfreezeReq req) {
        return exportVehicleLetterGuaranteeCmdExe.unfreeze(req);
    }

    @Override
    public boolean transferToReceivable(ExportVehicleLetterGuaranteeTransferReq req) {
        return exportVehicleLetterGuaranteeCmdExe.transferToReceivable(req);
    }

    @Override
    public boolean releaseReceivable(ExportVehicleLetterGuaranteeReleaseReq req) {
        return exportVehicleLetterGuaranteeCmdExe.releaseReceivable(req);
    }

    // ============== 查询 ==============

    @Override
    public List<ExportVehicleLetterGuaranteeListAvailableResp> listAvailableForCustomer(ExportVehicleLetterGuaranteeListAvailableReq req) {
        return exportVehicleLetterGuaranteeQryExe.listAvailableForCustomer(req);
    }

    /**
     * 资金校验(§8.5.1) — 校验 PO 所需金额是否可由客户可用保函覆盖,
     * 并附带 coverDays 校验(失效日期 ≥ 订单创建时间 + coverDays,Nacos 配置 letterGuarantee.coverDays,默认 100)
     */
    @Override
    public ExportVehicleLetterGuaranteeValidateResp validateForPo(ExportVehicleLetterGuaranteeValidateReq req) {
        ExportVehicleLetterGuaranteeValidateResp resp = new ExportVehicleLetterGuaranteeValidateResp();
        ExportVehicleLetterGuaranteeListAvailableReq listReq = new ExportVehicleLetterGuaranteeListAvailableReq();
        listReq.setCustomerId(req.getCustomerId());
        listReq.setOrderType(req.getOrderType());
        listReq.setRequiredAmount(req.getRequiredAmount());
        List<ExportVehicleLetterGuaranteeListAvailableResp> candidates = exportVehicleLetterGuaranteeQryExe.listAvailableForCustomer(listReq);
        resp.setCandidates(candidates);
        resp.setRequiredAmount(req.getRequiredAmount());

        // 1. 必须有候选保函
        if (candidates == null || candidates.isEmpty()) {
            resp.setPassed(false);
            resp.setFailCode("dict.letterGuarantee.validate.fail.noAvailable");
            resp.setFailMessage("客户无可用保函");
            return resp;
        }

        // 2. 总可用 ≥ 申请冻结金额
        BigDecimal totalAvailable = candidates.stream()
                .map(ExportVehicleLetterGuaranteeListAvailableResp::getAvailableAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resp.setTotalAvailable(totalAvailable);
        if (totalAvailable.compareTo(req.getRequiredAmount()) < 0) {
            resp.setPassed(false);
            resp.setFailCode("dict.letterGuarantee.validate.fail.insufficient");
            resp.setFailMessage("可用保函总金额不足:可用=" + totalAvailable + ", 申请=" + req.getRequiredAmount());
            return resp;
        }

        // 3. coverDays 校验(失效日期 ≥ 订单创建时间 + coverDays)
        long coverDays = resolveCoverDays();
        LocalDate deadline = req.getOrderCreateTime().plusDays(coverDays);
        boolean ok = candidates.stream().allMatch(c ->
                c.getExpiryDate() != null && !c.getExpiryDate().isBefore(deadline));
        if (!ok) {
            resp.setPassed(false);
            resp.setFailCode("dict.letterGuarantee.validate.fail.expiryTooEarly");
            resp.setFailMessage("存在保函失效日期早于订单创建时间 + " + coverDays + " 天");
            return resp;
        }

        resp.setPassed(true);
        return resp;
    }

    /**
     * 从 Nacos 配置 letterGuarantee.coverDays 读取覆盖天数(默认 100)。
     * 实际实现应通过 ExportVehicleLetterGuaranteeProperties 注入;此处先留可替换点。
     */
    private long resolveCoverDays() {
        return 100L;
    }
}
