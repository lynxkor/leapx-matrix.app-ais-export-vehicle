package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CBU SO 外部回调 Service 集成测试：验证 APS 状态回传、红旗质检反馈、收款、SAP 补推。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@DisplayName("CBU SO 外部回调 Service 集成测试")
@ActiveProfiles({"dev", "test"})
public class VehicleCbuSoCallbackServiceTest extends BaseServiceTest<VehicleCbuSoService> {

    private static final String TEST_SALES_COMPANY = "1000";
    private static final String TEST_CUSTOMER_CODE = "CUST001";
    private static final String TEST_CUSTOMER_NAME = "测试客户";

    @Resource
    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

    @Test
    @DisplayName("receiveProductionStatus - APS 状态回传状态机正常流转")
    public void testReceiveProductionStatus_stateTransition() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();

        // 状态 1：未下发(1001) -> 排产中(1003)
        service.receiveProductionStatus(buildStatusReq(soNo, 1, null));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.SCHEDULING.getCode());

        // 状态 5：排产中(1003) -> 总装下线(1007)
        VehicleCbuSoReceiveStatusReq status5Req = buildStatusReq(soNo, 5, "VIN" + poNo);
        status5Req.setAssemblyInTime(LocalDateTime.now().minusHours(2));
        status5Req.setAssemblyOutTime(LocalDateTime.now());
        service.receiveProductionStatus(status5Req);
        VehicleCbuSoDo so = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode(), so.getOrderStatus());
        assertEquals("VIN" + poNo, so.getVin());
    }

    @Test
    @DisplayName("receiveProductionStatus - 同状态重复回传跳过全部处理")
    public void testReceiveProductionStatus_duplicateSkipsSyncAndEvent() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String soId = soItem.getSoId();

        // 向前流转到排产中：写 1 条同步记录，记录排产时间
        LocalDateTime firstScheduleTime = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        VehicleCbuSoReceiveStatusReq firstReq = buildStatusReq(soNo, 1, null);
        firstReq.setScheduleTime(firstScheduleTime);
        service.receiveProductionStatus(firstReq);
        assertEquals(1, countSyncRecords(soId), "向前流转应写 1 条同步记录");

        // 同状态重复回传（时间更晚）：跳过全部处理，同步记录不新增、节点时间不变、状态不变
        VehicleCbuSoReceiveStatusReq dupReq = buildStatusReq(soNo, 1, null);
        dupReq.setScheduleTime(firstScheduleTime.plusHours(1));
        service.receiveProductionStatus(dupReq);
        assertEquals(1, countSyncRecords(soId), "同状态重复回传不应新增同步记录");
        VehicleCbuSoDo so = getSoBySoNo(poNo, soNo);
        assertEquals(firstScheduleTime, so.getScheduleTime(), "同状态重复回传不应覆盖节点时间");
        assertEquals(VehicleCbuSoStatusEnum.SCHEDULING.getCode(), so.getOrderStatus());
    }

    @Test
    @DisplayName("receiveProductionStatus - 状态回退跳过全部处理")
    public void testReceiveProductionStatus_rollbackSkipped() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String soId = soItem.getSoId();
        String vin = "VIN" + poNo;

        // 推到总装下线
        service.receiveProductionStatus(buildStatusReq(soNo, 5, vin));
        long countAfterForward = countSyncRecords(soId);
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode());

        // 回退到涂装上线（状态 3）：状态不变、同步记录不新增
        service.receiveProductionStatus(buildStatusReq(soNo, 3, null));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode());
        assertEquals(countAfterForward, countSyncRecords(soId), "状态回退不应新增同步记录");
    }

    @Test
    @DisplayName("receiveQcResult - 质检通过 P -> 1008；N/F 可互相覆盖且可被 P 覆盖")
    public void testReceiveQcResult_stateTransition() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        // 先推到总装下线，使 QC 合法
        service.receiveProductionStatus(buildStatusReq(soNo, 5, vin));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode());

        // N -> 1009
        service.receiveQcResult(buildQcReq(vin, "N", "初检不通过"));
        VehicleCbuSoDo soN = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.QC_FAILED.getCode(), soN.getOrderStatus());

        // F -> 1010
        service.receiveQcResult(buildQcReq(vin, "F", "未收到车辆"));
        VehicleCbuSoDo soF = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.QC_NO_VEHICLE.getCode(), soF.getOrderStatus());

        // P -> 1008，覆盖 N/F
        service.receiveQcResult(buildQcReq(vin, "P", "复检通过"));
        VehicleCbuSoDo soP = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.QC_PASSED.getCode(), soP.getOrderStatus());

        // P 不回退
        assertThrows(BizException.class, () -> service.receiveQcResult(buildQcReq(vin, "N", "不应覆盖")));
    }

    @Test
    @DisplayName("updateReceivedStatus - 质检通过后更新为已收款 1011")
    public void testUpdateReceivedStatus() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        service.receiveProductionStatus(buildStatusReq(soNo, 5, vin));
        service.receiveQcResult(buildQcReq(vin, "P", "通过"));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.QC_PASSED.getCode());

        service.updateReceivedStatus(soNo);
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.RECEIVED.getCode());

        // 幂等：再次调用不抛异常
        assertDoesNotThrow(() -> service.updateReceivedStatus(soNo));
    }

    @Test
    @DisplayName("reissueSap - 仅 1008 允许补推，其他状态抛业务异常")
    public void testReissueSap() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();

        // 非 1008 状态补推应失败
        assertThrows(BizException.class, () -> service.reissueSap(soNo));

        // 推到 1008 后补推应成功（事件异步发布，不抛异常即视为成功）
        String vin = "VIN" + poNo;
        service.receiveProductionStatus(buildStatusReq(soNo, 5, vin));
        service.receiveQcResult(buildQcReq(vin, "P", "通过"));
        assertDoesNotThrow(() -> service.reissueSap(soNo));
    }

    private SoResult.SoItem createSingleSo(String poNo) {
        SoCreateReq req = new SoCreateReq();
        req.setPoNo(poNo);
        req.setSalesCompanyCode(TEST_SALES_COMPANY);
        req.setCustomerCode(TEST_CUSTOMER_CODE);
        req.setCustomerName(TEST_CUSTOMER_NAME);
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

        List<SoResult.SoItem> items = service.createSo(req);
        assertNotNull(items);
        assertEquals(1, items.size());
        SoResult.SoItem item = items.get(0);
        assertEquals(VehicleCbuSoStatusEnum.NOT_ISSUED.getCode(), item.getOrderStatus());
        return item;
    }

    private VehicleCbuSoReceiveStatusReq buildStatusReq(String soNo, int status, String vin) {
        VehicleCbuSoReceiveStatusReq req = new VehicleCbuSoReceiveStatusReq();
        req.setSoNo(soNo);
        req.setStatus(status);
        req.setVin(vin);
        // 状态 1 回传计划总装下线日期
        if (status == 1) {
            req.setPlanOfflineDate(LocalDate.now().plusMonths(1));
        }
        req.setScheduleTime(LocalDateTime.now());
        req.setBarcodePrintTime(LocalDateTime.now());
        req.setBodyInTime(LocalDateTime.now());
        req.setPaintInTime(LocalDateTime.now());
        req.setAssemblyInTime(LocalDateTime.now());
        req.setAssemblyOutTime(LocalDateTime.now());
        return req;
    }

    private VehicleCbuSoReceiveQcReq buildQcReq(String vin, String result, String remark) {
        VehicleCbuSoReceiveQcReq req = new VehicleCbuSoReceiveQcReq();
        req.setVin(vin);
        req.setResult(result);
        req.setRemark(remark);
        req.setQcTime(LocalDateTime.now());
        return req;
    }

    private VehicleCbuSoDo getSoBySoNo(String poNo, String soNo) {
        List<VehicleCbuSoDo> list = service.listVehicleCbuSoByPoNo(poNo);
        return list.stream()
                .filter(so -> soNo.equals(so.getSoNo()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到 SO: " + soNo));
    }

    private void assertOrderStatus(String poNo, String soNo, Integer expectedStatus) {
        VehicleCbuSoDo so = getSoBySoNo(poNo, soNo);
        assertEquals(expectedStatus, so.getOrderStatus());
    }

    private long countSyncRecords(String soId) {
        VehicleCbuSoInterfaceSyncQo qo = new VehicleCbuSoInterfaceSyncQo();
        qo.setSoId(soId);
        List<VehicleCbuSoInterfaceSyncDo> list = vehicleCbuSoInterfaceSyncGateway.listVehicleCbuSoInterfaceSync(qo);
        return list == null ? 0 : list.size();
    }
}
