package com.leapmotor.export.vehicle.po.executor.cmd;

import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAdjustFreezeReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAdjustUnfreezeReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeFreezeReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeUnfreezeReq;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeAdjustUnfreezeResp;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeFreezeResp;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.executor.cmd.VehicleCbuSoCmdExe;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBatchReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoBatchResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoCancelReq;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;

import com.leapmotor.export.vehicle.po.enums.PoOrderTypeEnum;
import com.leapmotor.export.vehicle.po.enums.ExportVehicleCbuPoStatusEnum;
import com.leapmotor.export.nexus.customer.api.CustomerApi;
import com.leapmotor.export.nexus.customer.request.CustomerRequest;
import com.leapmotor.export.nexus.customer.response.CustomerResponse;
import com.leapmotor.export.nexus.vehicle.api.VehicleMaterialApi;
import com.leapmotor.export.nexus.vehicle.api.VehiclePriceHqApi;
import com.leapmotor.export.nexus.vehicle.dto.VehicleMaterialDto;
import com.leapmotor.export.nexus.vehicle.dto.VehiclePriceHqDto;
import com.leapmotor.export.nexus.vehicle.request.VehicleMaterialBatchMatchReq;
import com.leapmotor.export.nexus.vehicle.request.VehiclePriceHqMatchReq;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRefreshPriceReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoRefreshPriceResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSubmitReq;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PO 订单审批流转执行器（提交/驳回、供需审核、产销平衡审核）。
 * 与 ExportVehicleCbuPoCmdExe（CRUD/创建/编辑）分离。
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@Slf4j
@AllArgsConstructor
@Component
public class ExportVehicleCbuPoApprovalCmdExe {

    private ExportVehicleCbuPoGateway purchaseOrderGateway;
    private ExportVehicleCbuPoDetailGateway purchaseOrderDetailGateway;
    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;
    /**
     * 客户主数据，对接 app-ais-export-nexus 客户服务
     */
    private CustomerApi customerApi;
    /**
     * 整编物料主数据，对接 app-ais-export-nexus 整编物料服务
     */
    private VehicleMaterialApi vehicleMaterialApi;
    /**
     * 价格主数据，对接 app-ais-export-nexus 价格服务
     */
    private VehiclePriceHqApi vehiclePriceHqApi;
    /**
     * 保函服务（同项目同微服务，本地事务强一致）
     */
    private ExportVehicleLetterGuaranteeService letterGuaranteeService;
    /**
     * 事件发布（推红旗，T03 事件机制）
     */
    private EventHandlerHelper eventHandlerHelper;
    /**
     * 用于批量操作中 self 代理调用（保证 @Transactional 生效）
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * SO 写操作执行器（供需审核通过生成 SO / 产销平衡驳回作废 SO，同服务内直接调用）
     */
    private VehicleCbuSoCmdExe vehicleCbuSoCmdExe;

    // ==================== 销售提交（4 步编排，任一步失败整体回滚） ====================

    @Transactional(rollbackFor = Exception.class)
    public void submitExportVehicleCbuPo(ExportVehicleCbuPoSubmitReq req) {
        // 查订单 + 草稿态校验
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportVehicleCbuPoStatusEnum.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅草稿态订单可提交");
        }

