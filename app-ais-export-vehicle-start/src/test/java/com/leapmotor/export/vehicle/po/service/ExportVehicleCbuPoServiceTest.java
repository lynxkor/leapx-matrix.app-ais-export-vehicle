package com.leapmotor.export.vehicle.po.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.po.enums.ExportVehicleCbuPoStatusEnum;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRefreshPriceReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuOrderDetailReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PO 订单 Service 层集成测试（创建/编辑/提交/审核/刷新价格）。
 */
@DisplayName("PO 订单 Service 集成测试")
public class ExportVehicleCbuPoServiceTest extends BaseServiceTest<ExportVehicleCbuPoService> {

    @Resource
    private ExportVehicleCbuPoGateway purchaseOrderGateway;
    @Resource
    private ExportVehicleCbuPoDetailGateway purchaseOrderDetailGateway;

    private static final String DS775 = "B1099R1VEULWLDS775";

    // ==================== createFromHongqi ====================

    @Test
    @DisplayName("createFromHongqi - 草稿创建成功")
    public void testCreate_happy() {
        String poId = createDraft();
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(poId);
        assertNotNull(order);
        assertEquals(ExportVehicleCbuPoStatusEnum.DRAFT.getCode(), order.getOrderStatus());
    }

    @Test
    @DisplayName("createFromHongqi - 重复 poNo 抛已存在")
    public void testCreate_duplicate() {
        String poNo = uniquePoNo();
        service.createFromHongqi(buildCreateReq(poNo));
        assertThrows(BizException.class, () -> service.createFromHongqi(buildCreateReq(poNo)));
    }

    @Test
    @DisplayName("createFromHongqi - 明细编号重复抛错")
    public void testCreate_detailDup() {
        HongqiCbuPurchaseOrderReq req = buildCreateReq(uniquePoNo());
        req.getOrderDetailList().get(1).setDetailNumber(req.getOrderDetailList().get(0).getDetailNumber());
        assertThrows(BizException.class, () -> service.createFromHongqi(req));
    }

    @Test
    @DisplayName("createFromHongqi - poNo 日期段非法抛错")
    public void testCreate_badDate() {
        HongqiCbuPurchaseOrderReq req = buildCreateReq(uniquePoNo());
        req.setPoNo("HQ20261332000001");
        assertThrows(BizException.class, () -> service.createFromHongqi(req));
    }

    @Test
    @DisplayName("createFromHongqi - VIN码年份长串截取后4位落库(非原样存)")
    public void testCreate_vinYearTruncated() {
        HongqiCbuPurchaseOrderReq req = buildCreateReq(uniquePoNo());
        // 模拟真实入参 D-出口-WMP-2026，落库应只存末尾4位 2026
        req.getOrderDetailList().get(0).setVinYear("D-出口-WMP-2026");
        service.createFromHongqi(req);
        ExportVehicleCbuPoDetailQo qo = new ExportVehicleCbuPoDetailQo();
        qo.setPoNo(req.getPoNo());
        List<ExportVehicleCbuPoDetailDo> details = purchaseOrderDetailGateway.listExportVehicleCbuPoDetail(qo);
        String stored = details.stream()
                .filter(d -> "0001".equals(d.getOrderDetailNumber()))
                .findFirst()
                .map(ExportVehicleCbuPoDetailDo::getVinYear)
                .orElse(null);
        assertEquals("2026", stored);
    }

    // ==================== save ====================

    @Test
    @DisplayName("save - 草稿改销售公司+备注成功")
    public void testSave_happy() {
        String poId = createDraft();
        assertDoesNotThrow(() -> service.saveExportVehicleCbuPo(buildSaveReq(poId, "1000", "备注")));
    }

    @Test
    @DisplayName("save - 销售公司为空抛错")
    public void testSave_blankCompany() {
        String poId = createDraft();
        assertThrows(BizException.class, () -> service.saveExportVehicleCbuPo(buildSaveReq(poId, "", "x")));
    }

