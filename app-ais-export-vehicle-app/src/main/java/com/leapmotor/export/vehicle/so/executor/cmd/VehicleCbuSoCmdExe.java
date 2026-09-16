package com.leapmotor.export.vehicle.so.executor.cmd;

import com.leapmotor.export.nexus.company.api.CompanyApi;
import com.leapmotor.export.nexus.company.request.CompanyRequest;
import com.leapmotor.export.nexus.company.response.CompanyResponse;
import com.leapmotor.export.vehicle.acl.rpc.aps.gateway.ApsGateway;
import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsCancelReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsIssueReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.resp.ApsResult;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.SapOrderSyncReq;
import com.leapmotor.export.vehicle.base.config.ApsReceiveProperties;
import com.leapmotor.export.vehicle.base.config.SoIssueProperties;
import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeTransferReq;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeTransferItem;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.so.enums.InterfaceSyncTypeEnum;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoCancelReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoIssueReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReissueSapReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.user.LoginUser;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import com.leapmotor.platform.ais.common.redis.BlockLock;
import com.leapmotor.platform.ais.common.redis.DistributedLockTemplate;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import com.leapmotor.platform.ais.common.web.thread.AisRunnable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

/**
 *
 * VehicleCbuSo 写操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@Slf4j
@RequiredArgsConstructor
@RefreshScope
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

    /**
     * APS 接口日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter APS_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VehicleCbuSoGateway vehicleCbuSoGateway;
    private final VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;
    private final ApsGateway apsGateway;
    private final VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;
    private final EventHandlerHelper eventHandlerHelper;
    @Qualifier("taskExecutor")
    private final TaskExecutor taskExecutor;
    private final TransactionTemplate transactionTemplate;
    private final ApsReceiveProperties apsReceiveProperties;
    private final SoIssueProperties soIssueProperties;
    private final CompanyApi companyApi;
    private final DistributedLockTemplate distributedLockTemplate;
    @Qualifier("soIssueExecutor")
    private final TaskExecutor soIssueExecutor;
    private final ExportVehicleLetterGuaranteeService letterGuaranteeService;
    private final ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;

    /**
     * SAP 同步开关，默认关闭（上线后经 Nacos 配置 sap.sync.enabled=true 打开）。
     * 质检同步 SAP 接口晚于接收质检接口上线，上线前关闭待上线后打开。
     */
    @Value("${sap.sync.enabled:false}")
    private boolean sapSyncEnabled;

    public String addOrUpdateVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo) {
        return vehicleCbuSoDo.getId() == null
                ? vehicleCbuSoGateway.addVehicleCbuSo(vehicleCbuSoDo)
                : vehicleCbuSoGateway.updateVehicleCbuSo(vehicleCbuSoDo);
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
        // 批次统一时间戳前缀，避免跨分钟导致同批次 SO 单号前缀不一致
        String soNoPrefix = "SO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        for (SoCreateReq.SoDetailReq detailReq : req.getDetails()) {
            if (detailReq == null) {
                throw new BizException("PO明细存在空行，不允许创建SO");
            }
            if (detailReq.getDemandQuantity() == null || detailReq.getDemandQuantity().intValue() <= 0) {
                throw new BizException("PO明细行" + detailReq.getPoDetailNo()
                        + "需求数量为空或不大于0，不允许创建SO");
            }
            int quantity = detailReq.getDemandQuantity().intValue();
            for (int i = 0; i < quantity; i++) {
                pairs.add(Pair.of(buildSo(req, detailReq, lineNo, soNoPrefix), buildSoDetail(detailReq)));
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
     * 生成 SO 单号：prefix + 5位流水（同 po_detail_no）
     */
    private String genSoNo(String prefix) {
        return prefix + NoGenerator.genNoOnly("SO", "yyyyMMddHHmm", 5);
    }

    /**
     * 构建 SO
     */
    private VehicleCbuSoDo buildSo(SoCreateReq req, SoCreateReq.SoDetailReq detailReq, int lineNo, String soNoPrefix) {
        VehicleCbuSoDo so = new VehicleCbuSoDo();
        so.setSoNo(genSoNo(soNoPrefix));
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
        so.setReceiveOrderTime(req.getReceiveOrderTime());
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
        soDetail.setSalesMarket(detailReq.getSalesMarket());
        soDetail.setModelYear(detailReq.getModelYear());
        soDetail.setOptionalPackage(detailReq.getOptionalPackage());
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
    public VehicleCbuSoOperateResult issueToProduction(VehicleCbuSoIssueReq req) {
        List<VehicleCbuSoDo> soList = resolveIssueTargets(req);
        VehicleCbuSoOperateResult result = new VehicleCbuSoOperateResult();
        if (soList.isEmpty()) {
            return result;
        }
        int batchSize = soIssueProperties.getBatchSize();
        // 分批并行下发：避免 soIssueExecutor queue 溢出触发 AbortPolicy + detailMap IN 过大
        for (int i = 0; i < soList.size(); i += batchSize) {
            List<VehicleCbuSoDo> batch = soList.subList(i, Math.min(i + batchSize, soList.size()));
            // 预查本批：销售公司名称（按编码去重查主数据）、车辆明细（按 soId 批量）
            Map<String, String> companyNameMap = batchQuerySalesCompanyNames(batch);
            Map<String, VehicleCbuSoDetailDo> detailMap = batchQueryDetails(batch);
            // 内层并行：每条 SO 独立锁 + 独立子事务，AisRunnable 透传 LoginUser/MDC
            List<CompletableFuture<IssueItemResult>> futures = new ArrayList<>(batch.size());
            for (VehicleCbuSoDo so : batch) {
                String salesCompanyName = companyNameMap.get(so.getSalesCompanyCode());
                VehicleCbuSoDetailDo detail = detailMap.get(so.getId());
                CompletableFuture<IssueItemResult> future = new CompletableFuture<>();
                try {
                    soIssueExecutor.execute(new AisRunnable(() -> {
                        try {
                            future.complete(issueOneWithLock(so, salesCompanyName, detail));
                        } catch (Exception e) {
                            String reason = e instanceof BizException ? e.getMessage()
                                    : e.getClass().getSimpleName()
                                    + (e.getMessage() != null ? ": " + e.getMessage() : "");
                            future.complete(IssueItemResult.fail(so.getSoNo(), reason));
                        }
                    }));
                } catch (RejectedExecutionException e) {
                    result.addFail(so.getSoNo(), "线程池满，提交失败");
                }
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            for (CompletableFuture<IssueItemResult> f : futures) {
                IssueItemResult r = f.join();
                switch (r.getStatus()) {
                    case SUCCESS -> result.setSuccessCount(result.getSuccessCount() + 1);
                    case SKIP -> result.addSkip(r.getSoNo(), r.getReason());
                    case FAIL -> result.addFail(r.getSoNo(), r.getReason());
                    default -> {
                    }
                }
            }
        }
        result.setTotalCount(soList.size());
        result.setFailCount(result.getFailItems().size());
        result.setSkipCount(result.getSkipItems().size());
        return result;
    }

    /**
     * 单条 SO 下发：SO 维度锁 -> 锁内重读 + 二次校验 -> 事务内 doIssueOne -> 释放锁（晚于事务提交）。
     */
    private IssueItemResult issueOneWithLock(VehicleCbuSoDo so, String salesCompanyName,
                                             VehicleCbuSoDetailDo detail) {
        BlockLock lock;
        try {
            lock = distributedLockTemplate.blockLock("so:issue:" + so.getId(), 30000, 60000);
        } catch (Exception e) {
            return IssueItemResult.fail(so.getSoNo(), "获取锁失败:" + e.getMessage());
        }
        try {
            VehicleCbuSoDo latest = vehicleCbuSoGateway.getVehicleCbuSoById(so.getId());
            if (latest == null) {
                return IssueItemResult.fail(so.getSoNo(), "SO 不存在");
            }
            if (!latest.canIssue()) {
                return IssueItemResult.skip(so.getSoNo(), "状态非未下发生产，跳过");
            }
            VehicleCbuSoInterfaceSyncDo syncDo = transactionTemplate.execute(
                    status -> doIssueOne(latest, salesCompanyName, detail));
            asyncWriteInterfaceSync(syncDo);
            return IssueItemResult.success(so.getSoNo());
        } catch (Exception e) {
            String reason = e instanceof BizException ? e.getMessage()
                    : e.getClass().getSimpleName()
                    + (e.getMessage() != null ? ": " + e.getMessage() : "");
            return IssueItemResult.fail(so.getSoNo(), reason);
        } finally {
            closeLock(lock, "so:issue:" + so.getId());
        }
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
    private VehicleCbuSoInterfaceSyncDo doIssueOne(VehicleCbuSoDo so, String salesCompanyName,
                                                   VehicleCbuSoDetailDo detail) {
        if (!so.canIssue()) {
            throw new BizException("SO[" + so.getSoNo() + "]状态非未下发生产，不可下发");
        }
        // 1. 先更新本地 SO 状态（事务内未提交）
        so.issueToProduction();
        vehicleCbuSoGateway.updateVehicleCbuSo(so);
        // 2. 调用 APS 下发排产同步
        ApsIssueReq apsReq = buildApsIssueReq(so, salesCompanyName, detail);
        ApsResult apsResult;
        try {
            apsResult = apsGateway.issueSync(apsReq);
        } catch (Exception e) {
            log.error("SO下发APS异常 soNo={}", so.getSoNo(), e);
            asyncWriteInterfaceSync(buildSyncDo(so, InterfaceSyncTypeEnum.APS_ISSUE_SYNC, so.getSoNo(),
                    buildFailApsResult("异常:" + e.getMessage()), null));
            throw e;
        }
        // 3. APS 失败 -> 抛异常回滚本地 SO 更新
        if (apsResult == null || apsResult.getCode() != 200) {
            log.error("SO下发APS失败 soNo={} code={} msg={}", so.getSoNo(),
                    apsResult == null ? null : apsResult.getCode(),
                    apsResult == null ? null : apsResult.getMessage());
            ApsResult recordResult = apsResult != null ? apsResult : buildFailApsResult("null");
            asyncWriteInterfaceSync(buildSyncDo(so, InterfaceSyncTypeEnum.APS_ISSUE_SYNC, so.getSoNo(), recordResult,
                    null));
            throw new BizException("SO[" + so.getSoNo() + "]APS下发同步失败: "
                    + (apsResult == null ? "null" : apsResult.getMessage()));
        }
        // 4. 返回同步记录对象（上层在子事务提交后异步写入，避免写同步记录失败影响主业务）
        return buildSyncDo(so, InterfaceSyncTypeEnum.APS_ISSUE_SYNC, so.getSoNo(), apsResult, null);
    }

    /**
     * 构造失败的 ApsResult，用于 APS 异常/返回 null 场景记录接口同步记录（resultStatus=0，responseInfo 记原因）。
     */
    private ApsResult buildFailApsResult(String message) {
        ApsResult result = new ApsResult();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 构造 APS 下发请求，字段对齐《海外销售订单接口文档》POST /api/abroad/batchOrder。
     */
    private ApsIssueReq buildApsIssueReq(VehicleCbuSoDo so, String salesCompanyName,
                                         VehicleCbuSoDetailDo detail) {
        ApsIssueReq req = new ApsIssueReq();
        req.setVehPoId(so.getPoId());
        req.setVehPoName(so.getPoNo());
        req.setVehCbuSoId(so.getId());
        req.setVehCbuSoName(so.getSoNo());
        if (so.getReceiveOrderTime() != null) {
            req.setIssueOn(so.getReceiveOrderTime().format(APS_DATETIME_FORMAT));
        }
        req.setSalesCompanyCode(so.getSalesCompanyCode());
        req.setSalesCompanyName(salesCompanyName);
        req.setCustomerCode(so.getCustomerCode());
        req.setCustomerName(so.getCustomerName());
        req.setOrderType(10);
        if (so.getExpectedOfflineDate() != null) {
            req.setExpectProducedOn(so.getExpectedOfflineDate().atStartOfDay().format(APS_DATETIME_FORMAT));
        }
        req.setVinYear(so.getVinYear());
        req.setLineNo(so.getLineNo());
        req.setOrderCategory(100);
        req.setVehOrderSource(200);
        req.setQuantity(1);
        // 物料取自车辆明细（明细 id = 主表 id，由 issueToProduction 预查传入）
        if (detail != null) {
            req.setVehicleMaterialName(detail.getMaterialCode());
            req.setVehicleMaterialDesc(detail.getMaterialDescription());
        }
        return req;
    }

    /**
     * 按销售公司编码查主数据获取销售公司名称。
     */
    private String querySalesCompanyName(String companyCode) {
        if (StringUtils.isBlank(companyCode)) {
            return null;
        }
        try {
            CompanyRequest request = new CompanyRequest();
            request.setCompanyCode(companyCode);
            AjaxResult<List<CompanyResponse>> ret = companyApi.list(request);
            List<CompanyResponse> list = ret != null ? ret.getData() : null;
            return (list != null && !list.isEmpty()) ? list.get(0).getCompanyName() : null;
        } catch (Exception e) {
            log.warn("销售公司主数据查询失败 companyCode={}", companyCode, e);
            return null;
        }
    }

    /**
     * 批量按销售公司编码去重查主数据，返回 code -> name。
     */
    private Map<String, String> batchQuerySalesCompanyNames(List<VehicleCbuSoDo> soList) {
        Set<String> codes = soList.stream()
                .map(VehicleCbuSoDo::getSalesCompanyCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        Map<String, String> map = new HashMap<>();
        for (String code : codes) {
            map.put(code, querySalesCompanyName(code));
        }
        return map;
    }

    /**
     * 按 soId 批量查车辆明细，返回 soId -> detail。
     */
    private Map<String, VehicleCbuSoDetailDo> batchQueryDetails(List<VehicleCbuSoDo> soList) {
        List<String> soIds = soList.stream().map(VehicleCbuSoDo::getId).collect(Collectors.toList());
        if (soIds.isEmpty()) {
            return Collections.emptyMap();
        }
        VehicleCbuSoDetailQo qo = new VehicleCbuSoDetailQo();
        qo.setIds(soIds);
        List<VehicleCbuSoDetailDo> details = vehicleCbuSoDetailGateway.listVehicleCbuSoDetail(qo);
        if (details == null || details.isEmpty()) {
            return Collections.emptyMap();
        }
        return details.stream().collect(Collectors.toMap(VehicleCbuSoDetailDo::getId, d -> d, (a, b) -> a));
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
    public VehicleCbuSoOperateResult cancel(VehicleCbuSoCancelReq req) {
        List<VehicleCbuSoDo> soList = resolveCancelTargets(req);
        String reason = req.getReason();
        VehicleCbuSoOperateResult result = new VehicleCbuSoOperateResult();

        // 按 PO 维度加锁（不同 PO 不互斥，同 PO 串行）
        List<BlockLock> locks = acquireCancelLocks(req, soList);
        try {
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
        } finally {
            locks.forEach(l -> closeLock(l, "so:cancel:po"));
        }
        result.setTotalCount(soList.size());
        result.setFailCount(result.getFailItems().size());
        return result;
    }

    /**
     * 按 PO 维度获取作废锁：poNo 路径锁单个 PO；id/ids 路径按 soList 的 poNo 去重批量加锁。
     */
    private List<BlockLock> acquireCancelLocks(VehicleCbuSoCancelReq req, List<VehicleCbuSoDo> soList) {
        Set<String> poNos = new LinkedHashSet<>();
        if (StringUtils.isNotBlank(req.getPoNo())) {
            poNos.add(req.getPoNo());
        } else {
            soList.stream()
                    .map(VehicleCbuSoDo::getPoNo)
                    .filter(StringUtils::isNotBlank)
                    .forEach(poNos::add);
        }
        List<BlockLock> locks = new ArrayList<>();
        for (String poNo : poNos) {
            try {
                locks.add(distributedLockTemplate.blockLock("so:cancel:po:" + poNo, 30000, 60000));
            } catch (Exception e) {
                locks.forEach(l -> closeLock(l, "so:cancel:po"));
                throw new BizException("作废获取锁失败，请稍后重试");
            }
        }
        return locks;
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
                .anyMatch(so -> VehicleCbuSoStatusEnum.ISSUED.getCode().equals(so.getOrderStatus())
                        || VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(so.getOrderStatus()));

        try {
            List<VehicleCbuSoInterfaceSyncDo> syncDos = transactionTemplate.execute(status -> {
                ApsResult apsResult;
                String requestInfo;
                if (needApsSync) {
                    ApsCancelReq apsReq = new ApsCancelReq();
                    apsReq.setVehPoName(poNo);
                    try {
                        apsResult = apsGateway.cancelSync(apsReq);
                    } catch (Exception e) {
                        log.error("PO作废APS异常 poNo={}", poNo, e);
                        ApsResult failResult = buildFailApsResult("异常:" + e.getMessage());
                        for (VehicleCbuSoDo so : toCancel) {
                            asyncWriteInterfaceSync(
                                    buildSyncDo(so, InterfaceSyncTypeEnum.APS_CANCEL_SYNC, poNo, failResult, reason));
                        }
                        throw e;
                    }
                    if (apsResult == null || apsResult.getCode() != 200) {
                        log.error("PO作废APS失败 poNo={} code={} msg={}", poNo,
                                apsResult == null ? null : apsResult.getCode(),
                                apsResult == null ? null : apsResult.getMessage());
                        ApsResult recordResult = apsResult != null ? apsResult : buildFailApsResult("null");
                        for (VehicleCbuSoDo so : toCancel) {
                            asyncWriteInterfaceSync(
                                    buildSyncDo(so, InterfaceSyncTypeEnum.APS_CANCEL_SYNC, poNo, recordResult, reason));
                        }
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
                    built.add(buildSyncDo(so, InterfaceSyncTypeEnum.APS_CANCEL_SYNC, requestInfo, apsResult, reason));
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
     * <p>
     * 先更新本地 SO 状态 -> 仅排产中(
             SCHEDULING)调 APS 作废（未下发生产未进入 APS 直接改状态）
     * -> APS 失败抛异常回滚本地更新 -> 返回同步记录对象（由上层异步写入）。
     */
    private VehicleCbuSoInterfaceSyncDo doCancelOne(VehicleCbuSoDo so, String reason) {
        if (!so.canCancel()) {
            throw new BizException("SO[" + so.getSoNo() + "]状态不可作废");
        }
        // 仅排产中已进入 APS，需调 APS 作废；未下发生产直接改状态
        boolean needApsSync = VehicleCbuSoStatusEnum.ISSUED.getCode().equals(so.getOrderStatus())
                || VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(so.getOrderStatus());
        // 1. 先更新本地 SO 状态（事务内未提交）
        so.cancel();
        vehicleCbuSoGateway.updateVehicleCbuSo(so);
        // 2. 调用 APS 作废同步（仅排产中）
        ApsResult apsResult;
        String requestInfo;
        if (needApsSync) {
            ApsCancelReq apsReq = new ApsCancelReq();
            apsReq.setVehPoName(so.getPoNo());
            apsReq.setVehCbuSoName(so.getSoNo());
            try {
                apsResult = apsGateway.cancelSync(apsReq);
            } catch (Exception e) {
                log.error("SO作废APS异常 soNo={}", so.getSoNo(), e);
                asyncWriteInterfaceSync(buildSyncDo(so, InterfaceSyncTypeEnum.APS_CANCEL_SYNC, so.getSoNo(),
                        buildFailApsResult("异常:" + e.getMessage()), reason));
                throw e;
            }
            // APS 失败 -> 抛异常回滚本地 SO 更新
            if (apsResult == null || apsResult.getCode() != 200) {
                log.error("SO作废APS失败 soNo={} code={} msg={}", so.getSoNo(),
                        apsResult == null ? null : apsResult.getCode(),
                        apsResult == null ? null : apsResult.getMessage());
                ApsResult recordResult = apsResult != null ? apsResult : buildFailApsResult("null");
                asyncWriteInterfaceSync(
                        buildSyncDo(so, InterfaceSyncTypeEnum.APS_CANCEL_SYNC, so.getSoNo(), recordResult, reason));
                throw new BizException("SO[" + so.getSoNo() + "]APS作废同步失败: "
                        + (apsResult == null ? "null" : apsResult.getMessage()));
            }
            requestInfo = apsReq.getVehCbuSoName();
        } else {
            // 未下发生产，未进入 APS，无需同步
            apsResult = new ApsResult();
            apsResult.setCode(200);
            apsResult.setMessage("未下发生产，无需同步APS");
            requestInfo = "本地作废（未下发生产）";
        }
        // 3. 返回同步记录对象（上层在子事务提交后异步写入，避免写同步记录失败影响主业务）
        return buildSyncDo(so, InterfaceSyncTypeEnum.APS_CANCEL_SYNC, requestInfo, apsResult, reason);
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
     * @param reqs APS 状态回传请求
     */
    public VehicleCbuSoOperateResult receiveProductionStatusBatch(List<VehicleCbuSoReceiveStatusReq> reqs) {
        VehicleCbuSoOperateResult result = new VehicleCbuSoOperateResult();
        if (CollectionUtils.isEmpty(reqs)) {
            return result;
        }
        int limit = apsReceiveProperties.getBatchSizeLimit();
        if (reqs.size() > limit) {
            throw new BizException("单次最多" + limit + "条，请分批调用");
        }
        for (VehicleCbuSoReceiveStatusReq req : reqs) {
            try {
                transactionTemplate.execute(status -> {
                    doReceiveOne(req);
                    return null;
                });
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                String reason = e instanceof BizException ? e.getMessage()
                        : e.getClass().getSimpleName()
                        + (e.getMessage() != null ? ": " + e.getMessage() : "");
                result.addFail(req.getSoNo(), reason);
            }
        }
        result.setTotalCount(reqs.size());
        result.setFailCount(result.getFailItems().size());
        return result;
    }

    /**
     * 单条 APS 生产状态回传处理（幂等/回退跳过；向前流转更新状态/明细/同步记录/事件）。
     */
    private void doReceiveOne(VehicleCbuSoReceiveStatusReq req) {
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

        // 发布红旗生产信息同步事件（仅 1003/1006/1007 触发）
        // 客户系统同步记录（类型3）由事件 handler 在红旗返回后写入（成功/失败按 isSign）
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
    public void receiveQcResult(VehicleCbuSoReceiveQcReq req) {
        if (req == null || StringUtils.isBlank(req.getVin()) || req.getResult() == null) {
            throw new BizException("VIN与质检结果不能为空");
        }
        VehicleCbuSoQcResultEnum qcEnum = VehicleCbuSoQcResultEnum.of(req.getResult());
        if (qcEnum == null) {
            throw new BizException("不支持的质检结果：" + req.getResult());
        }

        // 先查一次仅用于获取 poNo 加锁与基本存在性校验
        VehicleCbuSoDetailDo preDetail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailByVin(req.getVin());
        if (preDetail == null) {
            throw new BizException("VIN[" + req.getVin() + "]未找到车辆明细");
        }
        VehicleCbuSoDo preSo = vehicleCbuSoGateway.getVehicleCbuSoById(preDetail.getId());
        if (preSo == null) {
            throw new BizException("VIN[" + req.getVin() + "]关联SO不存在");
        }

        // PO 维度锁：保函转应收共享 PO 关联保函占用，同 PO 串行，锁释放晚于事务提交
        BlockLock lock;
        try {
            lock = distributedLockTemplate.blockLock("so:qc:po:" + preSo.getPoNo(), 30000, 60000);
        } catch (Exception e) {
            throw new BizException("质检通过获取锁失败，请稍后重试");
        }
        try {
            transactionTemplate.execute(status -> {
                // 加锁后重新查询最新状态，避免并发下基于脏数据计算金额
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

                Integer currentQcResult = so.getQcResult();

                // 质检通过为终态，不允许变更
                if (VehicleCbuSoQcResultEnum.PASSED.getCode().equals(currentQcResult)) {
                    throw new BizException("SO[" + so.getSoNo() + "]当前已为质检通过，不允许变更");
                }

                // 质检不通过不允许改为未收到车辆
                if (VehicleCbuSoQcResultEnum.FAILED.getCode().equals(currentQcResult)
                        && VehicleCbuSoQcResultEnum.NO_VEHICLE.equals(qcEnum)) {
                    throw new BizException("SO[" + so.getSoNo()
                            + "]当前为质检不通过，不允许改为未收到车辆");
                }

                doReceiveQcResult(so, detail, qcEnum, req);
                return null;
            });
        } finally {
            closeLock(lock, "so:qc:po:" + preSo.getPoNo());
        }
    }

    /**
     * 质检结果处理（事务内）：更新 SO 质检信息；质检通过时联动 SO 金额、保函占用变应收、回写 PO 关联保函表、
     * afterCommit 发布 SAP 同步事件。保函转应收失败抛异常由主事务回滚 1008。
     */
    private void doReceiveQcResult(VehicleCbuSoDo so, VehicleCbuSoDetailDo detail,
                                   VehicleCbuSoQcResultEnum qcEnum, VehicleCbuSoReceiveQcReq req) {
        // 更新 SO 质检信息（含当前->上次覆盖逻辑）
        so.updateQcResult(qcEnum.getCode(), req.getRemark());
        so.setOrderStatus(qcResultToOrderStatus(qcEnum));
        if (req.getQcTime() != null) {
            so.setQcResultTime(req.getQcTime());
        }

        // 质检通过：联动 SO 冻结->应收 + 保函占用变应收 + PO 关联保函表回写
        if (VehicleCbuSoQcResultEnum.PASSED.equals(qcEnum)) {
            BigDecimal frozenAmount = so.getFrozenAmount() == null ? BigDecimal.ZERO : so.getFrozenAmount();
            transferFrozenToReceivable(so, detail, frozenAmount);
            so.setFrozenAmount(BigDecimal.ZERO);
            so.setReceivableAmount((so.getReceivableAmount() == null ? BigDecimal.ZERO : so.getReceivableAmount())
                    .add(frozenAmount));
        }
        vehicleCbuSoGateway.updateVehicleCbuSo(so);

        // 质检通过：afterCommit 发布 SAP 同步事件（publishAsync 事务内自动 afterCommit，回滚不发布）
        if (VehicleCbuSoQcResultEnum.PASSED.equals(qcEnum) && sapSyncEnabled) {
            SapOrderSyncReq sapReq = buildSapOrderSyncReq(so, detail);
            eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.SAP,
                    SapEventTypeEnum.SYNC_SAP, so.getSoNo(), sapReq);
        }
    }

    /**
     * 保函占用变应收：调保函 transferToReceivable，按返回明细回写 PO 关联保函表（占用-/应收+）。
     * 保函失败抛异常由主事务回滚 1008。
     */
    private void transferFrozenToReceivable(VehicleCbuSoDo so, VehicleCbuSoDetailDo detail, BigDecimal frozenAmount) {
        if (frozenAmount == null || frozenAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        ExportVehicleLetterGuaranteeTransferReq transferReq = new ExportVehicleLetterGuaranteeTransferReq();
        transferReq.setPoNo(so.getPoNo());
        transferReq.setSoNo(so.getSoNo());
        transferReq.setOrderId(so.getPoId());
        transferReq.setFrozenAmount(frozenAmount);
        transferReq.setVin(detail != null ? detail.getVin() : so.getVin());
        transferReq.setReason("SO单[" + so.getSoNo() + "]质检通过，占用变应收");

        List<ExportVehicleLetterGuaranteeTransferItem> items = letterGuaranteeService.transferToReceivable(transferReq);
        for (ExportVehicleLetterGuaranteeTransferItem item : items) {
            reducePoGuaranteeOccupiedAndRaiseReceivable(so.getPoNo(), item.getLgId(), item.getTransferAmount());
        }
    }

    /**
     * 回写 PO 关联保函表：poCurrentOccupiedAmount -= amount，poCurrentReceivableAmount += amount。
     */
    private void reducePoGuaranteeOccupiedAndRaiseReceivable(String poNo, String lgId, BigDecimal amount) {
        ExportVehicleCbuPoGuaranteeQo qo = new ExportVehicleCbuPoGuaranteeQo();
        qo.setPoNo(poNo);
        List<ExportVehicleCbuPoGuaranteeDo> list = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(qo);
        if (list == null) {
            return;
        }
        for (ExportVehicleCbuPoGuaranteeDo g : list) {
            if (lgId.equals(g.getGuaranteeId())) {
                ExportVehicleCbuPoGuaranteeDo update = new ExportVehicleCbuPoGuaranteeDo();
                update.setId(g.getId());
                BigDecimal occupied = g.getPoCurrentOccupiedAmount() == null
                        ? BigDecimal.ZERO : g.getPoCurrentOccupiedAmount();
                BigDecimal receivable = g.getPoCurrentReceivableAmount() == null
                        ? BigDecimal.ZERO : g.getPoCurrentReceivableAmount();
                update.setPoCurrentOccupiedAmount(occupied.subtract(amount));
                update.setPoCurrentReceivableAmount(receivable.add(amount));
                purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(update);
                break;
            }
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
    /**
     * SAP 手动补推（单条 / 批量）：仅 1008 允许补推，非 1008 归 SKIP；不拦截已成功态。
     * 重新发布 SAP 同步事件（publishAsync 事务外直接发布），SAP 三字段由 SapSyncNotifyHandler 回写。
     *
     * @param req 补推请求（id/ids）
     * @return 操作结果（success/skip/fail）
     */
    public VehicleCbuSoOperateResult reissueSap(VehicleCbuSoReissueSapReq req) {
        List<String> ids = new ArrayList<>();
        if (StringUtils.isNotBlank(req.getId())) {
            ids.add(req.getId());
        }
        if (CollectionUtils.isNotEmpty(req.getIds())) {
            ids.addAll(req.getIds());
        }
        if (ids.isEmpty()) {
            throw new BizException("SAP补推目标为空");
        }
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setIds(ids);
        List<VehicleCbuSoDo> soList = vehicleCbuSoGateway.listVehicleCbuSo(qo);

        VehicleCbuSoOperateResult result = new VehicleCbuSoOperateResult();
        for (VehicleCbuSoDo so : soList) {
            if (!VehicleCbuSoStatusEnum.QC_PASSED.getCode().equals(so.getOrderStatus())) {
                result.addSkip(so.getSoNo(), "状态非质检通过，跳过");
                continue;
            }
            try {
                VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(so.getId());
                SapOrderSyncReq sapReq = buildSapOrderSyncReq(so, detail);
                eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.SAP,
                        SapEventTypeEnum.SYNC_SAP, so.getSoNo(), sapReq);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (Exception e) {
                String reason = e instanceof BizException ? e.getMessage()
                        : e.getClass().getSimpleName()
                        + (e.getMessage() != null ? ": " + e.getMessage() : "");
                result.addFail(so.getSoNo(), reason);
            }
        }
        result.setTotalCount(soList.size());
        result.setFailCount(result.getFailItems().size());
        result.setSkipCount(result.getSkipItems().size());
        return result;
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
        if (req.getPlanOfflineDate() != null) {
            detail.setPlanOfflineDate(req.getPlanOfflineDate());
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
            if (detail.getPlanOfflineDate() != null) {
                req.setPlanOfflineDate(detail.getPlanOfflineDate().format(HONGQI_DATE_FORMAT));
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
        req.setIvSysid("DMS");

        SapOrderSyncReq.IsHead head = new SapOrderSyncReq.IsHead();
        head.setSorder(so.getSoNo());
        head.setPorder(so.getPoNo());
        head.setErdat(so.getReceiveOrderTime() != null
                ? so.getReceiveOrderTime().format(DateTimeFormatter.BASIC_ISO_DATE) : null);
        head.setLstatus("77-质检通过");
        LocalDateTime qcResultTime = so.getQcResultTime();
        head.setLsdatum(qcResultTime != null
                ? qcResultTime.format(DateTimeFormatter.BASIC_ISO_DATE) : null);
        head.setLsuzeit(qcResultTime != null
                ? qcResultTime.format(DateTimeFormatter.ofPattern("HHmmss")) : null);
        head.setBukrs(so.getSalesCompanyCode());
        head.setKunnr(so.getCustomerCode());
        head.setRecordType(convertOrderTypeToRecordType(so.getOrderType()));
        head.setOrderWay("100-红旗CBU订单");
        req.setIsHead(head);

        SapOrderSyncReq.ImTabItem item = new SapOrderSyncReq.ImTabItem();
        item.setSorderSub(so.getSoNo());
        item.setVhcle(detail != null ? detail.getInternalVehicleNo() : null);
        item.setVhvin(detail != null ? detail.getVin() : so.getVin());
        item.setMatnr(detail != null ? detail.getMaterialCode() : so.getMaterialCode());
        item.setMenge(BigDecimal.ONE);
        item.setPriceExw(so.getVehiclePrice());
        req.setImTab(List.of(item));

        return req;
    }

    private String convertOrderTypeToRecordType(Integer orderType) {
        if (orderType == null) {
            return "";
        }
        // 后续补充 SKD/CKD 等映射
        return switch (orderType) {
            case 1 -> "CBU";
            default -> "CBU";
        };
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
        taskExecutor.execute(new AisRunnable(() -> {
            try {
                vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(syncDo);
            } catch (Exception e) {
                log.error("异步写接口同步记录失败, soId={}, interfaceType={}, interfaceDesc={}",
                        syncDo.getSoId(), syncDo.getInterfaceType(), syncDo.getInterfaceDesc(), e);
            }
        }));
    }

    /**
     * 构建接口同步记录（调用 addVehicleCbuSoInterfaceSync 会自动刷新明细表同步快照）。
     */
    private VehicleCbuSoInterfaceSyncDo buildSyncDo(VehicleCbuSoDo so, InterfaceSyncTypeEnum interfaceType,
                                                    String requestInfo, ApsResult apsResult, String reason) {
        VehicleCbuSoInterfaceSyncDo syncDo = new VehicleCbuSoInterfaceSyncDo();
        syncDo.setSoId(so.getId());
        syncDo.setInterfaceType(interfaceType.getCode());
        syncDo.setInterfaceDesc(interfaceType.getMessage());
        syncDo.setCallTime(LocalDateTime.now());
        syncDo.setRequestInfo(StringUtils.left(requestInfo, 2000));
        syncDo.setResultStatus(apsResult != null && apsResult.getCode() == 200 ? 1 : 0);
        syncDo.setResponseInfo(StringUtils.left(
                apsResult == null ? null : apsResult.getMessage(), 2000));
        syncDo.setRemark(reason);
        syncDo.setStatus(1);
        return syncDo;
    }

    /**
     * 安全释放锁：锁提前过期时 close() 会抛 ConcurrentModificationException，捕获后仅记日志，不影响主流程。
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
}
