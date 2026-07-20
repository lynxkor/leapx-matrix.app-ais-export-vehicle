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
import com.leapmotor.export.vehicle.cache.CustomerInfoCache;
import com.leapmotor.export.vehicle.letterGuarantee.enums.ApprovalStatusEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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

    /**
     * 保函金额上限 10 亿
     */
    private static final BigDecimal MAX_TOTAL_AMOUNT = new BigDecimal("1000000000");
    /**
     * 保函编号最大长度
     */
    private static final int LG_CODE_MAX_LENGTH = 30;
    /**
     * 开户银行最大长度
     */
    private static final int BANK_NAME_MAX_LENGTH = 100;
    /**
     * 审核意见最大长度(通过/驳回通用)
     */
    private static final int APPROVE_REMARK_MAX_LENGTH = 200;

    // ============== 基础 CRUD ==============
    private ExportVehicleLetterGuaranteeCmdExe exportVehicleLetterGuaranteeCmdExe;
    private ExportVehicleLetterGuaranteeQryExe exportVehicleLetterGuaranteeQryExe;
    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;
    private CustomerInfoCache customerInfoCache;

    private static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BizException(fieldName + "不能为空");
        }
    }

    private static void requireMaxLength(String value, int max, String fieldName) {
        if (value != null && value.length() > max) {
            throw new BizException(fieldName + "长度不能超过" + max);
        }
    }

    @Override
    public ExportVehicleLetterGuaranteeResp getExportVehicleLetterGuaranteeById(String id) {
        ExportVehicleLetterGuaranteeDo d = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(id);
        ExportVehicleLetterGuaranteeResp resp = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainToResp(d);
        fillCustomerInfo(resp);
        return resp;
    }

    @Override
    public List<ExportVehicleLetterGuaranteeResp> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam param) {
        validateDateRange(param);
        ExportVehicleLetterGuaranteeQo qo = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.paramToQuery(param);
        List<ExportVehicleLetterGuaranteeDo> list = exportVehicleLetterGuaranteeGateway.listExportVehicleLetterGuarantee(qo);
        List<ExportVehicleLetterGuaranteeResp> resps = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToResps(list);
        fillCustomerInfo(resps);
        return resps;
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeResp> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam param) {
        validateDateRange(param);
        PageResult<ExportVehicleLetterGuaranteeDo> page = exportVehicleLetterGuaranteeQryExe.pageExportVehicleLetterGuarantee(param);
        PageResult<ExportVehicleLetterGuaranteeResp> respPage = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToRespPage(page);
        fillCustomerInfo(respPage.getRecords());
        return respPage;
    }

    /**
     * 校验查询入参中各起止日期区间:起始晚于截止即抛 BizException(允许起始=截止,表示同一时点/当天)。
     */
    private void validateDateRange(ExportVehicleLetterGuaranteeParam param) {
        if (param == null) {
            return;
        }
        checkRange(param.getEffectiveDateStart(), param.getEffectiveDateEnd(), "生效日期");
        checkRange(param.getExpiryDateStart(), param.getExpiryDateEnd(), "失效日期");
        checkRange(param.getSubmitTimeStart(), param.getSubmitTimeEnd(), "提交时间");
        checkRange(param.getApproveTimeStart(), param.getApproveTimeEnd(), "审核时间");
        checkRange(param.getCreateTimeStart(), param.getCreateTimeEnd(), "创建时间");
    }

    private <T extends Comparable<T>> void checkRange(T start, T end, String label) {
        if (start != null && end != null && start.compareTo(end) > 0) {
            throw new BizException(label + "的开始时间不能晚于结束时间");
        }
    }

    @Override
    public String addOrUpdateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeAddOrUpdateReq req) {
        validateAddOrUpdate(req);
        ExportVehicleLetterGuaranteeDo d = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.addCmdToDomain(req);
        // 编辑场景(id 非空):状态机校验 — 仅草稿(10)/驳回(40)可编辑,避免并发场景下 B 窗口用陈旧表单覆盖 A 已审核通过的数据
        if (d.getId() != null) {
            ExportVehicleLetterGuaranteeDo cur = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(d.getId());
            if (cur == null) {
                throw new BizException("保函不存在: id=" + d.getId());
            }
            String curApproval = cur.getApprovalStatus();
            if (!ApprovalStatusEnum.DRAFT.getCode().equals(curApproval)
                    && !ApprovalStatusEnum.REJECTED.getCode().equals(curApproval)) {
                throw new BizException("保函当前审批状态(" + ApprovalStatusEnum.getDesc(curApproval) + ")不可编辑,仅草稿/驳回可编辑,请刷新页面");
            }
        }
        // 保函编号唯一性(编辑时排除自身)
        long dupCount = exportVehicleLetterGuaranteeGateway.countByLgCode(d.getLgCode(), d.getId());
        if (dupCount > 0) {
            throw new BizException("保函编号已存在: " + d.getLgCode());
        }
        return exportVehicleLetterGuaranteeCmdExe.addOrUpdateExportVehicleLetterGuarantee(d);
    }

    /**
     * 新增/编辑 入参校验:
     * <ul>
     *   <li>必填:保函编号/客户ID/保函类型/订单类型/开户银行/生效日期/失效日期</li>
     *   <li>保函金额:大于 0,最多 2 位小数,≤ 1,000,000,000(10 亿)</li>
     *   <li>换算订单额度比例:大于 0,≤ 1,最多 2 位小数</li>
     *   <li>日期关系:生效日期 &lt; 失效日期,失效日期 &gt; 当天</li>
     * </ul>
     */
    private void validateAddOrUpdate(ExportVehicleLetterGuaranteeAddOrUpdateReq req) {
        if (req == null) {
            throw new BizException("请求参数不能为空");
        }
        // 必填校验
        requireNotBlank(req.getLgCode(), "保函编号");
        requireMaxLength(req.getLgCode(), LG_CODE_MAX_LENGTH, "保函编号");
        requireNotBlank(req.getCustomerId(), "客户ID");
        requireNotBlank(req.getLgType(), "保函类型");
        requireNotBlank(req.getOrderType(), "订单类型");
        requireNotBlank(req.getBankName(), "开户银行");
        requireMaxLength(req.getBankName(), BANK_NAME_MAX_LENGTH, "开户银行");
        if (req.getEffectiveDate() == null) {
            throw new BizException("生效日期不能为空");
        }
        if (req.getExpiryDate() == null) {
            throw new BizException("失效日期不能为空");
        }

        // 保函金额:> 0,≤ 10 亿,最多 2 位小数
        if (req.getTotalAmount() == null) {
            throw new BizException("保函金额不能为空");
        }
        if (req.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("保函金额必须大于 0");
        }
        if (req.getTotalAmount().scale() > 2) {
            throw new BizException("保函金额最多保留 2 位小数");
        }
        if (req.getTotalAmount().compareTo(MAX_TOTAL_AMOUNT) > 0) {
            throw new BizException("保函金额最大值不能超过 10 亿");
        }

        // 换算订单额度比例:> 0,≤ 1,最多 2 位小数
        if (req.getConversionRatio() == null) {
            throw new BizException("换算订单额度比例不能为空");
        }
        if (req.getConversionRatio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("换算订单额度比例必须大于 0");
        }
        if (req.getConversionRatio().scale() > 2) {
            throw new BizException("换算订单额度比例最多保留 2 位小数");
        }
        if (req.getConversionRatio().compareTo(BigDecimal.ONE) > 0) {
            throw new BizException("换算订单额度比例不能大于 1");
        }

        // 日期关系:生效日期 < 失效日期,失效日期 > 当天
        if (!req.getEffectiveDate().isBefore(req.getExpiryDate())) {
            throw new BizException("生效日期必须小于失效日期");
        }
        if (!req.getExpiryDate().isAfter(java.time.LocalDate.now())) {
            throw new BizException("失效日期必须大于当天");
        }
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
        String remark = req.getRemark();
        return runBatch(ids, id -> exportVehicleLetterGuaranteeCmdExe.submit(id, remark));
    }

    @Override
    public ExportVehicleLetterGuaranteeBatchStateMachineResp approve(ExportVehicleLetterGuaranteeApproveReq req) {
        Boolean approvedFlag = req.getApproved();
        if (approvedFlag == null) {
            throw new BizException("审核结论(approved)不能为空");
        }
        boolean approved = approvedFlag;
        if (!approved) {
            // 驳回时审核意见必填且 ≤ 200
            requireNotBlank(req.getApproveRemark(), "驳回原因");
            requireMaxLength(req.getApproveRemark(), APPROVE_REMARK_MAX_LENGTH, "驳回原因");
        }

        List<String> ids = resolveIds(req.getId(), req.getIds());
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
        return exportVehicleLetterGuaranteeCmdExe.expireEarly(req.getId(), req.getRemark());
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
    public ExportVehicleLetterGuaranteeFreezeResp freeze(ExportVehicleLetterGuaranteeFreezeReq req) {
        return exportVehicleLetterGuaranteeCmdExe.freeze(req);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeFreezeResp> batchFreeze(List<ExportVehicleLetterGuaranteeFreezeReq> list) {
        return exportVehicleLetterGuaranteeCmdExe.batchFreeze(list);
    }

    @Override
    public boolean unfreeze(ExportVehicleLetterGuaranteeUnfreezeReq req) {
        return exportVehicleLetterGuaranteeCmdExe.unfreeze(req);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeUnfreezeResp> batchUnfreeze(List<ExportVehicleLetterGuaranteeUnfreezeReq> list) {
        return exportVehicleLetterGuaranteeCmdExe.batchUnfreeze(list);
    }

    @Override
    public boolean transferToReceivable(ExportVehicleLetterGuaranteeTransferReq req) {
        return exportVehicleLetterGuaranteeCmdExe.transferToReceivable(req);
    }

    @Override
    public boolean releaseReceivable(ExportVehicleLetterGuaranteeReleaseReq req) {
        return exportVehicleLetterGuaranteeCmdExe.releaseReceivable(req);
    }

    // ============== 刷新订单价格联动(§3.5) ==============

    /**
     * 调增补冻(§3.5.2):
     * 与 {@link #freeze} 算法相同,仅入参语义差异(amountDiff 作为补冻结金额)。
     * 在 Service 层映射为 {@link ExportVehicleLetterGuaranteeFreezeReq} 复用 freeze 流程,避免重复实现排序与拆冻。
     */
    @Override
    public ExportVehicleLetterGuaranteeFreezeResp adjustFreeze(ExportVehicleLetterGuaranteeAdjustFreezeReq req) {
        if (req.getAmountDiff() == null || req.getAmountDiff().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("amountDiff 必须大于 0");
        }
        ExportVehicleLetterGuaranteeFreezeReq fr = new ExportVehicleLetterGuaranteeFreezeReq();
        fr.setCustomerId(req.getCustomerId());
        fr.setOrderType(req.getOrderType());
        fr.setOrderId(req.getOrderId());
        fr.setOrderNo(req.getOrderNo());
        fr.setPoNo(req.getPoNo());
        fr.setRequiredAmount(req.getAmountDiff());
        fr.setOrderCreateTime(req.getOrderCreateTime());
        fr.setRemark(req.getReason());
        ExportVehicleLetterGuaranteeFreezeResp resp = exportVehicleLetterGuaranteeCmdExe.freeze(fr);
        // 调增场景无 separate reason 文案,沿用 freeze 自身的 success/fail 字段
        return resp;
    }

    /**
     * 调减退差(§3.5.3):CmdExe.adjustUnfreeze 内部完成保函选择与单张解冻。
     */
    @Override
    public ExportVehicleLetterGuaranteeAdjustUnfreezeResp adjustUnfreeze(ExportVehicleLetterGuaranteeAdjustUnfreezeReq req) {
        return exportVehicleLetterGuaranteeCmdExe.adjustUnfreeze(req);
    }

    // ============== 查询 ==============

    /**
     * 资金校验(§8.5.1) — 校验 PO 所需金额是否可由客户可用保函覆盖。
     * <p>coverDays 校验(失效日期 ≥ 订单创建时间 + coverDays,默认 100 天)已下沉到
     * {@code listAvailableForCustomer} 的 SQL 层:把 {@code orderCreateTime} 透传给查询,
     * 由 Gateway 在 Qo.expiryDateGe 上做 {@code expiry_date >= orderCreateTime + coverDays} 过滤。
     */
    @Override
    public ExportVehicleLetterGuaranteeValidateResp validateForPo(ExportVehicleLetterGuaranteeFreezeReq req) {
        ExportVehicleLetterGuaranteeValidateResp resp = new ExportVehicleLetterGuaranteeValidateResp();
        ExportVehicleLetterGuaranteeListAvailableReq listReq = new ExportVehicleLetterGuaranteeListAvailableReq();
        listReq.setCustomerId(req.getCustomerId());
        listReq.setOrderType(req.getOrderType());
        listReq.setRequiredAmount(req.getRequiredAmount());
        listReq.setOrderCreateTime(req.getOrderCreateTime());
        List<ExportVehicleLetterGuaranteeDo> candidates = exportVehicleLetterGuaranteeQryExe.listAvailableForCustomer(listReq);
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
        // 列出所有满足 a~e 的保函,f 由调用方累加 SUM(availableAmount) 与 requiredAmount 比较
        BigDecimal totalAvailable = candidates.stream()
                .map(ExportVehicleLetterGuaranteeDo::getAvailableAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resp.setTotalAvailable(totalAvailable);
        if (totalAvailable.compareTo(req.getRequiredAmount()) < 0) {
            resp.setPassed(false);
            resp.setFailCode("dict.letterGuarantee.validate.fail.insufficient");
            resp.setFailMessage("提交失败,资金校验未通过,请联系商务。可用保函总金额=" + totalAvailable + ", 订单总金额=" + req.getRequiredAmount());
            return resp;
        }

        resp.setPassed(true);
        return resp;
    }

    // ============== 冗余字段填充(客户名称) ==============

    /**
     * 单条 Resp 填充 customerCode/customerName(查本地缓存,未命中跳过)。
     */
    private void fillCustomerInfo(ExportVehicleLetterGuaranteeResp resp) {
        if (resp == null || resp.getCustomerId() == null) {
            return;
        }
        CustomerInfoCache.Item item = customerInfoCache.getById(resp.getCustomerId());
        if (item != null) {
            resp.setCustomerCode(item.getCustomerCode());
            resp.setCustomerName(item.getCustomerName());
        }
    }

    /**
     * 批量 Resp 填充:收集 customerId → 一次 multiGet → 写回。
     */
    private void fillCustomerInfo(List<ExportVehicleLetterGuaranteeResp> resps) {
        if (resps == null || resps.isEmpty()) {
            return;
        }
        Set<String> ids = new HashSet<>();
        for (ExportVehicleLetterGuaranteeResp r : resps) {
            if (r != null && r.getCustomerId() != null) {
                ids.add(r.getCustomerId());
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        Map<String, CustomerInfoCache.Item> map = customerInfoCache.getByIds(ids);
        if (map.isEmpty()) {
            return;
        }
        for (ExportVehicleLetterGuaranteeResp r : resps) {
            if (r == null || r.getCustomerId() == null) {
                continue;
            }
            CustomerInfoCache.Item item = map.get(r.getCustomerId());
            if (item != null) {
                r.setCustomerCode(item.getCustomerCode());
                r.setCustomerName(item.getCustomerName());
            }
        }
    }
}
