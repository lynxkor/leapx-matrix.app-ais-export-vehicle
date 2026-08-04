package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SO 接口同步记录表 Service 集成测试
 *
 * <p>覆盖 {@link VehicleCbuSoInterfaceSyncService} 全部分支：
 * <ul>
 *   <li>{@code listVehicleCbuSoInterfaceSync} / {@code pageVehicleCbuSoInterfaceSync} — 列表与分页；</li>
 *   <li>{@code getVehicleCbuSoInterfaceSyncById} — 按 ID 查询（含不存在场景）；</li>
 *   <li>{@code addOrUpdateVehicleCbuSoInterfaceSync} — 新增与编辑；</li>
 *   <li>{@code deleteVehicleCbuSoInterfaceSyncByIds} / {@code enableOrDisableVehicleCbuSoInterfaceSyncByIds} — 删除与启停。</li>
 * </ul>
 *
 * @author lijianuo
 * @since 2026-07-17
 */
@DisplayName("SO 接口同步记录表 Service 集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VehicleCbuSoInterfaceSyncServiceTest
        extends BaseServiceTest<VehicleCbuSoInterfaceSyncService> {

    // ============== 测试夹具（@Order 控制执行顺序） ==============

    private static String syncId;              // 新增的同步记录 ID
    private static final String FIXED_SOID = "SO-UT-" + UUID.randomUUID().toString().substring(0, 8);

    // ============== CRUD ==============

    @Test
    @Order(1)
    @DisplayName("list — 无条件返回同步记录列表")
    public void testList_returnsRows() {
        List<VehicleCbuSoInterfaceSyncResp> rows =
                service.listVehicleCbuSoInterfaceSync(new VehicleCbuSoInterfaceSyncParam());
        printJson(rows);
        assertNotNull(rows);
    }

    @Test
    @Order(2)
    @DisplayName("page — 分页查询（pageNum=1, pageSize=10）")
    public void testPage_paginatesResults() {
        VehicleCbuSoInterfaceSyncParam param = new VehicleCbuSoInterfaceSyncParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        PageResult<VehicleCbuSoInterfaceSyncResp> page = service.pageVehicleCbuSoInterfaceSync(param);
        printJson(page);
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    @Order(3)
    @DisplayName("page — 按 soId + interfaceType 组合筛选")
    public void testPage_filtersBySoIdAndInterfaceType() {
        VehicleCbuSoInterfaceSyncParam param = new VehicleCbuSoInterfaceSyncParam();
        param.setSoId(FIXED_SOID);
        param.setInterfaceType(1);
        param.setPageNum(1L);
        param.setPageSize(10L);
        PageResult<VehicleCbuSoInterfaceSyncResp> page = service.pageVehicleCbuSoInterfaceSync(param);
        printJson(page);
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    @Order(4)
    @DisplayName("get — 通过 id 查询（不存在的 id 返回 null）")
    public void testGet_returnsNull_whenNotExists() {
        VehicleCbuSoInterfaceSyncResp missing =
                service.getVehicleCbuSoInterfaceSyncById("SYNC_NOT_EXIST_" + UUID.randomUUID());
        printJson(missing);
        assertNull(missing);
    }

    @Test
    @Order(5)
    @DisplayName("addOrUpdate — 新增一条同步记录并回查")
    public void testAddOrUpdate_createsSync() {
        VehicleCbuSoInterfaceSyncAddOrUpdateReq req = baseAddReq(1, 1);
        String id = service.addOrUpdateVehicleCbuSoInterfaceSync(req);
        printJson(id);
        assertNotNull(id);
        assertFalse(id.isEmpty());

        VehicleCbuSoInterfaceSyncResp resp = service.getVehicleCbuSoInterfaceSyncById(id);
        assertNotNull(resp);
        assertEquals(id, resp.getId());
        assertEquals(FIXED_SOID, resp.getSoId());
        assertEquals(req.getInterfaceType(), resp.getInterfaceType());
        assertEquals(req.getResultStatus(), resp.getResultStatus());
        syncId = id;
    }

    @Test
    @Order(6)
    @DisplayName("addOrUpdate — 编辑已存在的同步记录（更新 resultStatus 与 responseInfo）")
    public void testAddOrUpdate_updatesSync() {
        assertNotNull(syncId, "前置 Order(5) 必须先创建同步记录");

        VehicleCbuSoInterfaceSyncAddOrUpdateReq req = baseAddReq(1, 0);
        req.setId(syncId);
        req.setResponseInfo("{\"err\":\"ups UT update\"}");

        String updatedId = service.addOrUpdateVehicleCbuSoInterfaceSync(req);
        printJson(updatedId);
        assertEquals(syncId, updatedId, "编辑应返回原 ID");

        VehicleCbuSoInterfaceSyncResp resp = service.getVehicleCbuSoInterfaceSyncById(updatedId);
        assertEquals(0, resp.getResultStatus());
        assertEquals(req.getResponseInfo(), resp.getResponseInfo());
    }

    @Test
    @Order(7)
    @DisplayName("enableOrDisable — 切换同步记录状态")
    public void testEnableOrDisable_togglesStatus() {
        assertNotNull(syncId, "前置 Order(5) 必须先创建同步记录");

        LpBaseParam param = new LpBaseParam();
        param.setId(syncId);
        param.setStatus(0);
        boolean ok = service.enableOrDisableVehicleCbuSoInterfaceSyncByIds(param);
        printJson(ok);
        assertTrue(ok);

        VehicleCbuSoInterfaceSyncResp resp = service.getVehicleCbuSoInterfaceSyncById(syncId);
        assertNotNull(resp);
        assertEquals(0, resp.getStatus());
    }

    @Test
    @Order(8)
    @DisplayName("delete — 按 id 列表删除同步记录")
    public void testDeleteByIds_removesSync() {
        assertNotNull(syncId, "前置 Order(5) 必须先创建同步记录");

        boolean ok = service.deleteVehicleCbuSoInterfaceSyncByIds(Collections.singletonList(syncId));
        printJson(ok);
        assertTrue(ok);

        VehicleCbuSoInterfaceSyncResp missing = service.getVehicleCbuSoInterfaceSyncById(syncId);
        assertNull(missing, "删除后应查不到");
    }

    // ============== 测试数据构造 ==============

    private VehicleCbuSoInterfaceSyncAddOrUpdateReq baseAddReq(int interfaceType, int resultStatus) {
        VehicleCbuSoInterfaceSyncAddOrUpdateReq req = new VehicleCbuSoInterfaceSyncAddOrUpdateReq();
        req.setSoId(FIXED_SOID);
        req.setInterfaceType(interfaceType);
        req.setInterfaceDesc("UT 接口类型 " + interfaceType);
        req.setCallTime(LocalDateTime.now());
        req.setRequestInfo("{\"req\":\"ups UT\"}");
        req.setResultStatus(resultStatus);
        req.setResponseInfo(resultStatus == 1 ? "{\"ok\":true}" : "{\"ok\":false}");
        req.setRemark("UT 备注 " + UUID.randomUUID().toString().substring(0, 8));
        return req;
    }
}
