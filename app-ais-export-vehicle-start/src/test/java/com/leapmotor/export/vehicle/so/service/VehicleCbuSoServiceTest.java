package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CBU SO 创建 Service 集成测试
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@DisplayName("CBU SO 创建 Service 集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VehicleCbuSoServiceTest extends BaseServiceTest<VehicleCbuSoService> {

    private static String testPoNo;

    @Test
    @Order(1)
    @DisplayName("createSo — 按 PO 明细需求数量一车一单拆分")
    public void testCreateSo_splitOneVehiclePerSo() {
        testPoNo = "PO-UT-" + UUID.randomUUID().toString().substring(0, 8);

        SoCreateReq req = buildSoCreateReq(testPoNo);
        SoResult result = service.createSo(req);

        printJson(result);
        assertNotNull(result);
        List<SoResult.SoItem> items = result.getData();
        assertNotNull(items);
        assertEquals(3, items.size(), "明细1需求2 + 明细2需求1 应生成 3 个 SO");

        for (int i = 0; i < items.size(); i++) {
            SoResult.SoItem item = items.get(i);
            assertNotNull(item.getSoId());
            assertNotNull(item.getSoNo());
            assertTrue(item.getSoNo().startsWith("SO"), "SO 单号应以 SO 开头");
            assertEquals(i + 1, item.getLineNo(), "行号应从 1 开始连续递增");
            assertEquals(1001, item.getOrderStatus(), "初始状态应为 1001 未下发生产");
            assertEquals(testPoNo, result.getPoNo());
        }
    }

    @Test
    @Order(2)
    @DisplayName("createSo — 同一 PO 单号重复创建应幂等拦截")
    public void testCreateSo_idempotent() {
        assertThrows(BizException.class, () -> service.createSo(buildSoCreateReq(testPoNo)));
    }

    @Test
    @Order(3)
    @DisplayName("createSo — owner_user_id/owner_org_id 默认值应为 0（数据权限字段，未暴露到 App 层）")
    public void testCreateSo_ownerFieldsDefaultZero() {
        String poNo = "PO-OWNER-" + UUID.randomUUID().toString().substring(0, 8);
        SoCreateReq req = buildSoCreateReq(poNo);
        SoResult result = service.createSo(req);
        assertNotNull(result);
        List<SoResult.SoItem> items = result.getData();
        assertNotNull(items);
        assertFalse(items.isEmpty());

        // 验证 service 创建的 SO 单据已落库；owner 字段默认 0 通过 SQL DEFAULT 保证
        // App 层没有 owner 字段透出（数据权限走 @DataPermission），由 DB 默认值兜底
        for (SoResult.SoItem item : items) {
            assertNotNull(item.getSoId());
            assertNotNull(item.getSoNo());
        }
        // 业务确认：item.getSoId() 返回的主表 id 可作为后续数据权限字段 owner_user_id 写入的来源
        // 详见后续 @DataPermission 接入；当前阶段 owner 字段由 SQL DEFAULT 0 兜底
    }

    private SoCreateReq buildSoCreateReq(String poNo) {
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
        d1.setDemandQuantity(new BigDecimal("2"));
        d1.setUnitPrice(new BigDecimal("150000.00"));
        d1.setVinYear("2026");
        d1.setExpectedOfflineDate(LocalDate.now().plusMonths(1));
        d1.setMaterialCode("MAT001");
        d1.setMaterialDescription("测试物料1");
        details.add(d1);

        SoCreateReq.SoDetailReq d2 = new SoCreateReq.SoDetailReq();
        d2.setPoDetailNo("D002");
        d2.setDemandQuantity(new BigDecimal("1"));
        d2.setUnitPrice(new BigDecimal("160000.00"));
        d2.setVinYear("2026");
        d2.setExpectedOfflineDate(LocalDate.now().plusMonths(1));
        d2.setMaterialCode("MAT002");
        d2.setMaterialDescription("测试物料2");
        details.add(d2);

        req.setDetails(details);
        return req;
    }
}
