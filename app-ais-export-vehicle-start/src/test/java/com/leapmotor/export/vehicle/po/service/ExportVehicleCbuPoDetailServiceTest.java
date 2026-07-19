package com.leapmotor.export.vehicle.po.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.req.HongqiCbuOrderDetailReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PO 明细 Service 测试")
public class ExportVehicleCbuPoDetailServiceTest extends BaseServiceTest<ExportVehicleCbuPoDetailService> {

    @org.springframework.beans.factory.annotation.Autowired
    private ExportVehicleCbuPoService purchaseOrderService;

    @Test
    @DisplayName("listExportVehicleCbuPoDetail - 按 poNo 查询明细列表")
    public void testList_byPoNo() {
        String poNo = createPoWithDetails();
        ExportVehicleCbuPoDetailParam param = new ExportVehicleCbuPoDetailParam();
        param.setPoNo(poNo);
        List<ExportVehicleCbuPoDetailResp> list = service.listExportVehicleCbuPoDetail(param);
        assertNotNull(list);
        assertFalse(list.isEmpty(), "应查出明细");
    }

    @Test
    @DisplayName("pageExportVehicleCbuPoDetail - 分页查询")
    public void testPage() {
        ExportVehicleCbuPoDetailParam param = new ExportVehicleCbuPoDetailParam();
        assertDoesNotThrow(() -> service.pageExportVehicleCbuPoDetail(param));
    }

    @Test
    @DisplayName("getExportVehicleCbuPoDetailById - 按ID查询")
    public void testGetById() {
        String poNo = createPoWithDetails();
        ExportVehicleCbuPoDetailParam param = new ExportVehicleCbuPoDetailParam();
        param.setPoNo(poNo);
        List<ExportVehicleCbuPoDetailResp> list = service.listExportVehicleCbuPoDetail(param);
        assertNotNull(list.get(0).getId());
        ExportVehicleCbuPoDetailResp resp = service.getExportVehicleCbuPoDetailById(list.get(0).getId());
        assertNotNull(resp);
    }

    @Test
    @DisplayName("addOrUpdate - 新增明细")
    public void testAddOrUpdate() {
        com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailAddOrUpdateReq req = new com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailAddOrUpdateReq();
        String poNo = createPoWithDetails();
        req.setPoNo(poNo);
        req.setPoDetailNo("POD_TEST_001");
        req.setOrderDetailNumber("9999");
        req.setMaterialCode("B1099R1VEULWLDS775");
        req.setSyncMaterialCode("B1099R1VEULWLDS775");
        req.setDemandQuantity(new BigDecimal("1"));
        req.setExpectedOfflineDate(LocalDate.now());
        req.setVinYear("2026");
        assertDoesNotThrow(() -> service.addOrUpdateExportVehicleCbuPoDetail(req));
    }

    @Test
    @DisplayName("delete - 删除明细")
    public void testDelete() {
        String poNo = createPoWithDetails();
        ExportVehicleCbuPoDetailParam param = new ExportVehicleCbuPoDetailParam();
        param.setPoNo(poNo);
        List<ExportVehicleCbuPoDetailResp> list = service.listExportVehicleCbuPoDetail(param);
        assertTrue(service.deleteExportVehicleCbuPoDetailByIds(java.util.Collections.singletonList(list.get(0).getId())));
    }

    @Test
    @DisplayName("enableOrDisable - 启用/禁用")
    public void testEnableOrDisable() {
        String poNo = createPoWithDetails();
        ExportVehicleCbuPoDetailParam param = new ExportVehicleCbuPoDetailParam();
        param.setPoNo(poNo);
        List<ExportVehicleCbuPoDetailResp> list = service.listExportVehicleCbuPoDetail(param);
        com.leapmotor.paltform.ais.common.model.base.LpBaseParam p = new com.leapmotor.paltform.ais.common.model.base.LpBaseParam();
        p.setIdSet(java.util.Collections.singleton(list.get(0).getId()));
        p.setStatus(1);
        assertDoesNotThrow(() -> service.enableOrDisableExportVehicleCbuPoDetailByIds(p));
    }

    private String createPoWithDetails() {
        String poNo = "HQ" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%06d", Math.abs(System.nanoTime()) % 1000000L);
        HongqiCbuPurchaseOrderReq req = new HongqiCbuPurchaseOrderReq();
        req.setPoNo(poNo);
        req.setCustomerCode("HQ01");
        req.setPlanVersion("20260719V1");
        HongqiCbuOrderDetailReq d = new HongqiCbuOrderDetailReq();
        d.setDetailNumber("0001");
        d.setMaterialCode("B1099R1VEULWLDS775");
        d.setDemandQuantity(new BigDecimal("1"));
        d.setVinYear("2026");
        d.setExpectedOfflineDate("20261231");
        req.setOrderDetailList(Arrays.asList(d));
        purchaseOrderService.createFromHongqi(req);
        return poNo;
    }
}
