package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.nexus.company.api.CompanyApi;
import com.leapmotor.export.nexus.company.response.CompanyResponse;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.export.vehicle.acl.rpc.aps.gateway.ApsGateway;
import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsCancelReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.req.ApsIssueReq;
import com.leapmotor.export.vehicle.acl.rpc.aps.resp.ApsResult;
import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoCancelReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoIssueReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CBU SO 下发生产 / 作废 集成测试
 *
 * <p>覆盖 so-issue-cancel：
 * <ul>
 *   <li>单条下发：1001 -> 1002，写类型1同步记录；</li>
 *   <li>批量下发部分失败：成功项不回滚，失败项汇总；</li>
 *   <li>单条作废（1001 未下发）：1001 -> 1012，写类型2同步记录，不调 APS；</li>
 *   <li>重复作废幂等：已作废不再写同步记录、不调 APS；</li>
 *   <li>排产中作废（1003）：1012，写类型2同步记录，调用 APS 作废。</li>
 * </ul>
 *
 * <p>依赖：Nacos 配置可加载 + Redis（{@code @DistributeLock}）；APS 网关以 {@link MockBean} 桩成功。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@DisplayName("CBU SO 下发/作废 集成测试")
@ActiveProfiles("dev")
public class VehicleCbuSoIssueCancelServiceTest extends BaseServiceTest<VehicleCbuSoService> {

    @MockBean
    private ApsGateway apsGateway;

    @MockBean
    private CompanyApi companyApi;

    @Resource
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    @Resource
    private VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;

    @Resource
    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

    @BeforeEach
    public void stubApsSuccess() {
        ApsResult ok = new ApsResult();
        ok.setCode(200);
        ok.setMessage("ok");
        when(apsGateway.issueSync(any())).thenReturn(ok);
        when(apsGateway.cancelSync(any())).thenReturn(ok);
        // 销售公司主数据 stub（buildApsIssueReq 查 salesCompanyName）
        CompanyResponse company = new CompanyResponse();
        company.setCompanyCode("1000");
        company.setCompanyName("测试销售公司");
        AjaxResult<List<CompanyResponse>> companyAjax = AjaxResult.success(Arrays.asList(company));
        when(companyApi.list(any())).thenReturn(companyAjax);
    }

    @Test
    @DisplayName("单条下发：1001 -> 1002，写类型1同步记录")
    public void testIssue_single_success() {
        String poNo = "PO-I-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);

        VehicleCbuSoIssueReq req = new VehicleCbuSoIssueReq();
        req.setId(item.getSoId());
        VehicleCbuSoOperateResult result = service.issueToProduction(req);

        assertEquals(1, result.getSuccessCount(), "单条下发应成功");
        assertTrue(result.getFailItems().isEmpty());
        assertEquals(1002, getOrderStatus(poNo, item.getSoNo()), "状态应流转为已下发生产");
        assertEquals(1, awaitSyncCount(item.getSoId(), 1, 1), "应写 1 条类型1(下发排产同步APS)同步记录");
        assertEquals(1, getDetail(item.getSoId()).getSyncApsResult(),
                "明细同步快照 sync_aps_result 应被刷新为 1");
        verify(apsGateway, times(1)).issueSync(any());
    }