    @Test
    @DisplayName("save - 非草稿态抛错")
    public void testSave_notDraft() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode());
        assertThrows(BizException.class, () -> service.saveExportVehicleCbuPo(buildSaveReq(poId, "1000", "x")));
    }

    // ==================== reject ====================

    @Test
    @DisplayName("reject - 草稿驳回成功")
    public void testReject_happy() {
        String poId = createDraft();
        service.rejectExportVehicleCbuPo(rejectReq(poId, "驳回"));
        assertEquals(ExportVehicleCbuPoStatusEnum.SALES_REJECTED.getCode(),
                purchaseOrderGateway.getExportVehicleCbuPoById(poId).getOrderStatus());
    }

    @Test
    @DisplayName("reject - 非草稿态抛错")
    public void testReject_notDraft() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode());
        assertThrows(BizException.class, () -> service.rejectExportVehicleCbuPo(rejectReq(poId, "x")));
    }

    // ==================== submit ====================

    @Test
    @DisplayName("submit - 非草稿态抛错")
    public void testSubmit_notDraft() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode());
        assertThrows(BizException.class, () -> service.submitExportVehicleCbuPo(submitReq(poId)));
    }

    @Test
    @DisplayName("submit - 草稿提交成功（冻结保函）")
    public void testSubmit_happy() {
        String poId = createDraft();
        service.submitExportVehicleCbuPo(submitReq(poId));
        assertEquals(ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode(),
                purchaseOrderGateway.getExportVehicleCbuPoById(poId).getOrderStatus());
    }

    // ==================== supplyApprove ====================

    @Test
    @DisplayName("supplyApprove - 非销售已提交态抛错")
    public void testSupplyApprove_wrongStatus() {
        assertThrows(BizException.class, () -> service.supplyApprove(supplyApproveReq(createDraft())));
    }

    @Test
    @DisplayName("supplyApprove - 销售已提交转供需通过（生SO）")
    public void testSupplyApprove_happy() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode());
        service.supplyApprove(supplyApproveReq(poId));
        assertEquals(ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_APPROVED.getCode(),
                purchaseOrderGateway.getExportVehicleCbuPoById(poId).getOrderStatus());
    }

    // ==================== supplyReject ====================

    @Test
    @DisplayName("supplyReject - 非销售已提交态抛错")
    public void testSupplyReject_wrongStatus() {
        assertThrows(BizException.class, () -> service.supplyReject(supplyRejectReq(createDraft(), "x")));
    }

    @Test
    @DisplayName("supplyReject - 销售已提交转供需驳回（解冻）")
    public void testSupplyReject_happy() {
        String poId = createDraft();
        service.submitExportVehicleCbuPo(submitReq(poId));
        service.supplyReject(supplyRejectReq(poId, "供需不平衡"));
        assertEquals(ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_REJECTED.getCode(),
                purchaseOrderGateway.getExportVehicleCbuPoById(poId).getOrderStatus());
    }

    // ==================== balanceApprove ====================

    @Test
    @DisplayName("balanceApprove - 非供需通过态抛错")
    public void testBalanceApprove_wrongStatus() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode());
        assertThrows(BizException.class, () -> service.balanceApprove(balanceApproveReq(poId)));
    }

    @Test
    @DisplayName("balanceApprove - 供需通过转产销通过")
    public void testBalanceApprove_happy() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SUPPLY_DEMAND_APPROVED.getCode());
        service.balanceApprove(balanceApproveReq(poId));
        assertEquals(ExportVehicleCbuPoStatusEnum.PRODUCTION_SALES_BALANCE_APPROVED.getCode(),
                purchaseOrderGateway.getExportVehicleCbuPoById(poId).getOrderStatus());
    }

    // ==================== balanceReject ====================

    @Test
    @DisplayName("balanceReject - 非供需通过态抛错")
    public void testBalanceReject_wrongStatus() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SALES_SUBMITTED.getCode());
        assertThrows(BizException.class, () -> service.balanceReject(balanceRejectReq(poId, "x")));
    }

    @Test
    @DisplayName("balanceReject - 供需通过转产销驳回（SO作废+解冻）")
    public void testBalanceReject_happy() {
        String poId = createDraft();
        service.submitExportVehicleCbuPo(submitReq(poId));
        service.supplyApprove(supplyApproveReq(poId));
        service.balanceReject(balanceRejectReq(poId, "产销不平衡"));
        assertEquals(ExportVehicleCbuPoStatusEnum.PRODUCTION_SALES_BALANCE_REJECTED.getCode(),
                purchaseOrderGateway.getExportVehicleCbuPoById(poId).getOrderStatus());
    }

    // ==================== refreshPrice ====================

    @Test
    @DisplayName("refreshPrice - 终态禁刷抛错")
    public void testRefreshPrice_terminal() {
        String poId = setStatus(createDraft(), ExportVehicleCbuPoStatusEnum.SALES_REJECTED.getCode());
        assertThrows(BizException.class, () -> service.refreshPrice(refreshReq(poId)));
    }

    @Test
    @DisplayName("refreshPrice - 草稿刷价（分支A）")
    public void testRefreshPrice_draft() {
        assertDoesNotThrow(() -> service.refreshPrice(refreshReq(createDraft())));
    }

    @Test
    @DisplayName("refreshPrice - 已审刷价（分支B）")
    public void testRefreshPrice_branchB() {
        String poId = createDraft();
        service.submitExportVehicleCbuPo(submitReq(poId));
        assertDoesNotThrow(() -> service.refreshPrice(refreshReq(poId)));
    }

    // ==================== CRUD ====================
    @Test @DisplayName("getById")
    public void testGetById() { assertNotNull(service.getExportVehicleCbuPoById(createDraft())); }
    @Test @DisplayName("detail")
    public void testDetail() { assertNotNull(service.getExportVehicleCbuPoDetailById(createDraft())); }
    @Test @DisplayName("list")
    public void testList() { assertNotNull(service.listExportVehicleCbuPo(new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam())); }
    @Test @DisplayName("page")
    public void testPage() { assertNotNull(service.pageExportVehicleCbuPo(new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam())); }
    @Test @DisplayName("delete")
    public void testDelete() { assertTrue(service.deleteExportVehicleCbuPoByIds(Collections.singletonList(createDraft()))); }
    @Test @DisplayName("enableOrDisable")
    public void testEnableOrDisable() {
        String poId = createDraft();
        com.leapmotor.paltform.ais.common.model.base.LpBaseParam p = new com.leapmotor.paltform.ais.common.model.base.LpBaseParam();
        p.setIdSet(Collections.singleton(poId)); p.setStatus(1);
        assertDoesNotThrow(() -> service.enableOrDisableExportVehicleCbuPoByIds(p));
    }

    // ==================== batch（依赖保函+前置状态）====================
    @Test @DisplayName("batchSubmit") public void testBatchSubmit() { batch(createDraft(), null, req -> service.batchSubmit(req)); }
    @Test @DisplayName("batchReject") public void testBatchReject() { batch(createDraft(), "批量驳回", req -> service.batchReject(req)); }
    @Test @DisplayName("batchSupplyApprove") public void testBatchSupplyApprove() { batch(createDraft(), null, req -> service.batchSupplyApprove(req)); }
    @Test @DisplayName("batchSupplyReject") public void testBatchSupplyReject() { batch(createDraft(), "批量供需驳回", req -> service.batchSupplyReject(req)); }
    @Test @DisplayName("batchBalanceApprove") public void testBatchBalanceApprove() { batch(createDraft(), null, req -> service.batchBalanceApprove(req)); }
    @Test @DisplayName("batchBalanceReject") public void testBatchBalanceReject() { batch(createDraft(), "批量产销驳回", req -> service.batchBalanceReject(req)); }

    private void batch(String poId, String reason, java.util.function.Consumer<com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBatchReq> action) {
        com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBatchReq req = new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBatchReq();
        req.setPoIds(Collections.singletonList(poId));
        if (reason != null) req.setRejectReason(reason);
        assertDoesNotThrow(() -> action.accept(req));
    }

    // ==================== helper ====================

    private String createDraft() {
        return service.createFromHongqi(buildCreateReq(uniquePoNo()));
    }

    private String setStatus(String poId, String status) {
        ExportVehicleCbuPoDo upd = new ExportVehicleCbuPoDo();
        upd.setId(poId);
        upd.setOrderStatus(status);
        purchaseOrderGateway.updateExportVehicleCbuPo(upd);
        return poId;
    }

    private String uniquePoNo() {
        return "HQ" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%06d", Math.abs(System.nanoTime()) % 1000000L);
    }

    private HongqiCbuPurchaseOrderReq buildCreateReq(String poNo) {
        HongqiCbuPurchaseOrderReq req = new HongqiCbuPurchaseOrderReq();
        req.setPoNo(poNo);
        req.setCustomerCode("HQ01");
        req.setPlanVersion("20260719V1");
        HongqiCbuOrderDetailReq d1 = new HongqiCbuOrderDetailReq();
        d1.setDetailNumber("0001");
        d1.setMaterialCode(DS775);
        d1.setDemandQuantity(new BigDecimal("2"));
        d1.setVinYear("2026");
        d1.setExpectedOfflineDate("20261231");
        HongqiCbuOrderDetailReq d2 = new HongqiCbuOrderDetailReq();
        d2.setDetailNumber("0002");
        d2.setMaterialCode(DS775);
        d2.setDemandQuantity(new BigDecimal("1"));
        d2.setVinYear("2026");
        d2.setExpectedOfflineDate("20261231");
        req.setOrderDetailList(Arrays.asList(d1, d2));
        return req;
    }

    private ExportVehicleCbuPoSaveReq buildSaveReq(String poId, String company, String remark) {
        ExportVehicleCbuPoSaveReq req = new ExportVehicleCbuPoSaveReq();
        req.setPoId(poId);
        req.setSalesCompanyCode(company);
        ExportVehicleCbuPoDetailQo qo = new ExportVehicleCbuPoDetailQo();
        qo.setPoNo(purchaseOrderGateway.getExportVehicleCbuPoById(poId).getPoNo());
        List<ExportVehicleCbuPoDetailDo> details = purchaseOrderDetailGateway.listExportVehicleCbuPoDetail(qo);
        ExportVehicleCbuPoDetailSaveReq d = new ExportVehicleCbuPoDetailSaveReq();
        d.setDetailId(details.get(0).getId());
        d.setProductionRemark(remark);
        req.setDetailList(Collections.singletonList(d));
        return req;
    }

    private ExportVehicleCbuPoRejectReq rejectReq(String poId, String reason) {
        ExportVehicleCbuPoRejectReq r = new ExportVehicleCbuPoRejectReq();
        r.setPoId(poId); r.setRejectReason(reason); return r;
    }
    private ExportVehicleCbuPoSubmitReq submitReq(String poId) {
        ExportVehicleCbuPoSubmitReq r = new ExportVehicleCbuPoSubmitReq(); r.setPoId(poId); return r;
    }
    private ExportVehicleCbuPoSupplyApproveReq supplyApproveReq(String poId) {
        ExportVehicleCbuPoSupplyApproveReq r = new ExportVehicleCbuPoSupplyApproveReq(); r.setPoId(poId); return r;
    }
    private ExportVehicleCbuPoSupplyRejectReq supplyRejectReq(String poId, String reason) {
        ExportVehicleCbuPoSupplyRejectReq r = new ExportVehicleCbuPoSupplyRejectReq();
        r.setPoId(poId); r.setRejectReason(reason); return r;
    }
    private ExportVehicleCbuPoBalanceApproveReq balanceApproveReq(String poId) {
        ExportVehicleCbuPoBalanceApproveReq r = new ExportVehicleCbuPoBalanceApproveReq(); r.setPoId(poId); return r;
    }
    private ExportVehicleCbuPoBalanceRejectReq balanceRejectReq(String poId, String reason) {
        ExportVehicleCbuPoBalanceRejectReq r = new ExportVehicleCbuPoBalanceRejectReq();
        r.setPoId(poId); r.setRejectReason(reason); return r;
    }
    private ExportVehicleCbuPoRefreshPriceReq refreshReq(String poId) {
        ExportVehicleCbuPoRefreshPriceReq r = new ExportVehicleCbuPoRefreshPriceReq(); r.setPoId(poId); return r;
    }
}
