package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.acl.rpc.aps.gateway.ApsGateway;
import com.leapmotor.export.vehicle.acl.rpc.aps.resp.ApsResult;
import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListQueryAvailableVehiclesReq;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 应付清单-查询可添加车辆 Service 集成测试
 *
 * <p>覆盖 {@code /web/payableList/payableListManagement/queryAvailableVehicles}：
 * <ul>
 *   <li>按应付清单客户编码过滤（只返回同客户车辆）；</li>
 *   <li>只返回质检通过（qc_result=10）的车辆，质检未通过不返回；</li>
 *   <li>排除已绑定到其它应付清单的车辆（NOT IN），避免重复绑定；</li>
 *   <li>支持按 VIN 模糊过滤；</li>
 *   <li>应付清单不存在时抛出业务异常。</li>
 * </ul>
 *
 * <p>依赖：Nacos 配置可加载 + Redis（{@code @DistributeLock}）；APS 网关以 {@link MockBean} 桩成功。
 *
 * @author 赵吴涯
 * @since 2026-08-26
 */
@DisplayName("应付清单-查询可添加车辆 Service 集成测试")
@ActiveProfiles("dev")
public class PayableListAvailableVehicleServiceTest extends BaseServiceTest<PayableListService> {

    @MockBean
    private ApsGateway apsGateway;

    @Resource
    private VehicleCbuSoService vehicleCbuSoService;

    @Resource
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    @Resource
    private VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;

    @Resource
    private PayableListGateway payableListGateway;

    @Resource
    private PayableListDetailGateway payableListDetailGateway;

    @BeforeEach
    public void stubApsSuccess() {
        ApsResult ok = new ApsResult();
        ok.setCode(200);
        ok.setMessage("ok");
        when(apsGateway.issueSync(any())).thenReturn(ok);
    }

    @Test
    @DisplayName("查询可添加车辆 - 仅返回同客户、质检通过、未绑定其它清单的车辆")
    public void testQueryAvailableVehicles_onlyPassedAndUnboundAndSameCustomer() {
        String customerCode = "CUST-T-" + randomSuffix();
        String otherCustomerCode = "CUST-T-" + randomSuffix();

        // 应付清单
        String payableListId = createPayableList(customerCode);

        // SO-A：同客户、质检通过、未绑定 → 应返回
        SoResult.SoItem soA = createSo(customerCode);
        setQcResult(soA.getSoId(), VehicleCbuSoQcResultEnum.PASSED.getCode());

        // SO-B：同客户、质检不通过 → 不应返回
        SoResult.SoItem soB = createSo(customerCode);
        setQcResult(soB.getSoId(), VehicleCbuSoQcResultEnum.FAILED.getCode());

        // SO-C：同客户、质检通过、但已绑定到其它应付清单 → 不应返回
        SoResult.SoItem soC = createSo(customerCode);
        setQcResult(soC.getSoId(), VehicleCbuSoQcResultEnum.PASSED.getCode());
        bindSo(createPayableList(customerCode), soC.getSoId(), soC.getSoNo());

        // SO-D：其它客户、质检通过、未绑定 → 不应返回
        SoResult.SoItem soD = createSo(otherCustomerCode);
        setQcResult(soD.getSoId(), VehicleCbuSoQcResultEnum.PASSED.getCode());

        // 执行查询
        PayableListQueryAvailableVehiclesReq req = new PayableListQueryAvailableVehiclesReq();
        req.setPayableListId(payableListId);
        req.setPageNum(1L);
        req.setPageSize(50L);
        PageResult<SoOrderForPayableDto> page = service.queryAvailableVehicles(req);

        printJson(page);

        List<String> returnedSoNos = page.getRecords().stream()
                .map(SoOrderForPayableDto::getSoNo)
                .collect(Collectors.toList());

        assertTrue(returnedSoNos.contains(soA.getSoNo()), "应返回：同客户+质检通过+未绑定（SO-A）");
        assertFalse(returnedSoNos.contains(soB.getSoNo()), "不应返回：质检未通过（SO-B）");
        assertFalse(returnedSoNos.contains(soC.getSoNo()), "不应返回：已绑定其它应付清单（SO-C）");
        assertFalse(returnedSoNos.contains(soD.getSoNo()), "不应返回：其它客户（SO-D）");

        // 全量断言：返回记录客户编码必须等于清单客户编码，且不与任何已绑定 SO 重复
        List<String> boundSoIds = payableListDetailGateway.listAllBoundSoIds();
        page.getRecords().forEach(r -> {
            assertEquals(customerCode, r.getCustomerCode(), "返回记录客户编码应与清单一致");
            assertFalse(boundSoIds.contains(r.getSoId()), "返回记录不应包含已绑定 SO");
        });
    }

