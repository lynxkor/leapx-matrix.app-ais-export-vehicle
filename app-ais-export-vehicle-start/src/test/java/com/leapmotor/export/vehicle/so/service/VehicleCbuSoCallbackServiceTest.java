package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.nexus.company.api.CompanyApi;
import com.leapmotor.export.vehicle.acl.rpc.aps.gateway.ApsGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiProductionNodeResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.SapResult;
import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAddOrUpdateReq;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeFreezeResp;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeApproveReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeFreezeReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeSubmitReq;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReissueSapReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * CBU SO 外部回调 Service 集成测试：验证 APS 状态回传、红旗质检反馈、收款、SAP 补推。
 *
 * @author lijianuo
 * @since 2026-07-16
 */
@DisplayName("CBU SO 外部回调 Service 集成测试")
@ActiveProfiles("dev")
public class VehicleCbuSoCallbackServiceTest extends BaseServiceTest<VehicleCbuSoService> {

    private static final String TEST_SALES_COMPANY = "1000";
    private static final String TEST_CUSTOMER_CODE = "CUST001";
    private static final String TEST_CUSTOMER_NAME = "测试客户";

    @MockBean
    private ApsGateway apsGateway;

    @MockBean
    private CompanyApi companyApi;

    @MockBean
    private HongqiGateway hongqiGateway;

    @MockBean
    private SapGateway sapGateway;

    @Resource
    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

    @Resource
    private VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;

    @Resource
    private ExportVehicleLetterGuaranteeService letterGuaranteeService;

    @Resource
    private ExportVehicleLetterGuaranteeGateway letterGuaranteeGateway;

    @Resource
    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;

    @Test
    @DisplayName("receiveProductionStatus - APS 状态回传状态机正常流转")
    public void testReceiveProductionStatus_stateTransition() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String soId = soItem.getSoId();

