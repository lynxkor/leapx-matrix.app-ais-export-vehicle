package com.leapmotor.export.vehicle.so.executor.cmd;

import com.leapmotor.export.vehicle.acl.http.aps.gateway.ApsGateway;
import com.leapmotor.export.vehicle.acl.http.aps.req.ApsCancelReq;
import com.leapmotor.export.vehicle.acl.http.aps.req.ApsIssueReq;
import com.leapmotor.export.vehicle.acl.http.aps.resp.ApsResult;
import com.leapmotor.export.vehicle.acl.http.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.http.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoCancelReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoIssueReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.user.LoginUser;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import com.leapmotor.platform.ais.common.redis.annotation.DistributeLock;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * VehicleCbuSo 写操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Slf4j
@AllArgsConstructor
@Component
public class VehicleCbuSoCmdExe {

    /**
     * 红旗接口日期格式：yyyyMMdd
     */
    private static final DateTimeFormatter HONGQI_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    /**
     * 红旗接口日期时间格式：yyyyMMddHHmmss
     */
    private static final DateTimeFormatter HONGQI_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final VehicleCbuSoGateway vehicleCbuSoGateway;
    private final VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;
    private final ApsGateway apsGateway;
    private final VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;
    private final EventHandlerHelper eventHandlerHelper;
    @Qualifier("taskExecutor")
    private final TaskExecutor taskExecutor;
    private final TransactionTemplate transactionTemplate;