        // ===== 步骤1 检查订单数据 + 兜底匹配 =====
        if (StringUtils.isBlank(order.getSalesCompanyCode())) {
            throw new BizException("销售公司不能为空");
        }
        CustomerResponse customer = queryCustomerByExternalCode(order.getExternalCustomerCode());
        // 提交为强校验节点：客户主数据必须能查到（查不到说明 nexus 异常或数据缺失），不能静默跳过保函数资金校验
        if (customer == null) {
            throw new BizException("客户主数据查询失败，无法提交，请稍后重试");
        }
        if (StringUtils.isBlank(order.getCustomerCode())) {
            order.setCustomerCode(customer.getCustomerCode());
            order.setCustomerName(customer.getCustomerName());
        }
        // 明细物料兜底（批量匹配：一次 Feign 替代循环内逐条）
        ExportVehicleCbuPoDetailQo detailQo = new ExportVehicleCbuPoDetailQo();
        detailQo.setPoNo(order.getPoNo());
        List<ExportVehicleCbuPoDetailDo> details = purchaseOrderDetailGateway.listExportVehicleCbuPoDetail(detailQo);
        Map<String, VehicleMaterialDto> materialMap = queryMaterialByCodes(details.stream()
                .filter(d -> StringUtils.isBlank(d.getMaterialCode()))
                .map(ExportVehicleCbuPoDetailDo::getSyncMaterialCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        for (ExportVehicleCbuPoDetailDo d : details) {
            if (StringUtils.isBlank(d.getMaterialCode())) {
                VehicleMaterialDto m = materialMap.get(d.getSyncMaterialCode());
                if (m == null) {
                    throw new BizException("明细" + d.getOrderDetailNumber() + "物料编码无法匹配，不允许提交");
                }
                d.setMaterialCode(m.getCode());
                d.setMaterialDescription(m.getMaterialDesc());
                d.setSalesMarket(m.getSalesMarket());
                d.setModelYear(m.getYearType());
                d.setOptionalPackage(m.getOptionPackage());
            }
        }

        // ===== 步骤2 价格匹配（单价空的明细按 物料+订单类型+创建时间 取审批最新价） =====
        LocalDateTime orderCreateTime = order.getCreateTime();
        Map<String, BigDecimal> priceMap = queryLatestApprovedPrices(
                details.stream().map(ExportVehicleCbuPoDetailDo::getMaterialCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()),
                order.getOrderType(), orderCreateTime);
        List<String> noPriceMaterials = new ArrayList<>();
        for (ExportVehicleCbuPoDetailDo d : details) {
            if (d.getUnitPrice() == null) {
                BigDecimal price = priceMap.get(d.getMaterialCode());
                if (price == null) {
                    noPriceMaterials.add(d.getMaterialCode());
                    continue;
                }
                d.setUnitPrice(price);
                if (d.getDemandQuantity() == null) {
                    throw new BizException("明细" + d.getOrderDetailNumber() + "需求数量为空，不允许提交");
                }
                d.setTotalAmount(price.multiply(d.getDemandQuantity()));
            }
        }
        if (!noPriceMaterials.isEmpty()) {
            throw new BizException("提交失败，物料编码" + String.join("、", noPriceMaterials) + "无法匹配到有效价格");
        }
        // 重算总金额
        order.setTotalAmount(details.stream().map(ExportVehicleCbuPoDetailDo::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));

        // ===== 步骤3 资金校验与保函冻结 =====
        // nexus 资金校验类型：1010=无 / 1020=保函
        boolean isGuarantee = customer != null && customer.getFundVerifyType() != null && customer.getFundVerifyType() == 1020;
        if (isGuarantee) {
            ExportVehicleLetterGuaranteeFreezeReq freezeReq = new ExportVehicleLetterGuaranteeFreezeReq();
            freezeReq.setCustomerId(customer.getId());
            freezeReq.setOrderType(PoOrderTypeEnum.getName(order.getOrderType()));
            freezeReq.setOrderId(order.getId());
            freezeReq.setOrderNo(order.getPoNo());
            freezeReq.setPoNo(order.getPoNo());
            freezeReq.setRequiredAmount(order.getTotalAmount());
            // 保函数侧按订单创建时间筛选可用保函（失效日期 ≥ 创建时间 + coverDays），需传订单创建时间
            freezeReq.setOrderCreateTime(order.getCreateTime().toLocalDate());
            ExportVehicleLetterGuaranteeFreezeResp freezeResp = letterGuaranteeService.freeze(freezeReq);
            if (freezeResp == null || !Boolean.TRUE.equals(freezeResp.getSuccess()) || CollectionUtils.isEmpty(freezeResp.getFrozenItems())) {
                throw new BizException("提交失败，资金校验未通过，请联系商务");
            }
            // 写 PO-保函关联表（一个 PO 多保函）
            List<ExportVehicleCbuPoGuaranteeDo> guarantees = freezeResp.getFrozenItems().stream().map(item -> {
                ExportVehicleCbuPoGuaranteeDo g = new ExportVehicleCbuPoGuaranteeDo();
                g.setPoNo(order.getPoNo());
                g.setGuaranteeId(item.getLgId());
                g.setGuaranteeNumber(item.getLgCode());
                g.setPoCurrentOccupiedAmount(item.getFrozenAmount());
                g.setPoCurrentReceivableAmount(BigDecimal.ZERO);
                return g;
            }).toList();
            purchaseOrderGuaranteeGateway.addBatchExportVehicleCbuPoGuarantee(guarantees);
        }
        // fundCheckType = 无/null → 跳过保函

        // ===== 提交完成：状态→销售已提交 + submit_time + 回填订单/明细 =====
        order.setOrderStatus(ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode());
        order.setSubmitTime(LocalDateTime.now());
        purchaseOrderGateway.updateExportVehicleCbuPo(order);
        for (ExportVehicleCbuPoDetailDo d : details) {
            purchaseOrderDetailGateway.updateExportVehicleCbuPoDetail(d);
        }
    }

    // ==================== 销售驳回（终态 + 推红旗） ====================

    @Transactional(rollbackFor = Exception.class)
    public void rejectExportVehicleCbuPo(ExportVehicleCbuPoRejectReq req) {
        // 查订单 + 草稿态校验
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportVehicleCbuPoStatusEnum.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅草稿态订单可驳回");
        }

        // 状态→销售已驳回（终态）+ 驳回时间/人/说明（只 set 这些，updateById 防篡改）
        ExportVehicleCbuPoDo update = new ExportVehicleCbuPoDo();
        update.setId(req.getPoId());
        update.setOrderStatus(ExportVehicleCbuPoStatusEnum.SALES_REJECTED.getCode());
        update.setRejectTime(LocalDateTime.now());
        update.setRejectUser(LoginUserUtils.getCurrentUserOrSysDefault().getUserName());
        update.setRejectReason(req.getRejectReason());
        purchaseOrderGateway.updateExportVehicleCbuPo(update);

        // 推红旗审批结果（事件机制：落 event_notify_task，XXL-Job 异步重试，订单状态不因推送回滚）
        CbuApprovalResultReq pushReq = new CbuApprovalResultReq();
        pushReq.setPoNo(order.getPoNo());
        pushReq.setPlanVersion(order.getPlanVersion());
        pushReq.setStatus("R");
        pushReq.setAuditTime(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").format(LocalDateTime.now()));
        pushReq.setRemark(req.getRejectReason());
        eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.HONGQI, HongqiEventTypeEnum.PUSH_CBU_APPROVAL_RESULT, order.getId(), pushReq);
    }

