package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.vehicle.acl.rpc.aps.gateway.ApsGateway;
import com.leapmotor.export.vehicle.acl.rpc.aps.resp.ApsResult;
import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoIssueReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailAggregateResp;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * CBU SO 列表/分页/详情查询 集成测试
 *
 * <p>覆盖 so-query-detail：
 * <ul>
 *   <li>列表/分页查询返回车辆字段（VIN/产销识别码/内部车辆号/物料编码/物料描述）；</li>
 *   <li>下发后明细同步快照由网关自动刷新，列表/分页/详情查询返回最新同步快照；</li>
 *   <li>{@code /detail} 聚合返回 orderInfo + detailInfo。</li>
 * </ul>
 *
 * <p>依赖：Nacos 配置可加载 + Redis（{@code @DistributeLock}）；APS 网关以 {@link MockBean} 桩成功。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@DisplayName("CBU SO 列表/分页/详情查询 集成测试")
@ActiveProfiles("dev")
public class VehicleCbuSoQueryServiceTest extends BaseServiceTest<VehicleCbuSoService> {

    @MockBean
    private ApsGateway apsGateway;

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
    }

    @Test
    @DisplayName("list - 返回车辆字段（VIN/产销识别码/内部车辆号/物料编码/物料描述）")
    public void testList_returnsVehicleFields() {
        String poNo = "PO-Q-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);
        String vin = "VIN" + poNo;
        populateDetailVehicle(item.getSoId(), vin);

        VehicleCbuSoParam param = new VehicleCbuSoParam();
        param.setPoNo(poNo);
        List<VehicleCbuSoResp> rows = service.listVehicleCbuSo(param);
        assertNotNull(rows);
        assertEquals(1, rows.size());
        VehicleCbuSoResp r = rows.get(0);
        assertEquals(poNo, r.getPoNo());
        assertEquals(vin, r.getVin(), "应返回明细 VIN");
        assertEquals("PIC" + vin, r.getProductionIdentifyCode());
        assertEquals("IVN" + vin, r.getInternalVehicleNo());
        assertEquals("MAT001", r.getMaterialCode());
        assertEquals("测试物料1", r.getMaterialDescription());
    }

    @Test
    @DisplayName("page - 按 VIN 模糊查询返回分页结果")
    public void testPage_filterByVin() {
        String poNo = "PO-Q-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);
        String vin = "VIN" + poNo;
        populateDetailVehicle(item.getSoId(), vin);

        VehicleCbuSoParam param = new VehicleCbuSoParam();
        param.setVinLike(vin);
        PageResult<VehicleCbuSoResp> page = service.pageVehicleCbuSo(param);
        assertNotNull(page);
        assertTrue(page.getTotal() >= 1, "按 VIN 模糊查询应命中");
        assertTrue(page.getRecords().stream().anyMatch(r -> vin.equals(r.getVin())), "分页结果应包含该 VIN");
    }

    @Test
    @DisplayName("page - 下发后按 sync_aps_result=1 筛选返回刷新后的同步快照")
    public void testPage_filterBySyncApsResultAfterIssue() {
        String poNo = "PO-Q-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);
        issue(item.getSoId());
        awaitSyncCount(item.getSoId(), 1, 1);

        VehicleCbuSoParam param = new VehicleCbuSoParam();
        param.setSyncApsResult(1);
        PageResult<VehicleCbuSoResp> page = service.pageVehicleCbuSo(param);
        assertNotNull(page);
        boolean hit = page.getRecords().stream()
                .anyMatch(r -> item.getSoNo().equals(r.getSoNo())
                        && Integer.valueOf(1).equals(r.getSyncApsResult()));
        assertTrue(hit, "下发后明细同步快照应被刷新为 1，并可通过 syncApsResult 筛选返回");
    }

    @Test
    @DisplayName("detail - 聚合返回 orderInfo + detailInfo，detailInfo 含刷新后的同步快照")
    public void testDetail_aggregateWithSyncSnapshot() {
        String poNo = "PO-Q-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem item = createSingleSo(poNo);
        String vin = "VIN" + poNo;
        populateDetailVehicle(item.getSoId(), vin);
        issue(item.getSoId());
        awaitSyncCount(item.getSoId(), 1, 1);

        VehicleCbuSoDetailAggregateResp agg = service.detail(item.getSoId());
        assertNotNull(agg, "聚合详情不应为空");
        assertNotNull(agg.getOrderInfo(), "orderInfo 不应为空");
        assertNotNull(agg.getDetailInfo(), "detailInfo 不应为空");
        assertEquals(vin, agg.getOrderInfo().getVin());
        assertEquals("MAT001", agg.getDetailInfo().getMaterialCode());
        assertEquals(vin, agg.getDetailInfo().getVin());
        assertEquals(1, agg.getDetailInfo().getSyncApsResult(),
                "detailInfo 应返回刷新后的同步快照 sync_aps_result=1");
    }

    // ==================== 辅助 ====================

    private SoResult.SoItem createSingleSo(String poNo) {
        SoCreateReq req = new SoCreateReq();
        req.setPoNo(poNo);
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

    /**
     * 直接写明细车辆字段（VIN/产销识别码/内部车辆号），供查询返回校验。
     */
    private void populateDetailVehicle(String soId, String vin) {
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(soId);
        assertNotNull(detail, "车辆明细不存在");
        detail.setVin(vin);
        detail.setInternalVehicleNo("IVN" + vin);
        detail.setProductionIdentifyCode("PIC" + vin);
        vehicleCbuSoDetailGateway.updateVehicleCbuSoDetail(detail);
    }

    /**
     * 下发生产（APS 桩成功），触发接口同步记录写入与明细同步快照刷新。
     */
    private VehicleCbuSoOperateResult issue(String soId) {
        VehicleCbuSoIssueReq req = new VehicleCbuSoIssueReq();
        req.setId(soId);
        return service.issueToProduction(req);
    }

    private long countSyncByType(String soId, int interfaceType) {
        VehicleCbuSoInterfaceSyncQo qo = new VehicleCbuSoInterfaceSyncQo();
        qo.setSoId(soId);
        qo.setInterfaceType(interfaceType);
        List<VehicleCbuSoInterfaceSyncDo> list = vehicleCbuSoInterfaceSyncGateway.listVehicleCbuSoInterfaceSync(qo);
        return list == null ? 0 : list.size();
    }

    /**
     * 轮询等待异步同步记录写入与明细快照刷新。
     */
    private long awaitSyncCount(String soId, int interfaceType, int expected) {
        for (int i = 0; i < 60; i++) {
            if (countSyncByType(soId, interfaceType) >= expected) {
                return countSyncByType(soId, interfaceType);
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