    public String addOrUpdateVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo) {
        return vehicleCbuSoDo.getId() == null ? vehicleCbuSoGateway.addVehicleCbuSo(vehicleCbuSoDo) : vehicleCbuSoGateway.updateVehicleCbuSo(vehicleCbuSoDo);
    }

    public boolean deleteVehicleCbuSoByIds(List<String> ids) {
        return vehicleCbuSoGateway.deleteVehicleCbuSoByIds(ids);
    }

    public boolean enableOrDisableVehicleCbuSoByIds(LpBaseParam param) {
        return vehicleCbuSoGateway.enableOrDisableVehicleCbuSoByIds(param);
    }

    /**
     * 根据 PO 创建 CBU SO：一车一单拆分、生成单号/行号、主明细同事务保存。
     *
     * @param req PO 创建入参
     * @return 创建结果（PO 标识 + SO 列表）
     */
    @Transactional(rollbackFor = Exception.class)
    public SoResult createSo(SoCreateReq req) {
        if (req == null || StringUtils.isBlank(req.getPoNo())) {
            throw new BizException("PO单号不能为空");
        }
        if (CollectionUtils.isEmpty(req.getDetails())) {
            throw new BizException("PO明细不能为空");
        }

        // 幂等校验：同一 PO 不允许重复创建 SO
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setPoNo(req.getPoNo());
        if (CollectionUtils.isNotEmpty(vehicleCbuSoGateway.listVehicleCbuSo(qo))) {
            throw new BizException("PO单号[" + req.getPoNo() + "]已存在SO，不允许重复创建");
        }

        // 1) build 阶段：Pair 锁住 1:1，类型层面保证主明细对齐
        List<Pair<VehicleCbuSoDo, VehicleCbuSoDetailDo>> pairs = new ArrayList<>();
        int lineNo = 1;
        for (SoCreateReq.SoDetailReq detailReq : req.getDetails()) {
            if (detailReq == null || detailReq.getDemandQuantity() == null || detailReq.getDemandQuantity().intValue() <= 0) {
                continue;
            }
            int quantity = detailReq.getDemandQuantity().intValue();
            for (int i = 0; i < quantity; i++) {
                pairs.add(Pair.of(buildSo(req, detailReq, lineNo), buildSoDetail(detailReq)));
                lineNo++;
            }
        }
        if (pairs.isEmpty()) {
            throw new BizException("未生成任何SO，请检查PO明细需求数量");
        }

        // 2) 拆出 soList / detailList（与 pairs 共享对象引用，gateway 回写主表 id 时同步反映到 pairs.getLeft()）
        List<VehicleCbuSoDo> soList = new ArrayList<>(pairs.size());
        List<VehicleCbuSoDetailDo> detailList = new ArrayList<>(pairs.size());
        for (Pair<VehicleCbuSoDo, VehicleCbuSoDetailDo> p : pairs) {
            soList.add(p.getLeft());
            detailList.add(p.getRight());
        }

        // 3) 批量保存主表：so_no 已在 buildSo 用 NoGenerator 显式生成；
        // 平台 LpBaseEntity 自动生成主键并回写到 soList（即 pairs.getLeft()）。
        vehicleCbuSoGateway.batchAddVehicleCbuSo(soList);

        // 4) 回写主表 id/soNo 到明细：明细表 1:1 复用主表 id 作为自身主键，并按 so_no 关联主表
        for (Pair<VehicleCbuSoDo, VehicleCbuSoDetailDo> p : pairs) {
            p.getRight().setId(p.getLeft().getId());
            p.getRight().setSoNo(p.getLeft().getSoNo());
        }

        // 5) 批量保存车辆明细表
        vehicleCbuSoDetailGateway.batchAddVehicleCbuSoDetail(detailList);

        // 6) 拼装返回值：PO 标识放顶层，SO 列表放 data
        SoResult result = new SoResult();
        result.setPoId(req.getPoId());
        result.setPoNo(req.getPoNo());
        result.setData(soList.stream().map(this::toResultItem).collect(Collectors.toList()));
        return result;
    }

    /**
     * 从 soList 投影 SoItem（仅含 SO 维度字段，PO 标识由 SoResult.poId/poNo 统一承载）
     */
    private SoResult.SoItem toResultItem(VehicleCbuSoDo so) {
        SoResult.SoItem item = new SoResult.SoItem();
        item.setLineNo(so.getLineNo());
        item.setOrderStatus(so.getOrderStatus());
        item.setSoId(so.getId());
        item.setSoNo(so.getSoNo());
        return item;
    }

    /**
     * 生成 SO 单号：SO + yyyyMMddHHmm + 5位流水（同 po_detail_no）
     */
    private String genSoNo() {
        return "SO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + NoGenerator.genNoOnly("SO", "yyyyMMddHHmm", 5);
    }

    /**
     * 构建 SO
     */
    private VehicleCbuSoDo buildSo(SoCreateReq req, SoCreateReq.SoDetailReq detailReq, int lineNo) {
        VehicleCbuSoDo so = new VehicleCbuSoDo();
        so.setSoNo(genSoNo());
        so.setPoId(req.getPoId());
        so.setPoNo(req.getPoNo());
        so.setPoDetailId(detailReq.getPoDetailId());
        so.setPoDetailNo(detailReq.getPoDetailNo());
        so.setLineNo(lineNo);
        so.setSalesCompanyCode(req.getSalesCompanyCode());
        so.setCustomerCode(req.getCustomerCode());
        so.setCustomerName(req.getCustomerName());
        so.setExternalCustomerCode(req.getExternalCustomerCode());
        so.setOrderType(req.getOrderType());
        so.setPlanVersion(req.getPlanVersion());
        so.setExpectedOfflineDate(detailReq.getExpectedOfflineDate());
        so.setVinYear(detailReq.getVinYear());
        so.setProductionRemark(detailReq.getProductionRemark());
        so.setVehiclePrice(detailReq.getUnitPrice());
        so.setFrozenAmount(detailReq.getUnitPrice());
        so.setReceivableAmount(BigDecimal.ZERO);
        so.setDataSource(req.getDataSource());
        so.setOrderStatus(VehicleCbuSoStatusEnum.NOT_ISSUED.getCode());
        so.setStatus(1);
        return so;
    }

    /**
     * 构建 SO 明细
     */
    private VehicleCbuSoDetailDo buildSoDetail(SoCreateReq.SoDetailReq detailReq) {
        VehicleCbuSoDetailDo soDetail = new VehicleCbuSoDetailDo();
        soDetail.setMaterialCode(detailReq.getMaterialCode());
        soDetail.setMaterialDescription(detailReq.getMaterialDescription());
        soDetail.setStatus(1);
        return soDetail;
    }

    /**
     * 根据 PO 单号查询 SO 列表（供 PO 侧内部调用）。
     */
    public List<VehicleCbuSoDo> listVehicleCbuSoByPoNo(String poNo) {
        if (StringUtils.isBlank(poNo)) {
            throw new BizException("PO单号不能为空");
        }
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setPoNo(poNo);
        return vehicleCbuSoGateway.listVehicleCbuSo(qo);
    }

    /**
     * 按 PO 刷新 SO 车辆价/冻结金额：查 PO 下 SO，按 poDetailId 匹配明细新单价更新。
     * 调用方（refreshPrice 分支B）已校验 SO 非质检通过/已收款；作废 SO(1012) 跳过。
     */
    public void updatePrice(SoUpdatePriceReq req) {
        if (req == null || StringUtils.isBlank(req.getPoNo())) {
            throw new BizException("PO单号不能为空");
        }
        List<VehicleCbuSoDo> sos = listVehicleCbuSoByPoNo(req.getPoNo());
        if (CollectionUtils.isEmpty(sos) || CollectionUtils.isEmpty(req.getDetails())) {
            return;
        }
        for (VehicleCbuSoDo so : sos) {
            if (Integer.valueOf(1012).equals(so.getOrderStatus()) || so.getPoDetailId() == null) {
                continue;
            }
            for (SoUpdatePriceReq.DetailPrice dp : req.getDetails()) {
                if (so.getPoDetailId().equals(dp.getPoDetailId()) && dp.getUnitPrice() != null) {
                    VehicleCbuSoDo update = new VehicleCbuSoDo();
                    update.setId(so.getId());
                    update.setVehiclePrice(dp.getUnitPrice());
                    update.setFrozenAmount(dp.getUnitPrice());
                    vehicleCbuSoGateway.updateVehicleCbuSo(update);
                    break;
                }
            }
        }
    }

    // ==================== 下发生产 ====================

    /**
     * 下发生产（单条 / 批量 / 全量）：逐条独立子事务，失败项汇总，成功项不回滚。
     *
     * @param req 下发请求
     * @return 操作结果（成功数量 + 失败明细）
     */
    @DistributeLock(nameSpace = "export-vehicle", prefix = "so:issue",
            value = "#req.id != null ? #req.id : (#req.allFlag ? 'all' : 'batch')",
            tryLockTimeout = 3000, expireAfter = 60000,
            failedCode = 600, failedMsg = "下发生产处理中，请稍后重试")
    public VehicleCbuSoOperateResult issueToProduction(VehicleCbuSoIssueReq req) {
        List<VehicleCbuSoDo> soList = resolveIssueTargets(req);
        VehicleCbuSoOperateResult result = new VehicleCbuSoOperateResult();
        for (VehicleCbuSoDo so : soList) {
            VehicleCbuSoInterfaceSyncDo syncDo = null;
            try {
                syncDo = transactionTemplate.execute(status -> doIssueOne(so));
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                result.addFail(so.getSoNo(), e.getMessage());
            }
            if (syncDo != null) {
                asyncWriteInterfaceSync(syncDo);
            }
        }
        return result;
    }

    /**
     * 解析下发目标：全量按未下发生产状态查全表；否则按 id/ids 查询。
     */
    private List<VehicleCbuSoDo> resolveIssueTargets(VehicleCbuSoIssueReq req) {
        if (Boolean.TRUE.equals(req.getAllFlag())) {
            VehicleCbuSoQo qo = new VehicleCbuSoQo();
            qo.setOrderStatus(VehicleCbuSoStatusEnum.NOT_ISSUED.getCode());
            return vehicleCbuSoGateway.listVehicleCbuSo(qo);
        }
        List<String> ids = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getId())) {
            ids.add(req.getId());
        }
        if (CollectionUtils.isNotEmpty(req.getIds())) {
            ids.addAll(req.getIds());
        }
        if (ids.isEmpty()) {
            throw new BizException("下发生产目标为空");
        }
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setIds(ids);
        return vehicleCbuSoGateway.listVehicleCbuSo(qo);
    }

    /**
     * 单条下发（事务内执行）：
     * 先更新本地 SO 状态 -> 调 APS 下发排产同步 -> APS 失败抛异常回滚本地更新 -> 返回同步记录对象（由上层异步写入）。
     */
    private VehicleCbuSoInterfaceSyncDo doIssueOne(VehicleCbuSoDo so) {
        if (!so.canIssue()) {
            throw new BizException("SO[" + so.getSoNo() + "]状态非未下发生产，不可下发");
        }
        // 1. 先更新本地 SO 状态（事务内未提交）
        so.issueToProduction();
        vehicleCbuSoGateway.updateVehicleCbuSo(so);
        // 2. 调用 APS 下发排产同步
        ApsIssueReq apsReq = new ApsIssueReq();
        apsReq.setSoId(so.getId());
        apsReq.setSoNo(so.getSoNo());
        apsReq.setPoNo(so.getPoNo());
        ApsResult apsResult = apsGateway.issueSync(apsReq);
        // 3. APS 失败 -> 抛异常回滚本地 SO 更新
        if (apsResult == null || apsResult.getCode() != 200) {
            throw new BizException("SO[" + so.getSoNo() + "]APS下发同步失败: "
                    + (apsResult == null ? "null" : apsResult.getMessage()));
        }
        // 4. 返回同步记录对象（上层在子事务提交后异步写入，避免写同步记录失败影响主业务）
        return buildSyncDo(so, 1, "下发排产同步APS", apsReq.getSoNo(), apsResult, null);
    }

    // ==================== 作废 ====================

    /**
     * 作废（单条 / 批量 / 按 PO）：
     * - 按 `id`/`ids`：逐条独立子事务，失败项汇总；
     * - 按 `poNo`：PO 维度处理，可作废 SO 一个子事务，含 1003 时对该 PO 调一次 APS；APS 失败整组回滚。
     *
     * @param req 作废请求（reason 写入同步记录 remark）
     * @return 操作结果（成功数量 + 失败明细）
     */
    @DistributeLock(nameSpace = "export-vehicle", prefix = "so:cancel",
            value = "#req.id != null ? #req.id : (#req.poNo != null ? #req.poNo : 'batch')",
            tryLockTimeout = 3000, expireAfter = 60000,
            failedCode = 600, failedMsg = "作废处理中，请稍后重试")
    public VehicleCbuSoOperateResult cancel(VehicleCbuSoCancelReq req) {
        List<VehicleCbuSoDo> soList = resolveCancelTargets(req);
        String reason = req.getReason();
        VehicleCbuSoOperateResult result = new VehicleCbuSoOperateResult();

        if (StringUtils.isNotBlank(req.getPoNo())) {
            // PO 维度：同一 PO 下可作废 SO 一个子事务，含 1003 时调一次 APS
            processCancelByPo(req.getPoNo(), soList, reason, result);
        } else {
            // id/ids 维度：逐条独立子事务
            for (VehicleCbuSoDo so : soList) {
                if (so.isCancelled()) {
                    result.setSuccessCount(result.getSuccessCount() + 1);
                    continue;
                }
                VehicleCbuSoInterfaceSyncDo syncDo = null;
                try {
                    syncDo = transactionTemplate.execute(status -> doCancelOne(so, reason));
                    result.setSuccessCount(result.getSuccessCount() + 1);
                } catch (Exception e) {
                    result.addFail(so.getSoNo(), e.getMessage());
                }
                if (syncDo != null) {
                    asyncWriteInterfaceSync(syncDo);
                }
            }
        }
        return result;
    }

    /**
     * 解析作废目标：按 poNo 查该 PO 下全部 SO；否则按 id/ids 查询。
     */
    private List<VehicleCbuSoDo> resolveCancelTargets(VehicleCbuSoCancelReq req) {
        if (StringUtils.isNotBlank(req.getPoNo())) {
            VehicleCbuSoQo qo = new VehicleCbuSoQo();
            qo.setPoNo(req.getPoNo());
            return vehicleCbuSoGateway.listVehicleCbuSo(qo);
        }
        List<String> ids = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getId())) {
            ids.add(req.getId());
        }
        if (CollectionUtils.isNotEmpty(req.getIds())) {
            ids.addAll(req.getIds());
        }
        if (ids.isEmpty()) {
            throw new BizException("作废目标为空");
        }
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setIds(ids);
        return vehicleCbuSoGateway.listVehicleCbuSo(qo);
    }

    /**
     * PO 维度作废处理：
     * 1. 分组：已作废幂等成功、不可作废记失败、可作废归为一组；
     * 2. 若可作废组内含 1003 排产中，则对该 PO 调一次 APS 作废同步；
     * 3. 同一子事务内更新该 PO 下全部可作废 SO 状态；
     * 4. APS 失败或任意 SO 更新失败，整组回滚；
     * 5. 按 SO 异步写接口同步记录。
     */
    private void processCancelByPo(String poNo, List<VehicleCbuSoDo> soList, String reason,
                                   VehicleCbuSoOperateResult result) {
        List<VehicleCbuSoDo> toCancel = new ArrayList<>();
        for (VehicleCbuSoDo so : soList) {
            if (so.isCancelled()) {
                result.setSuccessCount(result.getSuccessCount() + 1);
            } else if (so.canCancel()) {
                toCancel.add(so);
            } else {
                result.addFail(so.getSoNo(), "状态不可作废");
            }
        }
        if (toCancel.isEmpty()) {
            return;
        }

        boolean needApsSync = toCancel.stream()
                .anyMatch(so -> VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(so.getOrderStatus()));

        try {
            List<VehicleCbuSoInterfaceSyncDo> syncDos = transactionTemplate.execute(status -> {
                ApsResult apsResult;
                String requestInfo;
                if (needApsSync) {
                    ApsCancelReq apsReq = new ApsCancelReq();
                    apsReq.setPoNo(poNo);
                    apsResult = apsGateway.cancelSync(apsReq);
                    if (apsResult == null || apsResult.getCode() != 200) {
                        throw new BizException("PO[" + poNo + "]APS作废同步失败: "
                                + (apsResult == null ? "null" : apsResult.getMessage()));
                    }
                    requestInfo = poNo;
                } else {
                    apsResult = new ApsResult();
                    apsResult.setCode(200);
                    apsResult.setMessage("未下发生产，无需同步APS");
                    requestInfo = "本地作废（未下发生产）";
                }

                List<VehicleCbuSoInterfaceSyncDo> built = new ArrayList<>();
                for (VehicleCbuSoDo so : toCancel) {
                    so.cancel();
                    vehicleCbuSoGateway.updateVehicleCbuSo(so);
                    built.add(buildSyncDo(so, 2, "作废同步APS", requestInfo, apsResult, reason));
                }
                return built;
            });

            result.setSuccessCount(result.getSuccessCount() + toCancel.size());
            if (CollectionUtils.isNotEmpty(syncDos)) {
                syncDos.forEach(this::asyncWriteInterfaceSync);
            }
        } catch (Exception e) {
            for (VehicleCbuSoDo so : toCancel) {
                result.addFail(so.getSoNo(), e.getMessage());
            }
        }
    }

    /**
     * 单条作废（事务内执行）：
     * 先更新本地 SO 状态 -> 仅排产中(SCHEDULING)调 APS 作废（未下发生产未进入 APS 直接改状态）
     * -> APS 失败抛异常回滚本地更新 -> 返回同步记录对象（由上层异步写入）。
     */
    private VehicleCbuSoInterfaceSyncDo doCancelOne(VehicleCbuSoDo so, String reason) {
        if (!so.canCancel()) {
            throw new BizException("SO[" + so.getSoNo() + "]状态不可作废");
        }
        // 仅排产中已进入 APS，需调 APS 作废；未下发生产直接改状态
        boolean needApsSync = VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(so.getOrderStatus());
        // 1. 先更新本地 SO 状态（事务内未提交）
        so.cancel();
        vehicleCbuSoGateway.updateVehicleCbuSo(so);
        // 2. 调用 APS 作废同步（仅排产中）
        ApsResult apsResult;
        String requestInfo;
        if (needApsSync) {
            ApsCancelReq apsReq = new ApsCancelReq();
            apsReq.setPoNo(so.getPoNo());
            apsReq.setSoNo(so.getSoNo());
            apsResult = apsGateway.cancelSync(apsReq);
            // APS 失败 -> 抛异常回滚本地 SO 更新
            if (apsResult == null || apsResult.getCode() != 200) {
                throw new BizException("SO[" + so.getSoNo() + "]APS作废同步失败: "
                        + (apsResult == null ? "null" : apsResult.getMessage()));
            }
            requestInfo = apsReq.getSoNo();
        } else {
            // 未下发生产，未进入 APS，无需同步
            apsResult = new ApsResult();
            apsResult.setCode(200);
            apsResult.setMessage("未下发生产，无需同步APS");
            requestInfo = "本地作废（未下发生产）";
        }
        // 3. 返回同步记录对象（上层在子事务提交后异步写入，避免写同步记录失败影响主业务）
        return buildSyncDo(so, 2, "作废同步APS", requestInfo, apsResult, reason);
    }

    // ==================== 外部回调（APS 节点 / 红旗质检 / SAP 清账） ====================

    /**
     * 接收 APS 生产状态回传：幂等/回退处理、更新 SO 状态与车辆明细、写客户系统同步记录、发布红旗生产信息同步事件。
     *
     * <p>幂等与回退策略（对齐 spec "重复状态幂等"场景）：
     * <ul>
     *   <li>目标状态 < 当前状态（回退，如总装上线回退涂装上线）：跳过全部处理；</li>
     *   <li>目标状态 == 当前状态（重复，时间相同或更晚）：跳过全部处理，不更新、不写同步记录、不发事件；</li>
     *   <li>目标状态 > 当前状态（向前流转）：更新状态、节点时间、车辆信息，写同步记录，按需发事件。</li>
     * </ul>
     *
     * @param req APS 状态回传请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void receiveProductionStatus(VehicleCbuSoReceiveStatusReq req) {
        if (req == null || StringUtils.isBlank(req.getSoNo()) || req.getStatus() == null) {
            throw new BizException("SO单号与状态码不能为空");
        }
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoBySoNo(req.getSoNo());
        if (so == null) {
            throw new BizException("SO[" + req.getSoNo() + "]不存在");
        }

        VehicleCbuSoStatusEnum targetStatusEnum = VehicleCbuSoStatusEnum.fromApsStatus(req.getStatus());
        if (targetStatusEnum == null) {
            throw new BizException("不支持的状态码：" + req.getStatus());
        }
        Integer currentStatus = so.getOrderStatus();
        Integer targetStatus = targetStatusEnum.getCode();

        // 幂等/回退：目标状态 <= 当前状态，跳过全部处理（不更新、不写同步记录、不发事件）
        if (currentStatus != null && targetStatus.compareTo(currentStatus) <= 0) {
            if (targetStatus.equals(currentStatus)) {
                log.info("SO[{}] 同状态{}重复回传，跳过全部处理", req.getSoNo(), currentStatus);
            } else {
                log.info("SO[{}] 当前状态{} >= 目标状态{}，跳过回退处理",
                        req.getSoNo(), currentStatus, targetStatus);
            }
            return;
        }

        // 向前流转：updateProductionStatus 仅更新状态，节点时间由 overrideNodeTimes 按 APS 请求写入
        if (req.getPlanOfflineDate() != null) {
            so.setExpectedOfflineDate(req.getPlanOfflineDate());
        }
        so.updateProductionStatus(req.getStatus());
        overrideNodeTimes(so, req);
        if (StringUtils.isNotBlank(req.getInternalVehicleNo())) {
            so.setInternalVehicleNo(req.getInternalVehicleNo());
        }
        if (StringUtils.isNotBlank(req.getProductionIdentifyCode())) {
            so.setProductionIdentifyCode(req.getProductionIdentifyCode());
        }
        if (StringUtils.isNotBlank(req.getVin())) {
            so.setVin(req.getVin());
        }
        vehicleCbuSoGateway.updateVehicleCbuSo(so);

        // 更新车辆明细
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(so.getId());
        if (detail != null) {
            updateDetailNodeInfo(detail, req);
            vehicleCbuSoDetailGateway.updateVehicleCbuSoDetail(detail);
        }

        // 写客户系统同步记录（类型 3）
        VehicleCbuSoInterfaceSyncDo syncDo = buildInterfaceSyncDo(so, 3, "车辆生产信息同步红旗",
                req.getSoNo(), 1, "状态" + req.getStatus());
        vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(syncDo);

        // 发布红旗生产信息同步事件（仅 1003/1006/1007 触发）
        if (isHongqiSyncNode(targetStatus)) {
            HongqiProductionNodeNotifyReq notifyReq = buildHongqiProductionNodeNotifyReq(so, detail, targetStatus);
            eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.HONGQI,
                    HongqiEventTypeEnum.PRODUCTION_NODE_SYNC, so.getSoNo(), List.of(notifyReq));
        }
    }

    /**
     * 接收红旗质检结果回传：按 VIN 找 SO，校验状态，更新质检信息，通过时触发 SAP 同步。
     *
     * @param req 红旗质检结果请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void receiveQcResult(VehicleCbuSoReceiveQcReq req) {
        if (req == null || StringUtils.isBlank(req.getVin()) || req.getResult() == null) {
            throw new BizException("VIN与质检结果不能为空");
        }
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailByVin(req.getVin());
        if (detail == null) {
            throw new BizException("VIN[" + req.getVin() + "]未找到车辆明细");
        }
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoById(detail.getId());
        if (so == null) {
            throw new BizException("VIN[" + req.getVin() + "]关联SO不存在");
        }

        // 校验订单状态至少为总装下线
        if (!isAtLeastAssemblyOut(so.getOrderStatus())) {
            throw new BizException("SO[" + so.getSoNo() + "]未总装下线，不能接收质检结果");
        }

        VehicleCbuSoQcResultEnum qcEnum = VehicleCbuSoQcResultEnum.of(req.getResult());
        if (qcEnum == null) {
            throw new BizException("不支持的质检结果：" + req.getResult());
        }

        // 通过状态不允许回退
        if (VehicleCbuSoQcResultEnum.PASSED.getCode().equals(so.getQcResult())
                && !VehicleCbuSoQcResultEnum.PASSED.equals(qcEnum)) {
            throw new BizException("SO[" + so.getSoNo() + "]当前已为质检通过，不允许回退");
        }

        // 更新 SO 质检信息（含当前→上次覆盖逻辑）
        so.updateQcResult(qcEnum.getCode(), req.getRemark());
        Integer orderStatus = qcResultToOrderStatus(qcEnum);
        so.setOrderStatus(orderStatus);
        if (req.getQcTime() != null) {
            so.setQcResultTime(req.getQcTime());
        }
        vehicleCbuSoGateway.updateVehicleCbuSo(so);

        // 写红旗反馈同步记录（类型 3）
        VehicleCbuSoInterfaceSyncDo hongqiSync = buildInterfaceSyncDo(so, 3, "红旗质检结果反馈",
                req.getVin(), 1, String.valueOf(req.getResult()));
        vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(hongqiSync);

        // 质检通过时同步 SAP
        if (VehicleCbuSoQcResultEnum.PASSED.equals(qcEnum)) {
            SapOrderSyncReq sapReq = buildSapOrderSyncReq(so, detail);
            VehicleCbuSoInterfaceSyncDo sapSync = buildInterfaceSyncDo(so, 4, "订单同步SAP",
                    so.getSoNo(), 1, null);
            vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(sapSync);
            eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.SAP,
                    SapEventTypeEnum.SYNC_SAP, so.getSoNo(), sapReq);
        }
    }

    /**
     * 供应付清单模块调用：按 SO 单号更新状态为已收款。
     *
     * @param soNo SO 单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateReceivedStatus(String soNo) {
        if (StringUtils.isBlank(soNo)) {
            throw new BizException("SO单号不能为空");
        }
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoBySoNo(soNo);
        if (so == null) {
            throw new BizException("SO[" + soNo + "]不存在");
        }
        if (VehicleCbuSoStatusEnum.RECEIVED.getCode().equals(so.getOrderStatus())) {
            return;
        }
        so.updateReceivedStatus();
        vehicleCbuSoGateway.updateVehicleCbuSo(so);
    }

    /**
     * SAP 手动补推：仅质检通过态允许补推，复用 SAP 同步事件并写接口同步记录。
     *
     * @param soNo SO 单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void reissueSap(String soNo) {
        if (StringUtils.isBlank(soNo)) {
            throw new BizException("SO单号不能为空");
        }
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoBySoNo(soNo);
        if (so == null) {
            throw new BizException("SO[" + soNo + "]不存在");
        }
        if (!VehicleCbuSoStatusEnum.QC_PASSED.getCode().equals(so.getOrderStatus())) {
            throw new BizException("仅质检通过态允许补推SAP");
        }
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(so.getId());
        SapOrderSyncReq sapReq = buildSapOrderSyncReq(so, detail);
        VehicleCbuSoInterfaceSyncDo sapSync = buildInterfaceSyncDo(so, 4, "订单同步SAP(手动补推)",
                so.getSoNo(), 1, null);
        vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(sapSync);
        eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.SAP,
                SapEventTypeEnum.SYNC_SAP, so.getSoNo(), sapReq);
    }

    // ==================== 回调辅助方法 ====================

    private boolean isAtLeastAssemblyOut(Integer orderStatus) {
        return orderStatus != null
                && orderStatus >= VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode();
    }

    private boolean isHongqiSyncNode(Integer orderStatus) {
        return VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(orderStatus)
                || VehicleCbuSoStatusEnum.ASSEMBLY_IN.getCode().equals(orderStatus)
                || VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode().equals(orderStatus);
    }

    private Integer qcResultToOrderStatus(VehicleCbuSoQcResultEnum qcEnum) {
        return switch (qcEnum) {
            case PASSED -> VehicleCbuSoStatusEnum.QC_PASSED.getCode();
            case FAILED -> VehicleCbuSoStatusEnum.QC_FAILED.getCode();
            case NO_VEHICLE -> VehicleCbuSoStatusEnum.QC_NO_VEHICLE.getCode();
        };
    }

    /**
     * 向前流转：无条件覆盖请求传入的节点时间（请求未传则保留原值）。
     */
    private void overrideNodeTimes(VehicleCbuSoDo so, VehicleCbuSoReceiveStatusReq req) {
        if (req.getScheduleTime() != null) {
            so.setScheduleTime(req.getScheduleTime());
        }
        if (req.getBarcodePrintTime() != null) {
            so.setBarcodePrintTime(req.getBarcodePrintTime());
        }
        if (req.getBodyInTime() != null) {
            so.setBodyInTime(req.getBodyInTime());
        }
        if (req.getPaintInTime() != null) {
            so.setPaintInTime(req.getPaintInTime());
        }
        if (req.getAssemblyInTime() != null) {
            so.setAssemblyInTime(req.getAssemblyInTime());
        }
        if (req.getAssemblyOutTime() != null) {
            so.setAssemblyOutTime(req.getAssemblyOutTime());
        }
    }

    private void updateDetailNodeInfo(VehicleCbuSoDetailDo detail, VehicleCbuSoReceiveStatusReq req) {
        if (StringUtils.isNotBlank(req.getInternalVehicleNo())) {
            detail.setInternalVehicleNo(req.getInternalVehicleNo());
        }
        if (StringUtils.isNotBlank(req.getProductionIdentifyCode())) {
            detail.setProductionIdentifyCode(req.getProductionIdentifyCode());
        }
        if (StringUtils.isNotBlank(req.getVin())) {
            detail.setVin(req.getVin());
        }
        if (req.getScheduleTime() != null) {
            detail.setScheduleTime(req.getScheduleTime());
        }
        if (req.getBarcodePrintTime() != null) {
            detail.setBarcodePrintTime(req.getBarcodePrintTime());
        }
        if (req.getBodyInTime() != null) {
            detail.setBodyInTime(req.getBodyInTime());
        }
        if (req.getPaintInTime() != null) {
            detail.setPaintInTime(req.getPaintInTime());
        }
        if (req.getAssemblyInTime() != null) {
            detail.setAssemblyInTime(req.getAssemblyInTime());
        }
        if (req.getAssemblyOutTime() != null) {
            detail.setAssemblyOutTime(req.getAssemblyOutTime());
        }
    }

    private HongqiProductionNodeNotifyReq buildHongqiProductionNodeNotifyReq(VehicleCbuSoDo so,
                                                                             VehicleCbuSoDetailDo detail,
                                                                             Integer orderStatus) {
        HongqiProductionNodeNotifyReq req = new HongqiProductionNodeNotifyReq();
        req.setPoNo(so.getPoNo());
        req.setSoNo(so.getSoNo());
        req.setLpMtlNo(detail != null ? detail.getMaterialCode() : so.getMaterialCode());

        if (detail != null) {
            req.setVin(detail.getVin());
            if (so.getExpectedOfflineDate() != null) {
                req.setPlanOfflineDate(so.getExpectedOfflineDate().format(HONGQI_DATE_FORMAT));
            }
            if (detail.getAssemblyInTime() != null) {
                req.setActOnlineTime(detail.getAssemblyInTime().format(HONGQI_DATETIME_FORMAT));
            }
            if (detail.getAssemblyOutTime() != null) {
                req.setActOfflineTime(detail.getAssemblyOutTime().format(HONGQI_DATETIME_FORMAT));
            }
        }

        // 1003 排产中：仅计划下线日期
        if (VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(orderStatus)) {
            req.setActOnlineTime(null);
            req.setActOfflineTime(null);
        }
        // 1006 总装上线：保留 actOnlineTime，清 actOfflineTime
        if (VehicleCbuSoStatusEnum.ASSEMBLY_IN.getCode().equals(orderStatus)) {
            req.setActOfflineTime(null);
        }
        return req;
    }

    private SapOrderSyncReq buildSapOrderSyncReq(VehicleCbuSoDo so, VehicleCbuSoDetailDo detail) {
        SapOrderSyncReq req = new SapOrderSyncReq();
        req.setCustomerPoNo(so.getPoNo());
        req.setSalesServiceSoNo(so.getSoNo());
        req.setOrderDate(so.getCreateTime() != null ? so.getCreateTime().toLocalDate() : null);
        req.setLogisticsStatus("");
        req.setOrderStatusDate(LocalDateTime.now());
        req.setSalesCompanyCode(so.getSalesCompanyCode());
        req.setCustomerCode(so.getCustomerCode());
        req.setOrderType(String.valueOf(so.getOrderType()));
        req.setOrderCategory("");
        req.setSplitOrderNo("");
        req.setVin(detail != null ? detail.getVin() : so.getVin());
        req.setMaterialCode(detail != null ? detail.getMaterialCode() : so.getMaterialCode());
        req.setOrderQuantity(BigDecimal.ONE);
        req.setExwUnitPrice(so.getVehiclePrice());
        return req;
    }

    private VehicleCbuSoInterfaceSyncDo buildInterfaceSyncDo(VehicleCbuSoDo so, Integer interfaceType,
                                                             String interfaceDesc, String requestInfo,
                                                             Integer resultStatus, String remark) {
        VehicleCbuSoInterfaceSyncDo syncDo = new VehicleCbuSoInterfaceSyncDo();
        syncDo.setSoId(so.getId());
        syncDo.setInterfaceType(interfaceType);
        syncDo.setInterfaceDesc(interfaceDesc);
        syncDo.setCallTime(LocalDateTime.now());
        syncDo.setRequestInfo(requestInfo);
        syncDo.setResultStatus(resultStatus);
        syncDo.setResponseInfo(null);
        syncDo.setRemark(remark);
        syncDo.setStatus(1);
        return syncDo;
    }

    /**
     * 异步写接口同步记录（失败只记日志，不影响主业务）。
     */
    private void asyncWriteInterfaceSync(VehicleCbuSoInterfaceSyncDo syncDo) {
        if (syncDo == null) {
            return;
        }
        // 提前捕获当前操作人，避免异步线程丢失 LoginUser
        LoginUser user = LoginUserUtils.getCurrentUserOrSysDefault();
        if (user != null) {
            syncDo.setCreator(user.getUserId());
            syncDo.setCreatorName(user.getUserName());
            syncDo.setModifier(user.getUserId());
            syncDo.setModifierName(user.getUserName());
        }
        taskExecutor.execute(() -> {
            try {
                vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(syncDo);
            } catch (Exception e) {
                log.error("异步写接口同步记录失败, soId={}, interfaceType={}, interfaceDesc={}",
                        syncDo.getSoId(), syncDo.getInterfaceType(), syncDo.getInterfaceDesc(), e);
            }
        });
    }

    /**
     * 构建接口同步记录（调用 addVehicleCbuSoInterfaceSync 会自动刷新明细表同步快照）。
     */
    private VehicleCbuSoInterfaceSyncDo buildSyncDo(VehicleCbuSoDo so, Integer interfaceType,
                                                    String interfaceDesc, String requestInfo, ApsResult apsResult,
                                                    String reason) {
        VehicleCbuSoInterfaceSyncDo syncDo = new VehicleCbuSoInterfaceSyncDo();
        syncDo.setSoId(so.getId());
        syncDo.setInterfaceType(interfaceType);
        syncDo.setInterfaceDesc(interfaceDesc);
        syncDo.setCallTime(LocalDateTime.now());
        syncDo.setRequestInfo(requestInfo);
        syncDo.setResultStatus(apsResult != null && apsResult.getCode() == 200 ? 1 : 0);
        syncDo.setResponseInfo(apsResult == null ? null : apsResult.getMessage());
        syncDo.setRemark(reason);
        syncDo.setStatus(1);
        return syncDo;
    }
}
