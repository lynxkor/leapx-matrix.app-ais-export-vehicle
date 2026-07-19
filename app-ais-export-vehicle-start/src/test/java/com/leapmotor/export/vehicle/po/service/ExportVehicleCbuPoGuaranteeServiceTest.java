package com.leapmotor.export.vehicle.po.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PO-保函关联 Service 测试")
public class ExportVehicleCbuPoGuaranteeServiceTest extends BaseServiceTest<ExportVehicleCbuPoGuaranteeService> {

    @Test
    @DisplayName("listExportVehicleCbuPoGuarantee - 列表查询不报错")
    public void testList() {
        ExportVehicleCbuPoGuaranteeParam param = new ExportVehicleCbuPoGuaranteeParam();
        assertDoesNotThrow(() -> service.listExportVehicleCbuPoGuarantee(param));
    }

    @Test
    @DisplayName("pageExportVehicleCbuPoGuarantee - 分页查询不报错")
    public void testPage() {
        ExportVehicleCbuPoGuaranteeParam param = new ExportVehicleCbuPoGuaranteeParam();
        assertDoesNotThrow(() -> service.pageExportVehicleCbuPoGuarantee(param));
    }

    @Test
    @DisplayName("addOrUpdate - 新增关联")
    public void testAddOrUpdate() {
        com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeAddOrUpdateReq req = new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeAddOrUpdateReq();
        req.setPoNo("HQ_TEST_G_001");
        req.setGuaranteeId("lg-test-id");
        req.setGuaranteeNumber("LG_TEST_001");
        req.setPoCurrentOccupiedAmount(new java.math.BigDecimal("100.00"));
        req.setPoCurrentReceivableAmount(java.math.BigDecimal.ZERO);
        assertDoesNotThrow(() -> service.addOrUpdateExportVehicleCbuPoGuarantee(req));
    }

    @Test
    @DisplayName("getById - 按ID查询")
    public void testGetById() {
        com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam param = new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam();
        java.util.List<com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp> list = service.listExportVehicleCbuPoGuarantee(param);
        if (!list.isEmpty()) {
            assertNotNull(service.getExportVehicleCbuPoGuaranteeById(list.get(0).getId()));
        }
    }

    @Test
    @DisplayName("delete - 删除")
    public void testDelete() {
        com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam param = new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam();
        java.util.List<com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp> list = service.listExportVehicleCbuPoGuarantee(param);
        if (!list.isEmpty()) {
            assertTrue(service.deleteExportVehicleCbuPoGuaranteeByIds(java.util.Collections.singletonList(list.get(0).getId())));
        }
    }

    @Test
    @DisplayName("enableOrDisable - 启用/禁用")
    public void testEnableOrDisable() {
        com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam param = new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam();
        java.util.List<com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp> list = service.listExportVehicleCbuPoGuarantee(param);
        if (!list.isEmpty()) {
            com.leapmotor.paltform.ais.common.model.base.LpBaseParam p = new com.leapmotor.paltform.ais.common.model.base.LpBaseParam();
            p.setIdSet(java.util.Collections.singletonList(list.get(0).getId()));
            p.setStatus(1);
            assertDoesNotThrow(() -> service.enableOrDisableExportVehicleCbuPoGuaranteeByIds(p));
        }
    }
}
