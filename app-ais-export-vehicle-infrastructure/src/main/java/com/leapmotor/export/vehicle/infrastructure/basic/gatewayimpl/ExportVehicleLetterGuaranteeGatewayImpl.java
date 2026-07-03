package com.leapmotor.export.vehicle.infrastructure.basic.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import com.leapmotor.export.vehicle.infrastructure.basic.convertor.ExportVehicleLetterGuaranteeStructMapper;
import com.leapmotor.export.vehicle.infrastructure.basic.dao.ExportVehicleLetterGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.basic.entity.ExportVehicleLetterGuarantee;
import com.leapmotor.export.vehicle.infrastructure.basic.mapper.ExportVehicleLetterGuaranteeMapper;
import com.leapmotor.export.vehicle.letterGuarantee.enums.ApprovalStatusEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.LgStatusEnum;
import com.leapmotor.export.vehicle.letterGuarantee.enums.RecordTypeEnum;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.user.LoginUser;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 保函主表领域网关实现
 * <p>
 * 业务规则(详见 docs/保函管理-设计文档.md §4 + §11.1):
 * - 状态机方法 (submit/approve/cancel/expireEarly): 校验当前状态,合法才迁移
 * - 资金流 (freeze/unfreeze/transferToReceivable/releaseReceivable): 三态(可用/占用/应收)金额守恒,写流水
 * - 自动生成明细编号 BK+yyyyMMdd+HHmmss+5位流水
 * <p>
 * 注意:事务边界由调用方 (CmdExe) 提供 @Transactional,本类不重复声明。
 * 无 Redis 分布式锁(项目暂未集成),依赖数据库事务 + 行锁 + 状态机校验做并发控制。
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@Slf4j
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeGatewayImpl implements ExportVehicleLetterGuaranteeGateway {

    private static final java.util.concurrent.atomic.AtomicLong SEQ = new java.util.concurrent.atomic.AtomicLong(0);
    private ExportVehicleLetterGuaranteeMapper exportVehicleLetterGuaranteeMapper;
    private ExportVehicleLetterGuaranteeDao exportVehicleLetterGuaranteeDao;

    // ============== 通用 CRUD(由生成器生成,保持原样) ==============
    private ExportVehicleLetterGuaranteeRecordGateway recordGateway;

    @Override
    public String addExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = ExportVehicleLetterGuaranteeStructMapper.INSTANCE.domainToDataObject(exportVehicleLetterGuaranteeDo);
        int result = exportVehicleLetterGuaranteeMapper.insert(exportVehicleLetterGuarantee);
        exportVehicleLetterGuaranteeDo.setId(exportVehicleLetterGuarantee.getId());
        return result > 0 ? exportVehicleLetterGuarantee.getId() : null;
    }

    @Override
    public int addBatchExportVehicleLetterGuarantee(List<ExportVehicleLetterGuaranteeDo> exportVehicleLetterGuaranteeDos) {
        List<ExportVehicleLetterGuarantee> exportVehicleLetterGuarantees =
                ExportVehicleLetterGuaranteeStructMapper.INSTANCE.domainToDataObjects(exportVehicleLetterGuaranteeDos);
        return exportVehicleLetterGuaranteeDao.batchSaveSomeColumn(exportVehicleLetterGuarantees);
    }

    @Override
    public String updateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = ExportVehicleLetterGuaranteeStructMapper.INSTANCE.domainToDataObject(exportVehicleLetterGuaranteeDo);
        int result = exportVehicleLetterGuaranteeMapper.updateById(exportVehicleLetterGuarantee);
        return result > 0 ? exportVehicleLetterGuarantee.getId() : null;
    }

    @Override
    public ExportVehicleLetterGuaranteeDo getExportVehicleLetterGuaranteeById(String id) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = exportVehicleLetterGuaranteeDao.getById(id);
        return ExportVehicleLetterGuaranteeStructMapper.INSTANCE.dataObjectToDomain(exportVehicleLetterGuarantee);
    }

    @Override
    public ExportVehicleLetterGuaranteeDo getOneExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = exportVehicleLetterGuaranteeDao.getOneForceLimit(exportVehicleLetterGuaranteeQo);
        return ExportVehicleLetterGuaranteeStructMapper.INSTANCE.dataObjectToDomain(exportVehicleLetterGuarantee);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeDo> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo) {
        List<ExportVehicleLetterGuarantee> exportVehicleLetterGuarantees = exportVehicleLetterGuaranteeDao.queryList(exportVehicleLetterGuaranteeQo);
        return ExportVehicleLetterGuaranteeStructMapper.INSTANCE.dataObjectsToDomains(exportVehicleLetterGuarantees);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeDo> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo) {
        Page<ExportVehicleLetterGuarantee> page = exportVehicleLetterGuaranteeDao.findPage(exportVehicleLetterGuaranteeQo);
        return PageUtils.transferPage(ExportVehicleLetterGuaranteeStructMapper.INSTANCE::dataObjectsToDomains, page);
    }

    // ============== 状态机 ==============

    @Override
    public boolean deleteExportVehicleLetterGuaranteeById(String id) {
        return exportVehicleLetterGuaranteeDao.removeById(id);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeByIds(Collection<String> ids) {
        return exportVehicleLetterGuaranteeDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = new ExportVehicleLetterGuarantee();
        exportVehicleLetterGuarantee.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            exportVehicleLetterGuarantee.setId(id);
            exportVehicleLetterGuaranteeDao.updateById(exportVehicleLetterGuarantee);
        }
        return true;
    }

    @Override
    public boolean submit(String id) {
        ExportVehicleLetterGuaranteeDo lg = mustGet(id);
        String cur = lg.getApprovalStatus();
        boolean isDraft = ApprovalStatusEnum.DRAFT.getCode().equals(cur);
        boolean isRejected = ApprovalStatusEnum.REJECTED.getCode().equals(cur);
        if (!isDraft && !isRejected) {
            throw new BizException("仅草稿(10)/驳回(40)可提交,当前状态: " + cur);
        }
        LoginUser user = LoginUserUtils.getCurrentUserOrSysDefault();
        lg.setApprovalStatus(ApprovalStatusEnum.SUBMITTED.getCode());
        lg.setSubmitterId(user.getUserId());
        lg.setSubmitterName(user.getUserName());
        lg.setSubmitTime(LocalDateTime.now());
        // lg_status 保持 10(未生效)
        return updateExportVehicleLetterGuarantee(lg) != null;
    }

    // ============== 资金流(三态金额守恒,写流水) ==============

    @Override
    public boolean approve(String id, boolean approved, String approveRemark) {
        ExportVehicleLetterGuaranteeDo lg = mustGet(id);
        if (!ApprovalStatusEnum.SUBMITTED.getCode().equals(lg.getApprovalStatus())) {
            throw new BizException("仅已提交可审核,当前状态: " + lg.getApprovalStatus());
        }
        if (approved) {
            lg.setApprovalStatus(ApprovalStatusEnum.APPROVED.getCode());
            lg.setLgStatus(LgStatusEnum.EFFECTIVE.getCode()); // 20 有效
        } else {
            lg.setApprovalStatus(ApprovalStatusEnum.REJECTED.getCode());
            // lg_status 保持 10(未生效)
        }
        LoginUser user = LoginUserUtils.getCurrentUserOrSysDefault();
        lg.setApproverId(user.getUserId());
        lg.setApproverName(user.getUserName());
        lg.setApproveTime(LocalDateTime.now());
        lg.setApproveRemark(approveRemark);
        return updateExportVehicleLetterGuarantee(lg) != null;
    }

    @Override
    public boolean cancel(String id, String remark) {
        ExportVehicleLetterGuaranteeDo lg = mustGet(id);
        String cur = lg.getApprovalStatus();
        boolean isDraft = ApprovalStatusEnum.DRAFT.getCode().equals(cur);
        boolean isRejected = ApprovalStatusEnum.REJECTED.getCode().equals(cur);
        if (!isDraft && !isRejected) {
            throw new BizException("仅草稿/驳回可作废,当前状态: " + cur);
        }
        lg.setApprovalStatus(ApprovalStatusEnum.CANCELLED.getCode());
        // lg_status 保持 10
        lg.setRemark(remark);
        return updateExportVehicleLetterGuarantee(lg) != null;
    }

    @Override
    public boolean expireEarly(String id, String reason) {
        ExportVehicleLetterGuaranteeDo lg = mustGet(id);
        if (!ApprovalStatusEnum.APPROVED.getCode().equals(lg.getApprovalStatus())) {
            throw new BizException("仅审核通过的保函可手动失效,当前审批状态: " + lg.getApprovalStatus());
        }
        if (!LgStatusEnum.EFFECTIVE.getCode().equals(lg.getLgStatus())) {
            throw new BizException("仅有效保函可手动失效,当前保函状态: " + lg.getLgStatus());
        }
        // 先捕获变化前金额(主表随后被 update 改写,直接读 lg 会得到 after)
        BigDecimal beforeAvailable = lg.getAvailableAmount() == null ? BigDecimal.ZERO : lg.getAvailableAmount();
        // 可用金额归零(若有),占用/应收保留继续走完转应收/释放应收
        BigDecimal afterAvailable = BigDecimal.ZERO;
        if (beforeAvailable.compareTo(BigDecimal.ZERO) != 0) {
            lg.setAvailableAmount(afterAvailable);
        }
        lg.setLgStatus(LgStatusEnum.INVALID.getCode()); // 30 失效
        String updateResult = updateExportVehicleLetterGuarantee(lg);
        if (updateResult != null) {
            // 写失效流水 record_type=50(可用→0 系统调整;占用/应收保留)
            writeRecord(lg, RecordTypeEnum.EXPIRE.getCode(), beforeAvailable,
                    beforeAvailable, afterAvailable, null, null, null, null,
                    null, null, null, reason, null, null);
        }
        return updateResult != null;
    }

    @Override
    public boolean freeze(String lgId, BigDecimal amount, String orderId, String orderNo,
                          String poNo, String soNo, String vin, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("冻结金额必须大于 0");
        }
        ExportVehicleLetterGuaranteeDo lg = mustGet(lgId);
        ensureEffective(lg);
        BigDecimal beforeAvailable = lg.getAvailableAmount() == null ? BigDecimal.ZERO : lg.getAvailableAmount();
        BigDecimal beforeOccupied = lg.getOccupiedAmount() == null ? BigDecimal.ZERO : lg.getOccupiedAmount();
        BigDecimal beforeReceivable = lg.getReceivableAmount() == null ? BigDecimal.ZERO : lg.getReceivableAmount();
        if (beforeAvailable.compareTo(amount) < 0) {
            throw new BizException("可用金额不足: 可用=" + beforeAvailable + ", 申请=" + amount);
        }
        BigDecimal afterAvailable = beforeAvailable.subtract(amount);
        BigDecimal afterOccupied = beforeOccupied.add(amount);
        lg.setAvailableAmount(afterAvailable);
        lg.setOccupiedAmount(afterOccupied);
        if (updateExportVehicleLetterGuarantee(lg) != null) {
            writeRecord(lg, RecordTypeEnum.FREEZE.getCode(), amount,
                    beforeAvailable, afterAvailable,
                    beforeOccupied, afterOccupied,
                    beforeReceivable, beforeReceivable, // 应收未变
                    poNo, soNo, vin, remark, orderId, orderNo);
            return true;
        }
        return false;
    }

    // ============== 查询 ==============

    @Override
    public boolean unfreeze(String lgId, BigDecimal amount, String orderId, String poNo, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("解冻金额必须大于 0");
        }
        ExportVehicleLetterGuaranteeDo lg = mustGet(lgId);
        ensureEffective(lg);
        BigDecimal beforeAvailable = lg.getAvailableAmount() == null ? BigDecimal.ZERO : lg.getAvailableAmount();
        BigDecimal beforeOccupied = lg.getOccupiedAmount() == null ? BigDecimal.ZERO : lg.getOccupiedAmount();
        BigDecimal beforeReceivable = lg.getReceivableAmount() == null ? BigDecimal.ZERO : lg.getReceivableAmount();
        if (beforeOccupied.compareTo(amount) < 0) {
            throw new BizException("占用金额不足: 占用=" + beforeOccupied + ", 申请解冻=" + amount);
        }
        BigDecimal afterAvailable = beforeAvailable.add(amount);
        BigDecimal afterOccupied = beforeOccupied.subtract(amount);
        lg.setOccupiedAmount(afterOccupied);
        lg.setAvailableAmount(afterAvailable);
        if (updateExportVehicleLetterGuarantee(lg) != null) {
            writeRecord(lg, RecordTypeEnum.UNFREEZE.getCode(), amount,
                    beforeAvailable, afterAvailable,
                    beforeOccupied, afterOccupied,
                    beforeReceivable, beforeReceivable, // 应收未变
                    poNo, null, null, reason, orderId, null);
            return true;
        }
        return false;
    }

    @Override
    public boolean transferToReceivable(String lgId, BigDecimal amount, String orderId,
                                        String soNo, String vin, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("转应收金额必须大于 0");
        }
        ExportVehicleLetterGuaranteeDo lg = mustGet(lgId);
        ensureEffective(lg);
        BigDecimal beforeAvailable = lg.getAvailableAmount() == null ? BigDecimal.ZERO : lg.getAvailableAmount();
        BigDecimal beforeOccupied = lg.getOccupiedAmount() == null ? BigDecimal.ZERO : lg.getOccupiedAmount();
        BigDecimal beforeReceivable = lg.getReceivableAmount() == null ? BigDecimal.ZERO : lg.getReceivableAmount();
        if (beforeOccupied.compareTo(amount) < 0) {
            throw new BizException("占用金额不足: 占用=" + beforeOccupied + ", 申请转应收=" + amount);
        }
        BigDecimal afterOccupied = beforeOccupied.subtract(amount);
        BigDecimal afterReceivable = beforeReceivable.add(amount);
        lg.setOccupiedAmount(afterOccupied);
        lg.setReceivableAmount(afterReceivable);
        if (updateExportVehicleLetterGuarantee(lg) != null) {
            writeRecord(lg, RecordTypeEnum.TRANSFER_RECEIVABLE.getCode(), amount,
                    beforeAvailable, beforeAvailable, // 可用未变
                    beforeOccupied, afterOccupied,
                    beforeReceivable, afterReceivable,
                    null, soNo, vin, reason, orderId, null);
            return true;
        }
        return false;
    }

    // ============== 内部工具 ==============

    @Override
    public boolean releaseReceivable(String lgId, BigDecimal amount, String orderId,
                                     String soNo, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException("释放应收金额必须大于 0");
        }
        ExportVehicleLetterGuaranteeDo lg = mustGet(lgId);
        BigDecimal beforeAvailable = lg.getAvailableAmount() == null ? BigDecimal.ZERO : lg.getAvailableAmount();
        BigDecimal beforeOccupied = lg.getOccupiedAmount() == null ? BigDecimal.ZERO : lg.getOccupiedAmount();
        BigDecimal beforeReceivable = lg.getReceivableAmount() == null ? BigDecimal.ZERO : lg.getReceivableAmount();
        if (beforeReceivable.compareTo(amount) < 0) {
            throw new BizException("应收金额不足: 应收=" + beforeReceivable + ", 申请释放=" + amount);
        }
        // 释放应收:应收 - amount(保函不补回,核销完毕)
        BigDecimal afterReceivable = beforeReceivable.subtract(amount);
        lg.setReceivableAmount(afterReceivable);
        if (updateExportVehicleLetterGuarantee(lg) != null) {
            writeRecord(lg, RecordTypeEnum.RELEASE_RECEIVABLE.getCode(), amount,
                    beforeAvailable, beforeAvailable, // 可用未变
                    beforeOccupied, beforeOccupied,   // 占用未变
                    beforeReceivable, afterReceivable,
                    null, null, null, reason, orderId, soNo);
            return true;
        }
        return false;
    }

    @Override
    public List<ExportVehicleLetterGuaranteeDo> listAvailableForCustomer(String customerId, String orderType, BigDecimal requiredAmount) {
        if (!StringUtils.hasText(customerId)) {
            throw new BizException("客户ID不能为空");
        }
        ExportVehicleLetterGuaranteeQo qo = new ExportVehicleLetterGuaranteeQo();
        qo.setCustomerId(customerId);
        qo.setOrderType(orderType);
        qo.setApprovalStatus(ApprovalStatusEnum.APPROVED.getCode());
        qo.setLgStatus(LgStatusEnum.EFFECTIVE.getCode());
        // 可按金额过滤;为简化,通过 list 后内存过滤
        List<ExportVehicleLetterGuaranteeDo> all = listExportVehicleLetterGuarantee(qo);
        if (requiredAmount == null || requiredAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return all;
        }
        return all.stream()
                .filter(lg -> lg.getAvailableAmount() != null && lg.getAvailableAmount().compareTo(requiredAmount) >= 0)
                .sorted((a, b) -> b.getAvailableAmount().compareTo(a.getAvailableAmount()))
                .toList();
    }

    @Override
    public List<ExportVehicleLetterGuaranteeDo> listExpiredBefore(java.time.LocalDate date) {
        if (date == null) {
            date = java.time.LocalDate.now();
        }
        // 直接基于 Entity 查(凌晨任务用,内部基础设施层操作)
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ExportVehicleLetterGuarantee> qw =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        qw.eq("approval_status", ApprovalStatusEnum.APPROVED.getCode());
        qw.eq("lg_status", LgStatusEnum.EFFECTIVE.getCode());
        qw.lt("expiry_date", date);
        // ServiceImpl 提供 list API;这里走 queryList
        com.baomidou.mybatisplus.extension.service.IService<ExportVehicleLetterGuarantee> svc = (com.baomidou.mybatisplus.extension.service.IService<ExportVehicleLetterGuarantee>) exportVehicleLetterGuaranteeDao;
        @SuppressWarnings("unchecked")
        List<ExportVehicleLetterGuarantee> entities = svc.list(qw);
        if (entities == null || entities.isEmpty()) return java.util.Collections.emptyList();
        return entities.stream()
                .map(ExportVehicleLetterGuaranteeStructMapper.INSTANCE::dataObjectToDomain)
                .toList();
    }

    private ExportVehicleLetterGuaranteeDo mustGet(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BizException("保函ID不能为空");
        }
        ExportVehicleLetterGuaranteeDo lg = getExportVehicleLetterGuaranteeById(id);
        if (lg == null) {
            throw new BizException("保函不存在: id=" + id);
        }
        return lg;
    }

    private void ensureEffective(ExportVehicleLetterGuaranteeDo lg) {
        if (!ApprovalStatusEnum.APPROVED.getCode().equals(lg.getApprovalStatus())) {
            throw new BizException("仅审核通过的保函可资金操作,当前审批状态: " + lg.getApprovalStatus());
        }
        if (!LgStatusEnum.EFFECTIVE.getCode().equals(lg.getLgStatus())) {
            throw new BizException("仅有效保函可资金操作,当前保函状态: " + lg.getLgStatus());
        }
    }

    /**
     * 写资金变动流水(主表 + 流水表同事务)。
     * <p>操作人(creator/creatorName)由本方法内部通过
     * {@code LoginUserUtils.getCurrentUserOrSysDefault()} 获取,无需调用方传入。
     * <p>所有金额前后值由调用方显式传入:主表已被本次调用的 update 改写,直接读 lg
     * 拿到的不是 before 而是 after,会出现流水 before==after 的错误。
     *
     * @param transactionAmount 本次变动金额(可空,系统调整类如 expire 写原余额)
     * @param beforeAvailable   变动前可用金额
     * @param afterAvailable    变动后可用金额
     * @param beforeOccupied    变动前占用金额
     * @param afterOccupied     变动后占用金额
     * @param beforeReceivable  变动前应收金额
     * @param afterReceivable   变动后应收金额
     * @param orderId           订单ID(可空,部分流水如 expireEarly 无订单)
     * @param orderNo           订单号(可空)
     */
    private void writeRecord(ExportVehicleLetterGuaranteeDo lg, String recordType,
                             BigDecimal transactionAmount,
                             BigDecimal beforeAvailable, BigDecimal afterAvailable,
                             BigDecimal beforeOccupied, BigDecimal afterOccupied,
                             BigDecimal beforeReceivable, BigDecimal afterReceivable,
                             String poNo, String soNo, String vin,
                             String remark, String orderId, String orderNo) {
        ExportVehicleLetterGuaranteeRecordDo record = new ExportVehicleLetterGuaranteeRecordDo();
        record.setId(UUID.randomUUID().toString().replace("-", ""));
        record.setLgRecordCode(genRecordCode());
        record.setLgId(lg.getId());
        record.setLgCode(lg.getLgCode());
        record.setLgType(lg.getLgType());
        record.setCustomerId(lg.getCustomerId());
        record.setTransactionType(recordType);
        record.setTransactionAmount(transactionAmount);
        record.setTransactionRemark(remark);
        record.setBeforeAvailable(beforeAvailable);
        record.setAfterAvailable(afterAvailable);
        record.setBeforeOccupied(beforeOccupied);
        record.setAfterOccupied(afterOccupied);
        record.setBeforeReceivable(beforeReceivable);
        record.setAfterReceivable(afterReceivable);
        record.setPoNo(poNo);
        record.setSoNo(soNo);
        record.setVin(vin);
        LoginUser user = LoginUserUtils.getCurrentUserOrSysDefault();
        record.setCreator(user.getUserId());
        record.setCreatorName(user.getUserName());
        record.setCreateTime(LocalDateTime.now());
        // 沿用主保函归属字段
        record.setOwnerUserId(lg.getOwnerUserId());
        record.setOwnerOrgId(lg.getOwnerOrgId());
        recordGateway.addExportVehicleLetterGuaranteeRecord(record);
    }


    /**
     * 生成明细编号 BK+yyyyMMdd+HHmmss+5位流水(同毫秒+自增滚动防冲突)
     */
    private String genRecordCode() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String ts = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long seq = SEQ.incrementAndGet() % 100000L;
        return String.format("BK%s%05d", ts, seq);
    }
}