    @Test
    @DisplayName("批量下发部分失败：成功项不回滚，失败项汇总")
    public void testIssue_batch_partialFailure() {
        String poNo1 = "PO-I-" + UUID.randomUUID().toString().substring(0, 8);
        String poNo2 = "PO-I-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem issued = createSingleSo(poNo1);
        SoResult.SoItem notIssued = createSingleSo(poNo2);

        // 先把 issued 下发到 1002，使其在批量下发时失败
        VehicleCbuSoIssueReq single = new VehicleCbuSoIssueReq();
        single.setId(issued.getSoId());
        service.issueToProduction(single);
        awaitSyncCount(issued.getSoId(), 1, 1);

        // 批量下发：issued(1002) 失败、notIssued(1001) 成功
        VehicleCbuSoIssueReq batch = new VehicleCbuSoIssueReq();
        batch.setIds(Arrays.asList(issued.getSoId(), notIssued.getSoId()));
        VehicleCbuSoOperateResult result = service.issueToProduction(batch);

        assertEquals(1, result.getSuccessCount(), "仅未下发的 1 条应成功");
        assertEquals(1, result.getSkipCount(), "已下发的 1 条应记跳过");
        assertEquals(0, result.getFailCount(), "无失败");
        assertEquals(issued.getSoNo(), result.getSkipItems().get(0).getSoNo());
        assertEquals(1002, getOrderStatus(poNo1, issued.getSoNo()), "已下发的不应回滚");
        assertEquals(1002, getOrderStatus(poNo2, notIssued.getSoNo()), "未下发的应成功流转");
    }

    @Test
    @DisplayName("单条作废（1001 未下发）：1012，写类型2同步记录，不调 APS")
    public void testCancel_notIssued_success() {
        String poNo = "PO-C-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);

        VehicleCbuSoCancelReq req = new VehicleCbuSoCancelReq();
        req.setId(item.getSoId());
        req.setReason("测试作废");
        VehicleCbuSoOperateResult result = service.cancel(req);

        assertEquals(1, result.getSuccessCount());
        assertTrue(result.getFailItems().isEmpty());
        assertEquals(1012, getOrderStatus(poNo, item.getSoNo()), "状态应流转为已作废");
        assertEquals(1, awaitSyncCount(item.getSoId(), 2, 1), "应写 1 条类型2(作废同步APS)同步记录");
        verify(apsGateway, never()).cancelSync(any());
    }

    @Test
    @DisplayName("重复作废幂等：已作废不再写同步记录、不调 APS")
    public void testCancel_idempotent() {
        String poNo = "PO-C-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);

        VehicleCbuSoCancelReq req = new VehicleCbuSoCancelReq();
        req.setId(item.getSoId());
        req.setReason("测试作废");
        service.cancel(req);
        long countAfterFirst = awaitSyncCount(item.getSoId(), 2, 1);

        // 再次作废：幂等跳过
        service.cancel(req);
        sleep(300);
        assertEquals(countAfterFirst, countSyncByType(item.getSoId(), 2), "重复作废不应新增同步记录");
        assertEquals(1012, getOrderStatus(poNo, item.getSoNo()));
    }

    @Test
    @DisplayName("排产中作废（1003）：1012，写类型2同步记录，调用 APS 作废")
    public void testCancel_scheduling_callsAps() {
        String poNo = "PO-C-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);
        // 直接置为排产中（1003），跳过下发/回调链路，聚焦作废逻辑
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoById(item.getSoId());
        so.setOrderStatus(1003);
        vehicleCbuSoGateway.updateVehicleCbuSo(so);

        reset(apsGateway);
        ApsResult ok = new ApsResult();
        ok.setCode(200);
        ok.setMessage("ok");
        when(apsGateway.cancelSync(any())).thenReturn(ok);

        VehicleCbuSoCancelReq req = new VehicleCbuSoCancelReq();
        req.setId(item.getSoId());
        req.setReason("排产中作废");
        VehicleCbuSoOperateResult result = service.cancel(req);

        assertEquals(1, result.getSuccessCount());
        assertEquals(1012, getOrderStatus(poNo, item.getSoNo()));
        assertEquals(1, awaitSyncCount(item.getSoId(), 2, 1), "应写 1 条类型2同步记录");
        verify(apsGateway, times(1)).cancelSync(any());
    }