        // 状态 1：未下发(1001) -> 排产中(1003)，回传计划总装下线日期
        LocalDate planOfflineDate = LocalDate.now().plusMonths(2);
        VehicleCbuSoReceiveStatusReq status1Req = buildStatusReq(soNo, 1, null);
        status1Req.setPlanOfflineDate(planOfflineDate);
        service.receiveProductionStatus(List.of(status1Req));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.SCHEDULING.getCode());

        // planOfflineDate 应写入车辆明细，且主表 expectedOfflineDate 保持 PO 创建时的值
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(soId);
        assertNotNull(detail);
        assertEquals(planOfflineDate, detail.getPlanOfflineDate(),
                "APS 回传的计划总装下线日期应写入车辆明细");
        VehicleCbuSoDo soAfterStatus1 = getSoBySoNo(poNo, soNo);
        assertEquals(LocalDate.now().plusMonths(1), soAfterStatus1.getExpectedOfflineDate(),
                "主表 expectedOfflineDate 不应被 APS 覆盖");

        // 状态 5：排产中(1003) -> 总装下线(1007)
        VehicleCbuSoReceiveStatusReq status5Req = buildStatusReq(soNo, 5, "VIN" + poNo);
        status5Req.setAssemblyInTime(LocalDateTime.now().minusHours(2));
        status5Req.setAssemblyOutTime(LocalDateTime.now());
        service.receiveProductionStatus(List.of(status5Req));
        VehicleCbuSoDo so = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode(), so.getOrderStatus());
        assertEquals("VIN" + poNo, vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(soId).getVin());

        // 后续状态不回传 planOfflineDate 时，明细值保持状态 1 回传的值
        VehicleCbuSoDetailDo detailAfterStatus5 = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(soId);
        assertNotNull(detailAfterStatus5);
        assertEquals(planOfflineDate, detailAfterStatus5.getPlanOfflineDate(),
                "后续状态未回传 planOfflineDate 时，明细值应保持状态 1 回传值");
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
        // mock HongqiGateway 返回成功（红旗测试环境不可达，mock 使 handler 写类型3 记录）
        HongqiResult<List<HongqiProductionNodeResultData>> mockResult = new HongqiResult<>();
        mockResult.setSuccess(true);
        HongqiProductionNodeResultData mockData = new HongqiProductionNodeResultData();
        mockData.setIsSign("Y");
        mockData.setSoNo(soNo);
        mockResult.setData(List.of(mockData));
        when(hongqiGateway.pushProductionNodeSync(any())).thenReturn(mockResult);
        service.receiveProductionStatus(List.of(firstReq));
        assertEquals(1, awaitSyncCount(soId, 3, 1, 5000),
                "向前流转应写 1 条类型3 同步记录");

        // 同状态重复回传（时间更晚）：跳过全部处理，同步记录不新增、节点时间不变、状态不变
        VehicleCbuSoReceiveStatusReq dupReq = buildStatusReq(soNo, 1, null);
        dupReq.setScheduleTime(firstScheduleTime.plusHours(1));
        service.receiveProductionStatus(List.of(dupReq));
        assertEquals(1, countSyncRecords(soId), "同状态重复回传不应新增同步记录");
        VehicleCbuSoDo so = getSoBySoNo(poNo, soNo);
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(soId);
        assertEquals(firstScheduleTime, detail.getScheduleTime(), "同状态重复回传不应覆盖节点时间");
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
        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 5, vin)));
        long countAfterForward = countSyncRecords(soId);
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode());

        // 回退到涂装上线（状态 3）：状态不变、同步记录不新增
        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 3, null)));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode());
        assertEquals(countAfterForward, countSyncRecords(soId), "状态回退不应新增同步记录");
    }

    @Test
    @DisplayName("receiveProductionStatus - 批量回传：单条失败不中断，超 100 拒")
    public void testReceiveProductionStatus_batch() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();

        // 批量 2 条：1 条正常(状态1->1003)，1 条无效状态码(失败)
        VehicleCbuSoReceiveStatusReq okReq = buildStatusReq(soNo, 1, null);
        VehicleCbuSoReceiveStatusReq badReq = buildStatusReq(soNo, 99, null);
        VehicleCbuSoOperateResult result = service.receiveProductionStatus(List.of(okReq, badReq));

        assertEquals(2, result.getTotalCount(), "总数应为 2");
        assertEquals(1, result.getSuccessCount(), "正常 1 条成功");
        assertEquals(1, result.getFailCount(), "无效状态码 1 条失败");
        assertEquals(1, result.getFailItems().size());
        assertEquals(soNo, result.getFailItems().get(0).getSoNo());
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.SCHEDULING.getCode());

        // 超 100 条被拒
        List<VehicleCbuSoReceiveStatusReq> overLimit = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            overLimit.add(buildStatusReq(soNo, 1, null));
        }
        assertThrows(BizException.class, () -> service.receiveProductionStatus(overLimit),
                "超过批量上限应抛 BizException");
    }

    @Test
    @DisplayName("createSo - 落库 receive_order_time = PO 创建时间")
    public void testCreateSo_receiveOrderTime() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime receiveOrderTime = LocalDateTime.of(2026, 7, 1, 9, 30, 0);

        SoCreateReq req = new SoCreateReq();
        req.setPoNo(poNo);
        req.setPoId(UUID.randomUUID().toString());
        req.setSalesCompanyCode(TEST_SALES_COMPANY);
        req.setCustomerCode(TEST_CUSTOMER_CODE);
        req.setCustomerName(TEST_CUSTOMER_NAME);
        req.setOrderType(1);
        req.setPlanVersion("V1");
        req.setReceiveOrderTime(receiveOrderTime);

        List<SoCreateReq.SoDetailReq> details = new ArrayList<>();
        SoCreateReq.SoDetailReq d1 = new SoCreateReq.SoDetailReq();
        d1.setPoDetailNo("D001");
        d1.setDemandQuantity(new BigDecimal("1"));
        d1.setUnitPrice(new BigDecimal("150000.00"));
        d1.setMaterialCode("MAT001");
        d1.setMaterialDescription("测试物料1");
        details.add(d1);
        req.setDetails(details);

        SoResult result = service.createSo(req);
        assertNotNull(result);
        SoResult.SoItem item = result.getData().get(0);

        VehicleCbuSoDo so = getSoBySoNo(poNo, item.getSoNo());
        assertEquals(receiveOrderTime, so.getReceiveOrderTime(),
                "SO 落库 receiveOrderTime 应等于传入的 PO 创建时间");
    }

    @Test
    @DisplayName("receiveQcResult - 质检通过 10 -> 1008；20/30 可互相覆盖且可被 10 覆盖")
    public void testReceiveQcResult_stateTransition() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        // 先推到总装下线，使 QC 合法
        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 5, vin)));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode());

        // N -> 1009
        service.receiveQcResult(buildQcReq(vin, 20, "初检不通过"));
        VehicleCbuSoDo soN = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.QC_FAILED.getCode(), soN.getOrderStatus());

        // F -> 1010
        service.receiveQcResult(buildQcReq(vin, 30, "未收到车辆"));
        VehicleCbuSoDo soF = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.QC_NO_VEHICLE.getCode(), soF.getOrderStatus());

        // P->1008 需保函冻结 setup（transferToReceivable 按 poNo 查冻结明细）
        String custId = "CUST_QC_" + UUID.randomUUID().toString().substring(0, 8);
        createApprovedLg("LG_QC_" + UUID.randomUUID().toString().substring(0, 8), custId, "CBU",
                new BigDecimal("200000.00"));
        letterGuaranteeService.freeze(buildFreezeReq(custId, "CBU", new BigDecimal("150000.00"), poNo));

        // P -> 1008，覆盖 N/F
        service.receiveQcResult(buildQcReq(vin, 10, "复检通过"));
        VehicleCbuSoDo soP = getSoBySoNo(poNo, soNo);
        assertEquals(VehicleCbuSoStatusEnum.QC_PASSED.getCode(), soP.getOrderStatus());

        // P 不回退
        assertThrows(BizException.class, () -> service.receiveQcResult(buildQcReq(vin, 20, "不应覆盖")));
    }

    @Test
    @DisplayName("updateReceivedStatus - 质检通过后更新为已收款 1011")
    public void testUpdateReceivedStatus() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 5, vin)));
        // 质检通过需保函冻结 setup
        String custId = "CUST_QC_" + UUID.randomUUID().toString().substring(0, 8);
        createApprovedLg("LG_QC_" + UUID.randomUUID().toString().substring(0, 8), custId, "CBU",
                new BigDecimal("200000.00"));
        letterGuaranteeService.freeze(buildFreezeReq(custId, "CBU", new BigDecimal("150000.00"), poNo));
        service.receiveQcResult(buildQcReq(vin, 10, "通过"));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.QC_PASSED.getCode());

        service.updateReceivedStatus(soNo);
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.RECEIVED.getCode());

        // 幂等：再次调用不抛异常
        assertDoesNotThrow(() -> service.updateReceivedStatus(soNo));
    }

    @Test
    @DisplayName("7.2 质检通过触发保函转应收：SO 金额联动 + 保函占用-/应收+ + PO 关联保函表回写")
    public void testReceiveQcResult_triggersGuaranteeTransfer() {
        String poNo = "PO-QC-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        String custId = "CUST_QC_" + UUID.randomUUID().toString().substring(0, 8);
        String lgId = createApprovedLg("LG_QC_" + UUID.randomUUID().toString().substring(0, 8), custId, "CBU",
                new BigDecimal("200000.00"));
        ExportVehicleLetterGuaranteeFreezeResp freezeResp = letterGuaranteeService.freeze(
                buildFreezeReq(custId, "CBU", new BigDecimal("150000.00"), poNo));
        for (ExportVehicleLetterGuaranteeFreezeResp.FrozenItem item : freezeResp.getFrozenItems()) {
            ExportVehicleCbuPoGuaranteeDo g = new ExportVehicleCbuPoGuaranteeDo();
            g.setPoNo(poNo);
            g.setGuaranteeId(item.getLgId());
            g.setGuaranteeNumber(item.getLgCode());
            g.setPoCurrentOccupiedAmount(item.getFrozenAmount());
            g.setPoCurrentReceivableAmount(BigDecimal.ZERO);
            purchaseOrderGuaranteeGateway.addBatchExportVehicleCbuPoGuarantee(List.of(g));
        }

        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 5, vin)));
        service.receiveQcResult(buildQcReq(vin, 10, "通过"));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.QC_PASSED.getCode());

        VehicleCbuSoDo so = getSoBySoNo(poNo, soNo);
        assertEquals(0, so.getFrozenAmount().compareTo(BigDecimal.ZERO), "SO frozenAmount 归零");
        assertEquals(0, so.getReceivableAmount().compareTo(new BigDecimal("150000.00")),
                "SO receivableAmount = 冻结额");

        ExportVehicleLetterGuaranteeDo lg = letterGuaranteeGateway.getExportVehicleLetterGuaranteeById(lgId);
        assertEquals(0, lg.getOccupiedAmount().compareTo(BigDecimal.ZERO), "保函占用归零");
        assertEquals(0, lg.getReceivableAmount().compareTo(new BigDecimal("150000.00")),
                "保函应收 += 冻结额");

        ExportVehicleCbuPoGuaranteeQo gqo = new ExportVehicleCbuPoGuaranteeQo();
        gqo.setPoNo(poNo);
        List<ExportVehicleCbuPoGuaranteeDo> guarantees = purchaseOrderGuaranteeGateway
                .listExportVehicleCbuPoGuarantee(gqo);
        ExportVehicleCbuPoGuaranteeDo poG = guarantees.stream()
                .filter(g -> lgId.equals(g.getGuaranteeId())).findFirst().orElse(null);
        assertNotNull(poG, "PO 关联保函记录存在");
        assertEquals(0, poG.getPoCurrentOccupiedAmount().compareTo(BigDecimal.ZERO),
                "PO 当前占用金额归零");
        assertEquals(0, poG.getPoCurrentReceivableAmount().compareTo(new BigDecimal("150000.00")),
                "PO 当前应收金额 = 冻结额");
    }

    @Test
    @DisplayName("7.3 保函占用不足时质检通过抛异常回滚，SO 不变 1008")
    public void testReceiveQcResult_insufficientGuaranteeRollsBack() {
        String poNo = "PO-QC-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        String custId = "CUST_QC_" + UUID.randomUUID().toString().substring(0, 8);
        createApprovedLg("LG_QC_" + UUID.randomUUID().toString().substring(0, 8), custId, "CBU",
                new BigDecimal("200000.00"));
        letterGuaranteeService.freeze(buildFreezeReq(custId, "CBU", new BigDecimal("100000.00"), poNo));

        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 5, vin)));

        BizException ex = assertThrows(BizException.class,
                () -> service.receiveQcResult(buildQcReq(vin, 10, "通过")));
        assertTrue(ex.getMessage().contains("累计占用金额不足") || ex.getMessage().contains("无冻结记录"),
                "异常信息含占用不足: " + ex.getMessage());

        VehicleCbuSoDo so = getSoBySoNo(poNo, soNo);
        assertNotEquals(VehicleCbuSoStatusEnum.QC_PASSED.getCode(), so.getOrderStatus(),
                "SO 未变为质检通过");
    }

    @Test
    @DisplayName("7.5 保函转应收失败回滚时不发布 SAP 事件（无类型4 同步记录）")
    public void testGuaranteeRollbackNoSapEvent() {
        String poNo = "PO-QC-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        String custId = "CUST_QC_" + UUID.randomUUID().toString().substring(0, 8);
        createApprovedLg("LG_QC_" + UUID.randomUUID().toString().substring(0, 8), custId, "CBU",
                new BigDecimal("200000.00"));
        letterGuaranteeService.freeze(buildFreezeReq(custId, "CBU", new BigDecimal("100000.00"), poNo));

        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 5, vin)));

        assertThrows(BizException.class, () -> service.receiveQcResult(buildQcReq(vin, 10, "通过")));

        VehicleCbuSoInterfaceSyncQo qo = new VehicleCbuSoInterfaceSyncQo();
        qo.setSoId(soItem.getSoId());
        List<VehicleCbuSoInterfaceSyncDo> syncs = vehicleCbuSoInterfaceSyncGateway
                .listVehicleCbuSoInterfaceSync(qo);
        long sapCount = syncs == null ? 0
                : syncs.stream().filter(s -> Integer.valueOf(4).equals(s.getInterfaceType())).count();
        assertEquals(0, sapCount, "保函回滚时不发布 SAP 事件，无类型4 记录");
    }
    @Test
    @DisplayName("7.4 SAP 同步失败不回滚 1008，类型4 记录 resultStatus=0")
    public void testSapSyncFailureKeepsQcPassed() {
        String poNo = "PO-QC-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);
        String soNo = soItem.getSoNo();
        String vin = "VIN" + poNo;

        String custId = "CUST_QC_" + UUID.randomUUID().toString().substring(0, 8);
        createApprovedLg("LG_QC_" + UUID.randomUUID().toString().substring(0, 8), custId, "CBU",
                new BigDecimal("200000.00"));
        letterGuaranteeService.freeze(buildFreezeReq(custId, "CBU", new BigDecimal("150000.00"), poNo));

        SapResult failResult = new SapResult();
        failResult.setCode("500");
        failResult.setMessage("SAP 异常");
        failResult.setSuccess(false);
        SapResult.Body body = new SapResult.Body();
        SapResult.SapRet esRet = new SapResult.SapRet();
        esRet.setRetCode("E");
        esRet.setRetReason("SAP 异常");
        body.setEsRet(esRet);
        failResult.setBody(body);
        when(sapGateway.syncSap(any())).thenReturn(failResult);

        service.receiveProductionStatus(List.of(buildStatusReq(soNo, 5, vin)));
        service.receiveQcResult(buildQcReq(vin, 10, "通过"));
        assertOrderStatus(poNo, soNo, VehicleCbuSoStatusEnum.QC_PASSED.getCode());

        awaitSyncCount(soItem.getSoId(), 4, 1, 5000);
        VehicleCbuSoInterfaceSyncQo qo = new VehicleCbuSoInterfaceSyncQo();
        qo.setSoId(soItem.getSoId());
        List<VehicleCbuSoInterfaceSyncDo> syncs = vehicleCbuSoInterfaceSyncGateway
                .listVehicleCbuSoInterfaceSync(qo);
        VehicleCbuSoInterfaceSyncDo sapSync = syncs.stream()
                .filter(s -> Integer.valueOf(4).equals(s.getInterfaceType())).findFirst().orElse(null);
        assertNotNull(sapSync, "类型4 SAP 同步记录存在");
        assertEquals(0, sapSync.getResultStatus(), "SAP 失败 resultStatus=0");
    }

    @Test
    @DisplayName("7.6 同 PO 多 SO 质检通过：先到者转应收后，后到者占用不足失败（PO 锁串行）")
    public void testConcurrentQcPassedSerializes() {
        String poNo = "PO-QC-" + UUID.randomUUID().toString().substring(0, 8);
        SoCreateReq req = new SoCreateReq();
        req.setPoNo(poNo);
        req.setPoId(UUID.randomUUID().toString());
        req.setSalesCompanyCode(TEST_SALES_COMPANY);
        req.setCustomerCode(TEST_CUSTOMER_CODE);
        req.setCustomerName(TEST_CUSTOMER_NAME);
        req.setOrderType(1);
        req.setPlanVersion("V1");
        List<SoCreateReq.SoDetailReq> details = new ArrayList<>();
        SoCreateReq.SoDetailReq d1 = new SoCreateReq.SoDetailReq();
        d1.setPoDetailNo("D001");
        d1.setDemandQuantity(new BigDecimal("2"));
        d1.setUnitPrice(new BigDecimal("150000.00"));
        d1.setMaterialCode("MAT001");
        d1.setMaterialDescription("测试物料1");
        details.add(d1);
        req.setDetails(details);
        SoResult result = service.createSo(req);
        String soNoA = result.getData().get(0).getSoNo();
        String soNoB = result.getData().get(1).getSoNo();
        String vinA = "VIN_A" + poNo;
        String vinB = "VIN_B" + poNo;

        String custId = "CUST_QC_" + UUID.randomUUID().toString().substring(0, 8);
        createApprovedLg("LG_QC_" + UUID.randomUUID().toString().substring(0, 8), custId, "CBU",
                new BigDecimal("200000.00"));
        letterGuaranteeService.freeze(buildFreezeReq(custId, "CBU", new BigDecimal("150000.00"), poNo));

        service.receiveProductionStatus(List.of(buildStatusReq(soNoA, 5, vinA)));
        service.receiveQcResult(buildQcReq(vinA, 10, "A 通过"));
        assertOrderStatus(poNo, soNoA, VehicleCbuSoStatusEnum.QC_PASSED.getCode());

        service.receiveProductionStatus(List.of(buildStatusReq(soNoB, 5, vinB)));
        assertThrows(BizException.class, () -> service.receiveQcResult(buildQcReq(vinB, 10, "B 通过")));
        assertNotEquals(VehicleCbuSoStatusEnum.QC_PASSED.getCode(),
                getSoBySoNo(poNo, soNoB).getOrderStatus());
    }



    @Test
    @DisplayName("reissueSap - 非 1008 归 SKIP；1008 补推见保函转应收端到端用例")
    public void testReissueSap() {
        String poNo = "PO-CB-" + UUID.randomUUID().toString().substring(0, 8);
        SoResult.SoItem soItem = createSingleSo(poNo);

        // 非 1008 状态补推：归 SKIP，不抛异常（对齐 issueToProduction 状态非未下发->SKIP 口径）
        VehicleCbuSoReissueSapReq req = new VehicleCbuSoReissueSapReq();
        req.setId(soItem.getSoId());
        VehicleCbuSoOperateResult result = service.reissueSap(req);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getSkipCount());
        assertEquals(0, result.getSuccessCount());
    }

    private SoResult.SoItem createSingleSo(String poNo) {
        SoCreateReq req = new SoCreateReq();
        req.setPoNo(poNo);
        req.setPoId(UUID.randomUUID().toString());
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

        SoResult result = service.createSo(req);
        assertNotNull(result);
        List<SoResult.SoItem> items = result.getData();
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

    private VehicleCbuSoReceiveQcReq buildQcReq(String vin, Integer result, String remark) {
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

    // ============== 保函冻结 setup（质检通过触发保函转应收） ==============

    private long countSyncByType(String soId, int interfaceType) {
        VehicleCbuSoInterfaceSyncQo qo = new VehicleCbuSoInterfaceSyncQo();
        qo.setSoId(soId);
        qo.setInterfaceType(interfaceType);
        List<VehicleCbuSoInterfaceSyncDo> list = vehicleCbuSoInterfaceSyncGateway
                .listVehicleCbuSoInterfaceSync(qo);
        return list == null ? 0 : list.size();
    }

    private long awaitSyncCount(String soId, int interfaceType, int expected, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (countSyncByType(soId, interfaceType) >= expected) {
                return countSyncByType(soId, interfaceType);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
        return countSyncByType(soId, interfaceType);
    }

    private ExportVehicleLetterGuaranteeAddOrUpdateReq baseAddReq(String lgCode, String customerId,
                                                                   String orderType, BigDecimal totalAmount,
                                                                   BigDecimal ratio) {
        ExportVehicleLetterGuaranteeAddOrUpdateReq req = new ExportVehicleLetterGuaranteeAddOrUpdateReq();
        if (lgCode != null) {
            req.setLgCode(lgCode);
        }
        req.setCustomerId(customerId);
        req.setLgType("PREPAYMENT");
        req.setOrderType(orderType);
        req.setBankName("中国工商银行杭州武林支行");
        req.setTotalAmount(totalAmount);
        req.setConversionRatio(ratio);
        req.setEffectiveDate(LocalDate.now());
        req.setExpiryDate(LocalDate.now().plusDays(180));
        return req;
    }

    private String createApprovedLg(String lgCode, String customerId, String orderType,
                                    BigDecimal totalAmount) {
        return createApprovedLgWithExpiry(lgCode, customerId, orderType, totalAmount,
                LocalDate.now().plusDays(180));
    }

    private String createApprovedLgWithExpiry(String lgCode, String customerId, String orderType,
                                              BigDecimal totalAmount, LocalDate expiryDate) {
        ExportVehicleLetterGuaranteeAddOrUpdateReq req = baseAddReq(lgCode, customerId, orderType, totalAmount,
                new BigDecimal("1.0"));
        req.setExpiryDate(expiryDate);
        req.setEffectiveDate(LocalDate.now().minusDays(1));
        String id = letterGuaranteeService.addOrUpdateExportVehicleLetterGuarantee(req);
        ExportVehicleLetterGuaranteeSubmitReq submitReq = new ExportVehicleLetterGuaranteeSubmitReq();
        submitReq.setId(id);
        letterGuaranteeService.submit(submitReq);
        ExportVehicleLetterGuaranteeApproveReq approveReq = new ExportVehicleLetterGuaranteeApproveReq();
        approveReq.setId(id);
        approveReq.setApproved(true);
        approveReq.setApproveRemark("同意");
        letterGuaranteeService.approve(approveReq);
        return id;
    }

    private ExportVehicleLetterGuaranteeFreezeReq buildFreezeReq(String customerId, String orderType,
                                                                 BigDecimal amount, String poNo) {
        ExportVehicleLetterGuaranteeFreezeReq req = new ExportVehicleLetterGuaranteeFreezeReq();
        req.setCustomerId(customerId);
        req.setOrderType(orderType);
        req.setOrderId(UUID.randomUUID().toString());
        req.setOrderNo(poNo);
        req.setPoNo(poNo);
        req.setRequiredAmount(amount);
        req.setOrderCreateTime(LocalDate.now());
        return req;
    }
}