    @Test
    @DisplayName("查询可添加车辆 - 支持按 VIN 模糊过滤")
    public void testQueryAvailableVehicles_filterByVin() {
        String customerCode = "CUST-T-" + randomSuffix();
        String payableListId = createPayableList(customerCode);

        SoResult.SoItem soA = createSo(customerCode);
        setQcResult(soA.getSoId(), VehicleCbuSoQcResultEnum.PASSED.getCode());
        setVin(soA.getSoId(), "LSV" + randomSuffix());

        SoResult.SoItem soB = createSo(customerCode);
        setQcResult(soB.getSoId(), VehicleCbuSoQcResultEnum.PASSED.getCode());
        setVin(soB.getSoId(), "OTHER" + randomSuffix());

        PayableListQueryAvailableVehiclesReq req = new PayableListQueryAvailableVehiclesReq();
        req.setPayableListId(payableListId);
        req.setVin("LSV");
        req.setPageNum(1L);
        req.setPageSize(50L);

        PageResult<SoOrderForPayableDto> page = service.queryAvailableVehicles(req);

        printJson(page);

        List<String> returnedSoNos = page.getRecords().stream()
                .map(SoOrderForPayableDto::getSoNo)
                .collect(Collectors.toList());
        assertTrue(returnedSoNos.contains(soA.getSoNo()), "按 VIN 过滤应返回匹配车辆");
        assertFalse(returnedSoNos.contains(soB.getSoNo()), "按 VIN 过滤不应返回不匹配车辆");
    }

    @Test
    @DisplayName("查询可添加车辆 - 应付清单不存在时抛出业务异常")
    public void testQueryAvailableVehicles_payableListNotExist() {
        PayableListQueryAvailableVehiclesReq req = new PayableListQueryAvailableVehiclesReq();
        req.setPayableListId("not-exist-" + randomSuffix());
        req.setPageNum(1L);
        req.setPageSize(10L);

        BizException ex = assertThrows(
                BizException.class,
                () -> service.queryAvailableVehicles(req),
                "应付清单不存在时应抛出业务异常"
        );
        assertTrue(ex.getMessage().contains("应付清单不存在"), "异常消息应提示应付清单不存在");
    }

    // ==================== 辅助 ====================

    /**
     * 创建草稿状态应付清单，返回主键 ID。
     */
    private String createPayableList(String customerCode) {
        PayableListDo pl = new PayableListDo();
        pl.setPayableListNo("YF-T-" + randomSuffix());
        pl.setBillStatus(PayableListStatusEnum.DRAFT.getCode());
        pl.setCustomerCode(customerCode);
        pl.setCustomerName("测试客户");
        pl.setGenerateMonth("202608");
        pl.setVersion(1);
        return payableListGateway.addPayableList(pl);
    }

    /**
     * 创建 SO 订单（含单条明细），返回 SO 项。
     */
    private SoResult.SoItem createSo(String customerCode) {
        String poNo = "PO-T-" + randomSuffix();
        SoCreateReq req = new SoCreateReq();
        req.setPoNo(poNo);
        req.setSalesCompanyCode("1000");
        req.setCustomerCode(customerCode);
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

        SoResult result = vehicleCbuSoService.createSo(req);
        assertNotNull(result, "创建 SO 结果不应为 null");
        assertNotNull(result.getData(), "SO 列表不应为 null");
        assertEquals(1, result.getData().size(), "应创建 1 条 SO");
        return result.getData().get(0);
    }

    /**
     * 更新 SO 主表质检结果（qc_result / qc_result_time / last_qc_result）。
     */
    private void setQcResult(String soId, Integer qcResultCode) {
        VehicleCbuSoDo so = vehicleCbuSoGateway.getVehicleCbuSoById(soId);
        assertNotNull(so, "SO 主表不应为空");
        so.updateQcResult(qcResultCode);
        vehicleCbuSoGateway.updateVehicleCbuSo(so);
    }

    /**
     * 写入 SO 车辆明细的 VIN 号。
     */
    private void setVin(String soId, String vin) {
        VehicleCbuSoDetailDo detail = vehicleCbuSoDetailGateway.getVehicleCbuSoDetailById(soId);
        assertNotNull(detail, "SO 车辆明细不应为空");
        detail.setVin(vin);
        vehicleCbuSoDetailGateway.updateVehicleCbuSoDetail(detail);
    }

    /**
     * 将 SO 绑定到指定应付清单（写一条明细），用于模拟「已绑定」状态。
     */
    private void bindSo(String payableListId, String soId, String soNo) {
        PayableListDetailDo detail = new PayableListDetailDo();
        detail.setPayableListId(payableListId);
        detail.setSoId(soId);
        detail.setSoNo(soNo);
        detail.setBillStatus(PayableListStatusEnum.DRAFT.getCode());
        detail.setGenerateMonth("202608");
        detail.setCustomerCode("测试客户");
        payableListDetailGateway.addPayableListDetail(detail);
    }

    private String randomSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