    @Test
    @DisplayName("下发：ApsIssueReq 字段对齐真实契约（含 issueOn 取 SO.receiveOrderTime）")
    public void testIssue_apsReqFields() {
        String poNo = "PO-F-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime receiveOrderTime = LocalDateTime.of(2026, 7, 1, 9, 30, 0);
        SoCreateReq createReq = new SoCreateReq();
        createReq.setPoNo(poNo);
        createReq.setPoId(UUID.randomUUID().toString());
        createReq.setSalesCompanyCode("1000");
        createReq.setCustomerCode("CUST001");
        createReq.setCustomerName("测试客户");
        createReq.setOrderType(1);
        createReq.setPlanVersion("V1");
        createReq.setReceiveOrderTime(receiveOrderTime);

        List<SoCreateReq.SoDetailReq> details = new ArrayList<>();
        SoCreateReq.SoDetailReq d1 = new SoCreateReq.SoDetailReq();
        d1.setPoDetailNo("D001");
        d1.setDemandQuantity(new BigDecimal("1"));
        d1.setUnitPrice(new BigDecimal("150000.00"));
        d1.setVinYear("2026");
        d1.setExpectedOfflineDate(LocalDate.of(2026, 9, 1));
        d1.setMaterialCode("MAT001");
        d1.setMaterialDescription("测试物料1");
        details.add(d1);
        createReq.setDetails(details);

        SoResult.SoItem item = service.createSo(createReq).getData().get(0);

        VehicleCbuSoIssueReq req = new VehicleCbuSoIssueReq();
        req.setId(item.getSoId());
        service.issueToProduction(req);

        ArgumentCaptor<ApsIssueReq> captor = ArgumentCaptor.forClass(ApsIssueReq.class);
        verify(apsGateway, times(1)).issueSync(captor.capture());
        ApsIssueReq apsReq = captor.getValue();
        assertEquals(poNo, apsReq.getVehPoName());
        assertEquals(item.getSoNo(), apsReq.getVehCbuSoName());
        assertEquals(item.getSoId(), apsReq.getVehCbuSoId());
        assertEquals("1000", apsReq.getSalesCompanyCode());
        assertEquals("测试销售公司", apsReq.getSalesCompanyName(),
                "salesCompanyName 从主数据查");
        assertEquals("CUST001", apsReq.getCustomerCode());
        assertEquals("测试客户", apsReq.getCustomerName());
        assertEquals("MAT001", apsReq.getVehicleMaterialName());
        assertEquals("测试物料1", apsReq.getVehicleMaterialDesc());
        assertEquals(10, apsReq.getOrderType(), "orderType 固定 10");
        assertEquals(100, apsReq.getOrderCategory(), "orderCategory 固定 100");
        assertEquals(200, apsReq.getVehOrderSource(), "vehOrderSource 固定 200");
        assertEquals(1, apsReq.getQuantity(), "quantity 固定 1");
        assertEquals("2026", apsReq.getVinYear());
        assertEquals("2026-07-01 09:30:00", apsReq.getIssueOn(),
                "issueOn 取 SO.receiveOrderTime 格式化 yyyy-MM-dd HH:mm:ss");
        assertNotNull(apsReq.getExpectProducedOn(), "expectProducedOn 取 expectedOfflineDate");
    }

    @Test
    @DisplayName("作废：ApsCancelReq 字段名 vehPoName/vehCbuSoName（SO 维度）")
    public void testCancel_apsReqFields_soDim() {
        String poNo = "PO-F-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);
        // 置为排产中(1003)以触发 APS 作废同步
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoById(item.getSoId());
        so.setOrderStatus(1003);
        vehicleCbuSoGateway.updateVehicleCbuSo(so);

        VehicleCbuSoCancelReq req = new VehicleCbuSoCancelReq();
        req.setId(item.getSoId());
        req.setReason("测试");
        service.cancel(req);

        ArgumentCaptor<ApsCancelReq> captor = ArgumentCaptor.forClass(ApsCancelReq.class);
        verify(apsGateway, times(1)).cancelSync(captor.capture());
        ApsCancelReq apsReq = captor.getValue();
        assertEquals(poNo, apsReq.getVehPoName(), "SO 维度作废应传 vehPoName");
        assertEquals(item.getSoNo(), apsReq.getVehCbuSoName(), "SO 维度作废应传 vehCbuSoName");
        assertEquals(-1, apsReq.getChangeType());
    }