    // ==================== 供需审核通过（生成 SO 单） ====================

    @Transactional(rollbackFor = Exception.class)
    public void supplyApprove(ExportVehicleCbuPoSupplyApproveReq req) {
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅销售已提交态可供需审核");
        }
        // 状态→供需审核通过 + 审核时间/人（只 set 这些，updateById 防篡改）
        ExportVehicleCbuPoDo update = new ExportVehicleCbuPoDo();
        update.setId(req.getPoId());
        update.setOrderStatus(ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_APPROVED.getCode());
        update.setSupplyDemandApproveTime(LocalDateTime.now());
        update.setSupplyDemandApprover(LoginUserUtils.getCurrentUserOrSysDefault().getUserName());
        purchaseOrderGateway.updateExportVehicleCbuPo(update);

        // 调 SO 创建（一车一单，PO 传参；SO 侧拆行号/生成单号）
        ExportVehicleCbuPoDetailQo detailQo = new ExportVehicleCbuPoDetailQo();
        detailQo.setPoNo(order.getPoNo());
        List<ExportVehicleCbuPoDetailDo> details = purchaseOrderDetailGateway.listExportVehicleCbuPoDetail(detailQo);
        SoCreateReq soReq = buildSoCreateReq(order, details);
        SoResult soResult = vehicleCbuSoCmdExe.createSo(soReq);
        if (soResult == null || CollectionUtils.isEmpty(soResult.getData())) {
            throw new BizException("SO 单创建失败：未生成任何 SO");
        }
    }

    private SoCreateReq buildSoCreateReq(ExportVehicleCbuPoDo order, List<ExportVehicleCbuPoDetailDo> details) {
        SoCreateReq req = new SoCreateReq();
        req.setPoNo(order.getPoNo());
        req.setPoId(order.getId());
        req.setOrderType(order.getOrderType());
        req.setSalesCompanyCode(order.getSalesCompanyCode());
        req.setCustomerCode(order.getCustomerCode());
        req.setCustomerName(order.getCustomerName());
        req.setExternalCustomerCode(order.getExternalCustomerCode());
        req.setPlanVersion(order.getPlanVersion());
        req.setReceiveOrderTime(order.getCreateTime());
        req.setDataSource(order.getDataSource());
        req.setDetails(details.stream().map(d -> {
            SoCreateReq.SoDetailReq sd = new SoCreateReq.SoDetailReq();
            sd.setPoDetailId(d.getId());
            sd.setPoDetailNo(d.getPoDetailNo());
            sd.setDemandQuantity(d.getDemandQuantity());
            sd.setUnitPrice(d.getUnitPrice());
            sd.setVinYear(d.getVinYear());
            sd.setExpectedOfflineDate(d.getExpectedOfflineDate());
            sd.setProductionRemark(d.getProductionRemark());
            sd.setMaterialCode(d.getMaterialCode());
            sd.setMaterialDescription(d.getMaterialDescription());
            return sd;
        }).toList());
        return req;
    }

    // ==================== 供需审核驳回（解冻保函 + 推红旗） ====================

    @Transactional(rollbackFor = Exception.class)
    public void supplyReject(ExportVehicleCbuPoSupplyRejectReq req) {
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅销售已提交态可供需审核驳回");
        }

        // 状态→供需审核驳回（终态）+ 驳回信息
        ExportVehicleCbuPoDo update = new ExportVehicleCbuPoDo();
        update.setId(req.getPoId());
        update.setOrderStatus(ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_REJECTED.getCode());
        update.setRejectTime(LocalDateTime.now());
        update.setRejectUser(LoginUserUtils.getCurrentUserOrSysDefault().getUserName());
        update.setRejectReason(req.getRejectReason());
        purchaseOrderGateway.updateExportVehicleCbuPo(update);

        // 保函解冻（传 poNo + orderId，保函侧完成主数据更新 + 交易明细）
        ExportVehicleLetterGuaranteeUnfreezeReq unfreezeReq = new ExportVehicleLetterGuaranteeUnfreezeReq();
        unfreezeReq.setPoNo(order.getPoNo());
        unfreezeReq.setOrderId(order.getId());
        unfreezeReq.setReason("供需审核驳回");
        letterGuaranteeService.unfreeze(unfreezeReq);

        // PO-保函关联表占用金额清零（按 poNo 查 + 逐条 update po_current_occupied_amount=0）
        ExportVehicleCbuPoGuaranteeQo gqo = new ExportVehicleCbuPoGuaranteeQo();
        gqo.setPoNo(order.getPoNo());
        List<ExportVehicleCbuPoGuaranteeDo> guarantees = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(gqo);
        for (ExportVehicleCbuPoGuaranteeDo g : guarantees) {
            ExportVehicleCbuPoGuaranteeDo gUpdate = new ExportVehicleCbuPoGuaranteeDo();
            gUpdate.setId(g.getId());
            gUpdate.setPoCurrentOccupiedAmount(BigDecimal.ZERO);
            purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(gUpdate);
        }

        // 推红旗审批结果（事件机制）
        CbuApprovalResultReq pushReq = new CbuApprovalResultReq();
        pushReq.setPoNo(order.getPoNo());
        pushReq.setPlanVersion(order.getPlanVersion());
        pushReq.setStatus("R");
        pushReq.setAuditTime(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").format(LocalDateTime.now()));
        pushReq.setRemark(req.getRejectReason());
        eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.HONGQI, HongqiEventTypeEnum.PUSH_CBU_APPROVAL_RESULT, order.getId(), pushReq);
    }

    // ==================== 产销平衡审核通过（终态 + 推红旗） ====================

    @Transactional(rollbackFor = Exception.class)
    public void balanceApprove(ExportVehicleCbuPoBalanceApproveReq req) {
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_APPROVED.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅供需审核通过态可产销平衡审核");
        }
        // 状态→产销平衡通过（终态）+ 审核时间/人
        ExportVehicleCbuPoDo update = new ExportVehicleCbuPoDo();
        update.setId(req.getPoId());
        update.setOrderStatus(ExportVehicleCbuPoStatusEnum.PRODUCTION_SALES_BALANCE_APPROVED.getCode());
        update.setProductionSalesBalanceApproveTime(LocalDateTime.now());
        update.setProductionSalesBalanceApprover(LoginUserUtils.getCurrentUserOrSysDefault().getUserName());
        purchaseOrderGateway.updateExportVehicleCbuPo(update);

        // 推红旗审批结果（通过）
        CbuApprovalResultReq pushReq = new CbuApprovalResultReq();
        pushReq.setPoNo(order.getPoNo());
        pushReq.setPlanVersion(order.getPlanVersion());
        pushReq.setStatus("A");
        pushReq.setAuditTime(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").format(LocalDateTime.now()));
        eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.HONGQI, HongqiEventTypeEnum.PUSH_CBU_APPROVAL_RESULT, order.getId(), pushReq);
    }

    // ==================== 产销平衡审核驳回（SO 作废 + 保函解冻 + 推红旗，APS 同步由 SO 侧负责） ====================

    @Transactional(rollbackFor = Exception.class)
    public void balanceReject(ExportVehicleCbuPoBalanceRejectReq req) {
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_APPROVED.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅供需审核通过态可产销平衡驳回");
        }

        // 查 PO 下所有 SO 状态（同服务内直接查询，不再走 Feign）
        List<VehicleCbuSoDo> soItems = vehicleCbuSoCmdExe.listVehicleCbuSoByPoNo(order.getPoNo());

        // 校验 c：SO 状态只能是 作废/未下发生产/排产中（焊装上线及之后不可驳回）
        for (VehicleCbuSoDo so : soItems) {
            Integer status = so.getOrderStatus();
            // status==null 视为脏数据，与下游 processCancelByPo 的可作废判断口径一致，避免前置放行后下游失败
            if (status != null
                    && (VehicleCbuSoStatusEnum.CANCELLED.getCode().equals(status)
                    || VehicleCbuSoStatusEnum.NOT_ISSUED.getCode().equals(status)
                    || VehicleCbuSoStatusEnum.SCHEDULING.getCode().equals(status))) {
                continue;
            }
            throw new BizException("存在不可作废状态的SO，焊装上线及之后不允许驳回");
        }

        // 状态→产销平衡驳回（终态）+ 驳回信息
        ExportVehicleCbuPoDo update = new ExportVehicleCbuPoDo();
        update.setId(req.getPoId());
        update.setOrderStatus(ExportVehicleCbuPoStatusEnum.PRODUCTION_SALES_BALANCE_REJECTED.getCode());
        update.setRejectTime(LocalDateTime.now());
        update.setRejectUser(LoginUserUtils.getCurrentUserOrSysDefault().getUserName());
        update.setRejectReason(req.getRejectReason());
        purchaseOrderGateway.updateExportVehicleCbuPo(update);

        // 保函解冻 + 关联表占用清零
        ExportVehicleLetterGuaranteeUnfreezeReq unfreezeReq = new ExportVehicleLetterGuaranteeUnfreezeReq();
        unfreezeReq.setPoNo(order.getPoNo());
        unfreezeReq.setOrderId(order.getId());
        unfreezeReq.setReason("产销平衡驳回");
        letterGuaranteeService.unfreeze(unfreezeReq);
        ExportVehicleCbuPoGuaranteeQo gqo = new ExportVehicleCbuPoGuaranteeQo();
        gqo.setPoNo(order.getPoNo());
        List<ExportVehicleCbuPoGuaranteeDo> guarantees = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(gqo);
        for (ExportVehicleCbuPoGuaranteeDo g : guarantees) {
            ExportVehicleCbuPoGuaranteeDo gUpdate = new ExportVehicleCbuPoGuaranteeDo();
            gUpdate.setId(g.getId());
            gUpdate.setPoCurrentOccupiedAmount(BigDecimal.ZERO);
            purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(gUpdate);
        }

        // SO 全作废放最后：本地写（状态/保函解冻/关联清零）先成功，再调含远程的 SO cancel；cancel 内 APS 远程失败会回滚本地但 APS 已作废属 SO 侧问题，PO 侧保证本地失败不污染 APS
        VehicleCbuSoCancelReq cancelReq = new VehicleCbuSoCancelReq();
        cancelReq.setPoNo(order.getPoNo());
        cancelReq.setReason("产销平衡驳回作废：" + req.getRejectReason());
        VehicleCbuSoOperateResult cancelResult = vehicleCbuSoCmdExe.cancel(cancelReq);
        if (CollectionUtils.isNotEmpty(cancelResult.getFailItems())) {
            String failDetail = cancelResult.getFailItems().stream().map(i -> i.getSoNo() + ":" + i.getReason()).collect(Collectors.joining(";"));
            throw new BizException("PO作废失败，部分SO作废异常：" + failDetail);
        }

        // 推红旗审批结果（驳回）
        CbuApprovalResultReq pushReq = new CbuApprovalResultReq();
        pushReq.setPoNo(order.getPoNo());
        pushReq.setPlanVersion(order.getPlanVersion());
        pushReq.setStatus("R");
        pushReq.setAuditTime(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").format(LocalDateTime.now()));
        pushReq.setRemark(req.getRejectReason());
        eventHandlerHelper.publishAsync(ThirdTargetPlatformEnum.HONGQI, HongqiEventTypeEnum.PUSH_CBU_APPROVAL_RESULT, order.getId(), pushReq);
    }

    // ==================== 刷新订单价格（联动保函 / SO） ====================

    @Transactional(rollbackFor = Exception.class)
    public ExportVehicleCbuPoRefreshPriceResp refreshPrice(ExportVehicleCbuPoRefreshPriceReq req) {
        ExportVehicleCbuPoRefreshPriceResp result = new ExportVehicleCbuPoRefreshPriceResp();
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }

        String status = order.getOrderStatus();
        // 分支B（已审非终态）= 20 销售已提交 / 40 供需通过 / 60 产销通过（需 SO 状态校验 + 保函联动）
        // 分支A（草稿/各类驳回终态）= 10 / 30 / 50 / 70（仅重算价格，不调保函不调SO）
        boolean isBranchB = ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode().equals(status)
                || ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_APPROVED.getCode().equals(status)
                || ExportVehicleCbuPoStatusEnum.PRODUCTION_SALES_BALANCE_APPROVED.getCode().equals(status);

        // 查明细
        ExportVehicleCbuPoDetailQo detailQo = new ExportVehicleCbuPoDetailQo();
        detailQo.setPoNo(order.getPoNo());
        List<ExportVehicleCbuPoDetailDo> details = purchaseOrderDetailGateway.listExportVehicleCbuPoDetail(detailQo);

        // 分支B：SO 状态校验（步骤1）— 质检通过/已收款的车辆禁止刷新价格，避免 PO 新价与 SO 旧价脱钩
        if (isBranchB) {
            List<VehicleCbuSoDo> soItems = vehicleCbuSoCmdExe.listVehicleCbuSoByPoNo(order.getPoNo());
            for (VehicleCbuSoDo so : soItems) {
                Integer soStatus = so.getOrderStatus();
                if (VehicleCbuSoStatusEnum.QC_PASSED.getCode().equals(soStatus)
                        || VehicleCbuSoStatusEnum.RECEIVED.getCode().equals(soStatus)) {
                    throw new BizException("存在质检通过/已收款的车辆，不允许刷新价格");
                }
            }
        }

        // ===== 重算价格（步骤2，同 提交的匹配规则；批量一次 Feign） =====
        BigDecimal oldTotal = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        result.setOldTotal(oldTotal);
        int updatedCount = 0;
        int unmatchedCount = 0;
        Map<String, BigDecimal> priceMap = queryLatestApprovedPrices(
                details.stream().map(ExportVehicleCbuPoDetailDo::getMaterialCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()),
                order.getOrderType(), order.getCreateTime());
        for (ExportVehicleCbuPoDetailDo d : details) {
            if (StringUtils.isBlank(d.getMaterialCode())) {
                continue;
            }
            BigDecimal newPrice = priceMap.get(d.getMaterialCode());
            if (newPrice != null) {
                updatedCount++;
                d.setUnitPrice(newPrice);
                if (d.getDemandQuantity() == null) {
                    throw new BizException("明细" + d.getOrderDetailNumber() + "需求数量为空，不允许刷新价格");
                }
                d.setTotalAmount(newPrice.multiply(d.getDemandQuantity()));
                purchaseOrderDetailGateway.updateExportVehicleCbuPoDetail(d);
            } else {
                unmatchedCount++;
                // 已审态（分支B）不允许把价格置空，否则破坏已审价格不变量并按缩水总额误触发保函调减；仅草稿态（分支A）留空
                if (isBranchB) {
                    throw new BizException("物料 " + d.getMaterialCode() + " 刷新后匹配不到有效价格，不允许刷新");
                }
                d.setUnitPrice(null);
                d.setTotalAmount(null);
                purchaseOrderDetailGateway.updatePriceById(d.getId(), null, null);
            }
        }
        BigDecimal newTotal = details.stream().map(d -> d.getTotalAmount() != null ? d.getTotalAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);

        // ===== 更新主表总金额（明细已在上方按匹配结果逐条更新） =====
        order.setTotalAmount(newTotal);
        purchaseOrderGateway.updateExportVehicleCbuPo(order);

        // ===== 分支B: 联动保函 + SO =====
        String guaranteeAction = "无调整";
        if (isBranchB) {
            BigDecimal diff = newTotal.subtract(oldTotal);
            int cmp = diff.compareTo(BigDecimal.ZERO);

            if (cmp < 0) {
                // (2) 新总金额 < 原：调减保函
                BigDecimal amountDiff = oldTotal.subtract(newTotal);
                guaranteeAction = "解冻 " + amountDiff.stripTrailingZeros().toPlainString();
                ExportVehicleLetterGuaranteeAdjustUnfreezeReq unfreezeReq = new ExportVehicleLetterGuaranteeAdjustUnfreezeReq();
                unfreezeReq.setPoNo(order.getPoNo());
                unfreezeReq.setOrderId(order.getId());
                unfreezeReq.setAmountDiff(amountDiff);
                unfreezeReq.setReason("刷新价格调减");
                ExportVehicleLetterGuaranteeAdjustUnfreezeResp unfreezeResp = letterGuaranteeService.adjustUnfreeze(unfreezeReq);
                if (unfreezeResp == null || !Boolean.TRUE.equals(unfreezeResp.getSuccess())) {
                    throw new BizException("保函调减失败: " + (unfreezeResp != null ? unfreezeResp.getFailMessage() : "unknown"));
                }
                // 按 unfreezeItems 逐条调减 PO-保函关联表占用金额
                if (unfreezeResp.getUnfreezeItems() != null) {
                    for (ExportVehicleLetterGuaranteeAdjustUnfreezeResp.UnfreezeItem item : unfreezeResp.getUnfreezeItems()) {
                        reduceGuaranteeOccupied(order.getPoNo(), item.getLgId(), item.getUnfrozenAmount());
                    }
                }
            } else if (cmp > 0) {
                // (3) 新总金额 > 原：补冻保函（保函侧自动判断可用金额是否充足）
                guaranteeAction = "补冻 " + diff.stripTrailingZeros().toPlainString();
                CustomerResponse refreshCustomer = queryCustomerByExternalCode(order.getExternalCustomerCode());
                if (refreshCustomer == null) {
                    throw new BizException("客户主数据查询失败，无法刷新价格，请稍后重试");
                }
                ExportVehicleLetterGuaranteeAdjustFreezeReq freezeReq = new ExportVehicleLetterGuaranteeAdjustFreezeReq();
                freezeReq.setCustomerId(refreshCustomer.getId());
                freezeReq.setOrderType(PoOrderTypeEnum.getName(order.getOrderType()));
                freezeReq.setOrderId(order.getId());
                freezeReq.setOrderNo(order.getPoNo());
                freezeReq.setPoNo(order.getPoNo());
                freezeReq.setAmountDiff(diff);
                // 保函数侧按订单创建时间筛选可用保函（失效日期 ≥ 创建时间 + coverDays），需传订单创建时间
                freezeReq.setOrderCreateTime(order.getCreateTime().toLocalDate());
                freezeReq.setReason("刷新价格调增");
                ExportVehicleLetterGuaranteeFreezeResp freezeResp = letterGuaranteeService.adjustFreeze(freezeReq);
                if (freezeResp == null || !Boolean.TRUE.equals(freezeResp.getSuccess()) || CollectionUtils.isEmpty(freezeResp.getFrozenItems())) {
                    String failMsg = (freezeResp != null && freezeResp.getFailMessage() != null) ? freezeResp.getFailMessage() : "剩余可用金额不足";
                    throw new BizException(failMsg);
                }
                // 写/更新 PO-保函关联表：调增可能命中已冻结过的保函(同 poNo+lgId 已存在)，按存在性 update 累加/insert，避免撞 uk_po_guarantee
                ExportVehicleCbuPoGuaranteeQo existGqo = new ExportVehicleCbuPoGuaranteeQo();
                existGqo.setPoNo(order.getPoNo());
                Map<String, ExportVehicleCbuPoGuaranteeDo> existMap = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(existGqo).stream()
                        .collect(Collectors.toMap(ExportVehicleCbuPoGuaranteeDo::getGuaranteeId, g -> g, (a, b) -> a));
                List<ExportVehicleCbuPoGuaranteeDo> toInsert = new ArrayList<>();
                for (ExportVehicleLetterGuaranteeFreezeResp.FrozenItem item : freezeResp.getFrozenItems()) {
                    ExportVehicleCbuPoGuaranteeDo exist = existMap.get(item.getLgId());
                    if (exist != null) {
                        ExportVehicleCbuPoGuaranteeDo upd = new ExportVehicleCbuPoGuaranteeDo();
                        upd.setId(exist.getId());
                        BigDecimal base = exist.getPoCurrentOccupiedAmount() != null ? exist.getPoCurrentOccupiedAmount() : BigDecimal.ZERO;
                        upd.setPoCurrentOccupiedAmount(base.add(item.getFrozenAmount()));
                        purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(upd);
                    } else {
                        ExportVehicleCbuPoGuaranteeDo g = new ExportVehicleCbuPoGuaranteeDo();
                        g.setPoNo(order.getPoNo());
                        g.setGuaranteeId(item.getLgId());
                        g.setGuaranteeNumber(item.getLgCode());
                        g.setPoCurrentOccupiedAmount(item.getFrozenAmount());
                        g.setPoCurrentReceivableAmount(BigDecimal.ZERO);
                        toInsert.add(g);
                    }
                }
                if (!toInsert.isEmpty()) {
                    purchaseOrderGuaranteeGateway.addBatchExportVehicleCbuPoGuarantee(toInsert);
                }
            }
            // 新=原：不调保函，直接更新 SO

            // 联动 SO：按明细新单价更新 PO 下 SO 的车辆价/冻结金额（同事务，作废SO跳过）
            SoUpdatePriceReq soUpdateReq = new SoUpdatePriceReq();
            soUpdateReq.setPoNo(order.getPoNo());
            soUpdateReq.setDetails(details.stream()
                    .filter(d -> d.getId() != null && d.getUnitPrice() != null)
                    .map(d -> {
                        SoUpdatePriceReq.DetailPrice dp = new SoUpdatePriceReq.DetailPrice();
                        dp.setPoDetailId(d.getId());
                        dp.setUnitPrice(d.getUnitPrice());
                        return dp;
                    }).toList());
            vehicleCbuSoCmdExe.updatePrice(soUpdateReq);
        }

        result.setNewTotal(newTotal);
        result.setUpdatedDetailCount(updatedCount);
        result.setUnmatchedDetailCount(unmatchedCount);
        result.setGuaranteeAction(guaranteeAction);
        if (newTotal.compareTo(oldTotal) != 0) {
            result.setMessage("总金额由 " + oldTotal.stripTrailingZeros().toPlainString()
                    + " 变更为 " + newTotal.stripTrailingZeros().toPlainString());
        } else {
            result.setMessage("金额未发生变更");
        }
        return result;
    }

    // ==================== 批量操作（每条独立事务，部分成功部分失败，汇总返回） ====================

    /**
     * 提取错误摘要：BizException 取 message，其他异常取类名
     */
    private String errorSummary(Exception e) {
        if (e instanceof BizException) {
            return e.getMessage();
        }
        return e.getClass().getSimpleName() + (e.getMessage() != null ? ": " + e.getMessage() : "");
    }

    private ExportVehicleCbuPoBatchResp runBatch(List<String> poIds, java.util.function.Consumer<String> op) {
        ExportVehicleCbuPoBatchResp resp = new ExportVehicleCbuPoBatchResp();
        resp.setTotal(poIds.size());
        int success = 0, fail = 0;
        for (String poId : poIds) {
            try {
                op.accept(poId);
                success++;
            } catch (Exception e) {
                fail++;
                ExportVehicleCbuPoBatchResp.FailItem item = new ExportVehicleCbuPoBatchResp.FailItem();
                item.setPoId(poId);
                item.setReason(errorSummary(e));
                resp.getFailItems().add(item);
            }
        }
        resp.setSuccessCount(success);
        resp.setFailCount(fail);
        return resp;
    }

    public ExportVehicleCbuPoBatchResp batchSubmit(ExportVehicleCbuPoBatchReq req) {
        ExportVehicleCbuPoApprovalCmdExe self = applicationContext.getBean(ExportVehicleCbuPoApprovalCmdExe.class);
        return runBatch(req.getPoIds(), poId -> {
            ExportVehicleCbuPoSubmitReq single = new ExportVehicleCbuPoSubmitReq();
            single.setPoId(poId);
            self.submitExportVehicleCbuPo(single);
        });
    }

    public ExportVehicleCbuPoBatchResp batchReject(ExportVehicleCbuPoBatchReq req) {
        ExportVehicleCbuPoApprovalCmdExe self = applicationContext.getBean(ExportVehicleCbuPoApprovalCmdExe.class);
        return runBatch(req.getPoIds(), poId -> {
            ExportVehicleCbuPoRejectReq single = new ExportVehicleCbuPoRejectReq();
            single.setPoId(poId);
            single.setRejectReason(req.getRejectReason());
            self.rejectExportVehicleCbuPo(single);
        });
    }

    public ExportVehicleCbuPoBatchResp batchSupplyApprove(ExportVehicleCbuPoBatchReq req) {
        ExportVehicleCbuPoApprovalCmdExe self = applicationContext.getBean(ExportVehicleCbuPoApprovalCmdExe.class);
        return runBatch(req.getPoIds(), poId -> {
            ExportVehicleCbuPoSupplyApproveReq single = new ExportVehicleCbuPoSupplyApproveReq();
            single.setPoId(poId);
            self.supplyApprove(single);
        });
    }

    public ExportVehicleCbuPoBatchResp batchSupplyReject(ExportVehicleCbuPoBatchReq req) {
        ExportVehicleCbuPoApprovalCmdExe self = applicationContext.getBean(ExportVehicleCbuPoApprovalCmdExe.class);
        return runBatch(req.getPoIds(), poId -> {
            ExportVehicleCbuPoSupplyRejectReq single = new ExportVehicleCbuPoSupplyRejectReq();
            single.setPoId(poId);
            single.setRejectReason(req.getRejectReason());
            self.supplyReject(single);
        });
    }

    public ExportVehicleCbuPoBatchResp batchBalanceApprove(ExportVehicleCbuPoBatchReq req) {
        ExportVehicleCbuPoApprovalCmdExe self = applicationContext.getBean(ExportVehicleCbuPoApprovalCmdExe.class);
        return runBatch(req.getPoIds(), poId -> {
            ExportVehicleCbuPoBalanceApproveReq single = new ExportVehicleCbuPoBalanceApproveReq();
            single.setPoId(poId);
            self.balanceApprove(single);
        });
    }

    public ExportVehicleCbuPoBatchResp batchBalanceReject(ExportVehicleCbuPoBatchReq req) {
        ExportVehicleCbuPoApprovalCmdExe self = applicationContext.getBean(ExportVehicleCbuPoApprovalCmdExe.class);
        return runBatch(req.getPoIds(), poId -> {
            ExportVehicleCbuPoBalanceRejectReq single = new ExportVehicleCbuPoBalanceRejectReq();
            single.setPoId(poId);
            single.setRejectReason(req.getRejectReason());
            self.balanceReject(single);
        });
    }

    // ==================== 保函关联表辅助方法 ====================

    /**
     * 按 lgId 调减 PO-保函关联表的占用金额
     */
    private void reduceGuaranteeOccupied(String poNo, String lgId, BigDecimal amount) {
        ExportVehicleCbuPoGuaranteeQo qo = new ExportVehicleCbuPoGuaranteeQo();
        qo.setPoNo(poNo);
        List<ExportVehicleCbuPoGuaranteeDo> list = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(qo);
        for (ExportVehicleCbuPoGuaranteeDo g : list) {
            if (lgId.equals(g.getGuaranteeId())) {
                BigDecimal current = g.getPoCurrentOccupiedAmount() != null ? g.getPoCurrentOccupiedAmount() : BigDecimal.ZERO;
                ExportVehicleCbuPoGuaranteeDo update = new ExportVehicleCbuPoGuaranteeDo();
                update.setId(g.getId());
                update.setPoCurrentOccupiedAmount(current.subtract(amount));
                purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(update);
                break;
            }
        }
    }

    // ==================== 主数据查询辅助方法（直调 nexus Feign API） ====================

    private CustomerResponse queryCustomerByExternalCode(String externalCode) {
        if (externalCode == null || externalCode.isEmpty()) {
            return null;
        }
        try {
            CustomerRequest req = new CustomerRequest();
            req.setExternalCode(externalCode);
            AjaxResult<List<CustomerResponse>> ret = customerApi.list(req);
            List<CustomerResponse> list = (ret != null) ? ret.getData() : null;
            return (list != null && !list.isEmpty()) ? list.get(0) : null;
        } catch (Exception e) {
            log.error("客户主数据查询失败 externalCode={}", externalCode, e);
            return null;
        }
    }

    /** 批量查整编物料主数据（一次 Feign），返回 code → dto，循环内查 Map 替代逐条 */
    private Map<String, VehicleMaterialDto> queryMaterialByCodes(List<String> materialCodes) {
        if (materialCodes == null || materialCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            VehicleMaterialBatchMatchReq req = new VehicleMaterialBatchMatchReq();
            req.setMaterialCodes(materialCodes);
            AjaxResult<List<VehicleMaterialDto>> ret = vehicleMaterialApi.batchMatch(req);
            List<VehicleMaterialDto> list = (ret != null) ? ret.getData() : null;
            if (list == null || list.isEmpty()) {
                return Collections.emptyMap();
            }
            return list.stream().collect(Collectors.toMap(VehicleMaterialDto::getCode, dto -> dto, (a, b) -> a));
        } catch (Exception e) {
            log.error("整编主数据批量查询失败 codes={}", materialCodes, e);
            return Collections.emptyMap();
        }
    }

    /** 批量匹配价格（一次 Feign batchMatch），返回 materialCode → price，无价的不入 Map */
    private Map<String, BigDecimal> queryLatestApprovedPrices(List<String> materialCodes, Integer orderType, LocalDateTime orderCreateTime) {
        if (materialCodes == null || materialCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<VehiclePriceHqMatchReq> reqs = materialCodes.stream().map(code -> {
                VehiclePriceHqMatchReq r = new VehiclePriceHqMatchReq();
                r.setMaterialCode(code);
                r.setOrderType(orderType != null ? String.valueOf(orderType) : null);
                r.setOrderCreateTime(orderCreateTime);
                return r;
            }).collect(Collectors.toList());
            AjaxResult<List<VehiclePriceHqDto>> ret = vehiclePriceHqApi.batchMatch(reqs);
            List<VehiclePriceHqDto> list = (ret != null) ? ret.getData() : null;
            if (list == null || list.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, BigDecimal> map = new HashMap<>();
            for (int i = 0; i < materialCodes.size() && i < list.size(); i++) {
                VehiclePriceHqDto dto = list.get(i);
                if (dto != null && dto.getPrice() != null) {
                    map.put(materialCodes.get(i), dto.getPrice());
                }
            }
            return map;
        } catch (Exception e) {
            log.error("价格主数据批量查询失败 codes={}", materialCodes, e);
            return Collections.emptyMap();
        }
    }
}
