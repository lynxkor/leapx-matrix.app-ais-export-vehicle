package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
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
 * SO 车辆明细表 Service 集成测试
 *
 * <p>覆盖 {@link VehicleCbuSoDetailService} 全部分支：
 * <ul>
 *   <li>{@code listVehicleCbuSoDetail} / {@code pageVehicleCbuSoDetail} — 列表与分页；</li>
 *   <li>{@code getVehicleCbuSoDetailById} — 按 ID 查询（含不存在场景）；</li>
 *   <li>{@code addOrUpdateVehicleCbuSoDetail} — 新增与编辑；</li>
 *   <li>{@code deleteVehicleCbuSoDetailByIds} / {@code enableOrDisableVehicleCbuSoDetailByIds} — 删除与启停。</li>
 * </ul>
 *
 * @author lijianuo
 * @since 2026-07-17
 */
@DisplayName("SO 车辆明细表 Service 集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VehicleCbuSoDetailServiceTest extends BaseServiceTest<VehicleCbuSoDetailService> {

    // ============== 测试夹具（@Order 控制执行顺序） ==============

    private static String detailId;            // 新增的明细 ID
    private static final String FIXED_SONO = "SO-UT-" + UUID.randomUUID().toString().substring(0, 8);

    // ============== CRUD ==============

    @Test
    @Order(1)
    @DisplayName("list — 无条件返回明细列表")
    public void testList_returnsRows() {
        List<VehicleCbuSoDetailResp> rows = service.listVehicleCbuSoDetail(new VehicleCbuSoDetailParam());
        printJson(rows);
        assertNotNull(rows);
    }

    @Test
    @Order(2)
    @DisplayName("page — 分页查询（pageNum=1, pageSize=10）")
    public void testPage_paginatesResults() {
        VehicleCbuSoDetailParam param = new VehicleCbuSoDetailParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        PageResult<VehicleCbuSoDetailResp> page = service.pageVehicleCbuSoDetail(param);
        printJson(page);
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    @Order(3)
    @DisplayName("page — 按 soNo 精确筛选")
    public void testPage_filtersBySoNo() {
        VehicleCbuSoDetailParam param = new VehicleCbuSoDetailParam();
        param.setSoNo(FIXED_SONO);
        param.setPageNum(1L);
        param.setPageSize(10L);
        PageResult<VehicleCbuSoDetailResp> page = service.pageVehicleCbuSoDetail(param);
        printJson(page);
        assertNotNull(page);
        // 无数据时 records 为空列表，total 为 0
        assertNotNull(page.getRecords());
    }

    @Test
    @Order(4)
    @DisplayName("get — 通过 id 查询（不存在的 id 返回 null）")
    public void testGet_returnsNull_whenNotExists() {
        VehicleCbuSoDetailResp missing = service.getVehicleCbuSoDetailById("DETAIL_NOT_EXIST_" + UUID.randomUUID());
        printJson(missing);
        assertNull(missing);
    }

    @Test
    @Order(5)
    @DisplayName("addOrUpdate — 新增一条明细并回查")
    public void testAddOrUpdate_createsDetail() {
        VehicleCbuSoDetailAddOrUpdateReq req = baseAddReq();
        String id = service.addOrUpdateVehicleCbuSoDetail(req);
        printJson(id);
        assertNotNull(id);
        assertFalse(id.isEmpty());

        VehicleCbuSoDetailResp resp = service.getVehicleCbuSoDetailById(id);
        assertNotNull(resp);
        assertEquals(id, resp.getId());
        assertEquals(FIXED_SONO, resp.getSoNo());
        assertEquals(req.getVin(), resp.getVin());
        assertEquals(req.getMaterialCode(), resp.getMaterialCode());
        // 注：owner_user_id/owner_org_id 字段在 SQL 中已加默认值 0（数据权限字段，未暴露到 App 层 Req/Resp），
        // 可通过 Entity 或 Mapper XML 验证。此处仅验证业务字段。
        detailId = id;
    }

    @Test
    @Order(6)
    @DisplayName("addOrUpdate — 编辑已存在的明细（更新备注）")
    public void testAddOrUpdate_updatesDetail() {
        assertNotNull(detailId, "前置 Order(5) 必须先创建明细");

        VehicleCbuSoDetailAddOrUpdateReq req = baseAddReq();
        req.setId(detailId);
        req.setRemark("UT 更新备注 " + UUID.randomUUID().toString().substring(0, 8));

        String updatedId = service.addOrUpdateVehicleCbuSoDetail(req);
        printJson(updatedId);
        assertEquals(detailId, updatedId, "编辑应返回原 ID");

        VehicleCbuSoDetailResp resp = service.getVehicleCbuSoDetailById(updatedId);
        assertEquals(req.getRemark(), resp.getRemark());
    }

    @Test
    @Order(7)
    @DisplayName("enableOrDisable — 切换明细状态")
    public void testEnableOrDisable_togglesStatus() {
        assertNotNull(detailId, "前置 Order(5) 必须先创建明细");

        LpBaseParam param = new LpBaseParam();
        param.setId(detailId);
        param.setStatus(0);
        boolean ok = service.enableOrDisableVehicleCbuSoDetailByIds(param);
        printJson(ok);
        assertTrue(ok);

        VehicleCbuSoDetailResp resp = service.getVehicleCbuSoDetailById(detailId);
        assertNotNull(resp);
        assertEquals(0, resp.getStatus());
    }

    @Test
    @Order(8)
    @DisplayName("delete — 按 id 列表删除明细")
    public void testDeleteByIds_removesDetail() {
        assertNotNull(detailId, "前置 Order(5) 必须先创建明细");

        boolean ok = service.deleteVehicleCbuSoDetailByIds(Collections.singletonList(detailId));
        printJson(ok);
        assertTrue(ok);

        VehicleCbuSoDetailResp missing = service.getVehicleCbuSoDetailById(detailId);
        assertNull(missing, "删除后应查不到");
    }

    // ============== 测试数据构造 ==============

    private VehicleCbuSoDetailAddOrUpdateReq baseAddReq() {
        VehicleCbuSoDetailAddOrUpdateReq req = new VehicleCbuSoDetailAddOrUpdateReq();
        req.setSoNo(FIXED_SONO);
        req.setInternalVehicleNo("IVN" + UUID.randomUUID().toString().substring(0, 8));
        req.setProductionIdentifyCode("PIC" + UUID.randomUUID().toString().substring(0, 8));
        req.setVin("VIN" + UUID.randomUUID().toString().substring(0, 8));
        req.setMaterialCode("MAT001");
        req.setMaterialDescription("测试物料");
        req.setSalesMarket("G117");
        req.setModelYear("2026");
        req.setOptionalPackage("OPT-STD");
        LocalDateTime now = LocalDateTime.now();
        req.setScheduleTime(now.plusDays(1));
        req.setBarcodePrintTime(now.plusDays(2));
        req.setBodyInTime(now.plusDays(3));
        req.setPaintInTime(now.plusDays(4));
        req.setAssemblyInTime(now.plusDays(5));
        req.setAssemblyOutTime(now.plusDays(6));
        req.setRemark("UT 备注");
        return req;
    }
}