    @Test
    @DisplayName("作废：ApsCancelReq 字段名 vehPoName（PO 维度，vehCbuSoName 空）")
    public void testCancel_apsReqFields_poDim() {
        String poNo = "PO-F-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoById(item.getSoId());
        so.setOrderStatus(1003);
        vehicleCbuSoGateway.updateVehicleCbuSo(so);

        VehicleCbuSoCancelReq req = new VehicleCbuSoCancelReq();
        req.setPoNo(poNo);
        req.setReason("测试");
        service.cancel(req);

        ArgumentCaptor<ApsCancelReq> captor = ArgumentCaptor.forClass(ApsCancelReq.class);
        verify(apsGateway, atLeast(1)).cancelSync(captor.capture());
        ApsCancelReq apsReq = captor.getValue();
        assertEquals(poNo, apsReq.getVehPoName());
        assertNull(apsReq.getVehCbuSoName(), "PO 维度作废 vehCbuSoName 应为空");
        assertEquals(-1, apsReq.getChangeType());
    }

    // ==================== 辅助 ====================

    private SoResult.SoItem createSingleSo(String poNo) {
        SoCreateReq req = new SoCreateReq();
        req.setPoNo(poNo);
        req.setPoId(UUID.randomUUID().toString());
        req.setSalesCompanyCode("1000");
        req.setCustomerCode("CUST001");
        req.setCustomerName("测试客户");
        req.setOrderType(1);
        req.setPlanVersion("V1");

        List<SoCreateReq.SoDetailReq> details = new ArrayList<>();
        SoCreateReq.SoDetailReq d1 = new SoCreateReq.SoDetailReq();
        d1.setPoDetailNo("D001");
        d1.setDemandQuantity(new BigDecimal("1"));
        d1.setUnitPrice(new BigDecimal("150000.00"));
        d1.setVinYear("2026");
        d1.setExpectedOfflineDate(LocalDate.now().plusMonths(1));
        d1.setMaterialCode("MAT001");
        d1.setMaterialDescription("测试物料1");
        details.add(d1);
        req.setDetails(details);

        SoResult result = service.createSo(req);
        assertNotNull(result);
        List<SoResult.SoItem> items = result.getData();
        assertNotNull(items);
        assertEquals(1, items.size());
        return items.get(0);
    }

    private Integer getOrderStatus(String poNo, String soNo) {
        return service.listVehicleCbuSoByPoNo(poNo).stream()
                .filter(s -> soNo.equals(s.getSoNo()))
                .findFirst()
                .map(VehicleCbuSoDo::getOrderStatus)
                .orElseThrow(() -> new AssertionError("未找到 SO: " + soNo));
    }

    private VehicleCbuSoDetailDo getDetail(String soId) {
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(soId);
        assertNotNull(detail, "车辆明细不应为空: " + soId);
        return detail;
    }

    private long countSyncByType(String soId, int interfaceType) {
        VehicleCbuSoInterfaceSyncQo qo = new VehicleCbuSoInterfaceSyncQo();
        qo.setSoId(soId);
        qo.setInterfaceType(interfaceType);
        List<VehicleCbuSoInterfaceSyncDo> list = vehicleCbuSoInterfaceSyncGateway.listVehicleCbuSoInterfaceSync(qo);
        return list == null ? 0 : list.size();
    }

    /**
     * 轮询等待异步同步记录写入（下发/作废同步记录为异步写入）。
     */
    private long awaitSyncCount(String soId, int interfaceType, int expected) {
        for (int i = 0; i < 60; i++) {
            long c = countSyncByType(soId, interfaceType);
            if (c >= expected) {
                return c;
            }
            sleep(50);
        }
        return countSyncByType(soId, interfaceType);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
