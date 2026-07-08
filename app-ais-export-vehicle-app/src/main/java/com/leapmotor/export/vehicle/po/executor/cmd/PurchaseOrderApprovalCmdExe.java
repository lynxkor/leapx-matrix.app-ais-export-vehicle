package com.leapmotor.export.vehicle.po.executor.cmd;

import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeFreezeReq;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeFreezeResp;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.HongqiThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.acl.http.hongqi.request.CbuApprovalResultReq;
import com.leapmotor.export.vehicle.po.enums.PurchaseOrderStatusEnum;
import com.leapmotor.export.vehicle.po.gateway.PlaceholderCustomerDataGateway;
import com.leapmotor.export.vehicle.po.gateway.PlaceholderMaterialDataGateway;
import com.leapmotor.export.vehicle.po.gateway.PlaceholderPriceDataGateway;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderDetailGateway;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGateway;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGuaranteeGateway;
import com.leapmotor.export.vehicle.po.model.PlaceholderCustomerDataDo;
import com.leapmotor.export.vehicle.po.model.PlaceholderMaterialDataDo;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderDetailQo;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderRejectReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderSubmitReq;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import com.leapmotor.platform.ais.common.redis.annotation.DistributeLock;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PO 订单审批流转执行器（4.3 提交/驳回、4.4 供需审核、4.5 产销平衡审核）。
 * 与 PurchaseOrderCmdExe（CRUD/创建/编辑）分离。
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class PurchaseOrderApprovalCmdExe {

    private PurchaseOrderGateway purchaseOrderGateway;
    private PurchaseOrderDetailGateway purchaseOrderDetailGateway;
    private PurchaseOrderGuaranteeGateway purchaseOrderGuaranteeGateway;
    /** 【占位】客户主数据 */
    private PlaceholderCustomerDataGateway customerDataGateway;
    /** 【占位】整编主数据 */
    private PlaceholderMaterialDataGateway materialDataGateway;
    /** 【占位】价格主数据 */
    private PlaceholderPriceDataGateway priceDataGateway;
    /** 保函服务（同项目同微服务，本地事务强一致） */
    private ExportVehicleLetterGuaranteeService letterGuaranteeService;
    /** 事件发布（推红旗，T03 事件机制） */
    private EventHandlerHelper eventHandlerHelper;

    // ==================== 4.3 销售提交（4 步编排，任一步失败整体回滚） ====================

    @DistributeLock(nameSpace = "export-vehicle", prefix = "po:submit", value = "#req.poId",
            tryLockTimeout = 3000, expireAfter = 60000, failedCode = 600, failedMsg = "订单正在提交，请稍后重试")
    @Transactional(rollbackFor = Exception.class)
    public void submitPurchaseOrder(PurchaseOrderSubmitReq req) {
        // 查订单 + 草稿态校验
        PurchaseOrderDo order = purchaseOrderGateway.getPurchaseOrderById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!PurchaseOrderStatusEnum.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅草稿态订单可提交");
        }

        // ===== 步骤1 检查订单数据 + 兜底匹配 =====
        if (StringUtils.isBlank(order.getSalesCompany())) {
            throw new BizException("销售公司不能为空");
        }
        PlaceholderCustomerDataDo customer = customerDataGateway.getByExternalCode(order.getExternalCustomerCode());
        if (StringUtils.isBlank(order.getCustomerCode())) {
            if (customer == null) {
                throw new BizException("客户编码无法匹配，不允许提交");
            }
            order.setCustomerCode(customer.getCustomerCode());
            order.setCustomerName(customer.getCustomerName());
        }
        // 明细物料兜底
        PurchaseOrderDetailQo detailQo = new PurchaseOrderDetailQo();
        detailQo.setPoNumber(order.getPoNumber());
        List<PurchaseOrderDetailDo> details = purchaseOrderDetailGateway.listPurchaseOrderDetail(detailQo);
        for (PurchaseOrderDetailDo d : details) {
            if (StringUtils.isBlank(d.getMaterialCode())) {
                PlaceholderMaterialDataDo m = materialDataGateway.getBySyncMaterialCode(d.getSyncMaterialCode());
                if (m == null) {
                    throw new BizException("明细" + d.getOrderDetailNumber() + "物料编码无法匹配，不允许提交");
                }
                d.setMaterialCode(m.getMaterialCode());
                d.setMaterialDescription(m.getMaterialDescription());
                d.setSalesMarket(m.getSalesMarket());
                d.setYearModel(m.getYearModel());
                d.setOptionalPackage(m.getOptionalPackage());
            }
        }

        // ===== 步骤2 价格匹配（单价空的明细按 物料+订单类型+创建时间 取审批最新价） =====
        LocalDateTime orderCreateTime = order.getCreateTime();
        List<String> noPriceMaterials = new ArrayList<>();
        for (PurchaseOrderDetailDo d : details) {
            if (d.getUnitPrice() == null) {
                BigDecimal price = priceDataGateway.getLatestApprovedPrice(d.getMaterialCode(), order.getOrderType(), orderCreateTime);
                if (price == null) {
                    noPriceMaterials.add(d.getMaterialCode());
                    continue;
                }
                d.setUnitPrice(price);
                d.setTotalAmount(price.multiply(d.getDemandQuantity()));
            }
        }
        if (!noPriceMaterials.isEmpty()) {
            throw new BizException("提交失败，物料编码" + String.join("、", noPriceMaterials) + "无法匹配到有效价格");
        }
        // 重算总金额
        order.setTotalAmount(details.stream()
                .map(PurchaseOrderDetailDo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // ===== 步骤3 资金校验与保函冻结 =====
        String fundCheckType = customer != null ? customer.getFundCheckType() : null;
        if ("保函".equals(fundCheckType)) {
            ExportVehicleLetterGuaranteeFreezeReq freezeReq = new ExportVehicleLetterGuaranteeFreezeReq();
            freezeReq.setCustomerId(order.getCustomerCode()); // TODO customerId 用 customerCode，待保函侧确认
            freezeReq.setOrderType(order.getOrderType());
            freezeReq.setOrderId(order.getId());
            freezeReq.setOrderNo(order.getPoNumber());
            freezeReq.setPoNo(order.getPoNumber());
            freezeReq.setRequiredAmount(order.getTotalAmount());
            ExportVehicleLetterGuaranteeFreezeResp freezeResp = letterGuaranteeService.freeze(freezeReq);
            if (freezeResp == null || !Boolean.TRUE.equals(freezeResp.getSuccess())
                    || CollectionUtils.isEmpty(freezeResp.getFrozenItems())) {
                throw new BizException("提交失败，资金校验未通过，请联系商务");
            }
            // 写 PO-保函关联表（一个 PO 多保函）
            List<PurchaseOrderGuaranteeDo> guarantees = freezeResp.getFrozenItems().stream().map(item -> {
                PurchaseOrderGuaranteeDo g = new PurchaseOrderGuaranteeDo();
                g.setPoNumber(order.getPoNumber());
                g.setGuaranteeId(item.getLgId());
                g.setGuaranteeNumber(item.getLgCode());
                g.setPoCurrentOccupiedAmount(item.getFrozenAmount());
                g.setPoCurrentReceivableAmount(BigDecimal.ZERO);
                return g;
            }).toList();
            purchaseOrderGuaranteeGateway.addBatchPurchaseOrderGuarantee(guarantees);
        }
        // fundCheckType = 无/null → 跳过保函

        // ===== 提交完成：状态→销售已提交 + submit_time + 回填订单/明细 =====
        order.setOrderStatus(PurchaseOrderStatusEnum.SALES_SUBMITTED.getCode());
        order.setSubmitTime(LocalDateTime.now());
        purchaseOrderGateway.updatePurchaseOrder(order);
        for (PurchaseOrderDetailDo d : details) {
            purchaseOrderDetailGateway.updatePurchaseOrderDetail(d);
        }
    }

    // ==================== 4.3 销售驳回（终态 + 推红旗） ====================

    @DistributeLock(nameSpace = "export-vehicle", prefix = "po:reject", value = "#req.poId",
            tryLockTimeout = 3000, expireAfter = 60000, failedCode = 600, failedMsg = "订单正在处理，请稍后重试")
    @Transactional(rollbackFor = Exception.class)
    public void rejectPurchaseOrder(PurchaseOrderRejectReq req) {
        // 查订单 + 草稿态校验
        PurchaseOrderDo order = purchaseOrderGateway.getPurchaseOrderById(req.getPoId());
        if (order == null) {
            throw new BizException("PO订单不存在");
        }
        if (!PurchaseOrderStatusEnum.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BizException("仅草稿态订单可驳回");
        }

        // 状态→销售已驳回（终态）+ 驳回时间/人/说明（只 set 这些，updateById 防篡改）
        PurchaseOrderDo update = new PurchaseOrderDo();
        update.setId(req.getPoId());
        update.setOrderStatus(PurchaseOrderStatusEnum.SALES_REJECTED.getCode());
        update.setRejectTime(LocalDateTime.now());
        update.setRejectUser(String.valueOf(LoginUserUtils.getUserId()));
        update.setRejectReason(req.getRejectReason());
        purchaseOrderGateway.updatePurchaseOrder(update);

        // 推红旗审批结果（事件机制：落 event_notify_task，XXL-Job 异步重试，订单状态不因推送回滚）
        CbuApprovalResultReq pushReq = new CbuApprovalResultReq();
        pushReq.setPoNo(order.getPoNumber());
        pushReq.setResult("REJECTED");
        pushReq.setOpinion(req.getRejectReason());
        eventHandlerHelper.publishAsync(
                HongqiThirdTargetPlatformEnum.HONGQI,
                HongqiEventTypeEnum.PUSH_CBU_APPROVAL_RESULT,
                order.getId(),
                pushReq);
    }
}
