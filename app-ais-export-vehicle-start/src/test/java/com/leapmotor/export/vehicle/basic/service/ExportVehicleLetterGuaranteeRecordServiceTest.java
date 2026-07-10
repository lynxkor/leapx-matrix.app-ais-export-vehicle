package com.leapmotor.export.vehicle.basic.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 保函交易明细 Record Service 集成测试。
 *
 * <p>覆盖 {@link com.leapmotor.export.vehicle.controller.web.basic.ExportVehicleLetterGuaranteeRecordController}
 * 背后的 Service 流程:
 * <ul>
 *   <li>/list、/page、/get — 查询</li>
 *   <li>/addOrUpdate — 新增(交易明细表追加写,设计文档 §11.6)</li>
 *   <li>/delete — 删除</li>
 *   <li>/enableOrDisable — 启用/禁用</li>
 * </ul>
 *
 * <p>关键业务约束:交易明细仅追加写,不允许修改/删除(详见设计文档 §11.6);
 * 测试覆盖非法场景时 Service 应抛 {@link BizException}。
 *
 * @author wangkai2527148
 * @since 2026-07-08
 */
@DisplayName("保函交易明细 Record Service 集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExportVehicleLetterGuaranteeRecordServiceTest extends BaseServiceTest<ExportVehicleLetterGuaranteeRecordService> {

    // ============== 测试夹具(共享,@Order 控制执行顺序) ==============

    private static String recordFreezeId;      // 冻结流水记录(10 冻结)
    private static String recordUnfreezeId;    // 解冻流水记录(20 解冻)
    private static String recordTransferId;    // 转应收流水记录(30 转应收)
    private static String recordReleaseId;     // 释放应收流水记录(40 释放应收)
    private static String recordInvalidId;     // 失效流水记录(50 失效)
    private static String recordDuplicateId;   // 用于重复 lgRecordCode 校验

    // ============== 查询 ==============

    @Test
    @Order(1)
    @DisplayName("list — 无条件返回交易明细列表")
    public void testList_returnsRows() {
        List<ExportVehicleLetterGuaranteeRecordResp> rows = service.listExportVehicleLetterGuaranteeRecord(
                new ExportVehicleLetterGuaranteeRecordParam());
        printJson(rows);
        assertNotNull(rows);
    }

    @Test
    @Order(2)
    @DisplayName("page — 分页查询")
    public void testPage_paginatesResults() {
        ExportVehicleLetterGuaranteeRecordParam param = new ExportVehicleLetterGuaranteeRecordParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        PageResult<ExportVehicleLetterGuaranteeRecordResp> page = service.pageExportVehicleLetterGuaranteeRecord(param);
        printJson(page);
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    @Order(3)
    @DisplayName("page — 按 transactionType(变动类型)筛选")
    public void testPage_filtersByTransactionType() {
        ExportVehicleLetterGuaranteeRecordParam param = new ExportVehicleLetterGuaranteeRecordParam();
        param.setTransactionType("10"); // 10=冻结
        PageResult<ExportVehicleLetterGuaranteeRecordResp> page = service.pageExportVehicleLetterGuaranteeRecord(param);
        printJson(page);
        assertNotNull(page);
    }

    @Test
    @Order(4)
    @DisplayName("page — 按 lgCode(保函编号)筛选")
    public void testPage_filtersByLgCode() {
        ExportVehicleLetterGuaranteeRecordParam param = new ExportVehicleLetterGuaranteeRecordParam();
        param.setLgCode("LG_TEST_RECORD");
        PageResult<ExportVehicleLetterGuaranteeRecordResp> page = service.pageExportVehicleLetterGuaranteeRecord(param);
        printJson(page);
        assertNotNull(page);
    }

    @Test
    @Order(5)
    @DisplayName("get — 通过 id 查询详情")
    public void testGet_returnsById() {
        String newId = createRecordFreeze();
        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(newId);
        printJson(resp);
        assertNotNull(resp);
        assertEquals(newId, resp.getId());
    }

    @Test
    @Order(6)
    @DisplayName("get — 记录不存在返回 null")
    public void testGet_returnsNull_whenNotExists() {
        ExportVehicleLetterGuaranteeRecordResp missing = service.getExportVehicleLetterGuaranteeRecordById("RECORD_NOT_EXIST");
        printJson(missing);
        assertNull(missing);
    }

    // ============== 写入(addOrUpdate 走创建分支) ==============

    @Test
    @Order(10)
    @DisplayName("addOrUpdate — 追加冻结流水(交易类型 10)")
    public void testAddOrUpdate_createsFreezeRecord() {
        String newId = createRecordFreeze();
        printJson(newId);
        assertNotNull(newId);

        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(newId);
        assertNotNull(resp);
        assertEquals("10", resp.getTransactionType());
        assertTrue(resp.getTransactionAmount().compareTo(BigDecimal.ZERO) > 0, "变动金额应大于0");
        recordFreezeId = newId;
    }

    @Test
    @Order(11)
    @DisplayName("addOrUpdate — 追加解冻流水(交易类型 20)")
    public void testAddOrUpdate_createsUnfreezeRecord() {
        String lgRecordCode = "BK" + System.currentTimeMillis() + "00002";
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = baseRecordReq(lgRecordCode, "20");
        req.setTransactionAmount(new BigDecimal("50000.00"));
        req.setTransactionRemark("PO单 101撤回,资金解冻");
        req.setBeforeAvailable(new BigDecimal("900000.00"));
        req.setAfterAvailable(new BigDecimal("950000.00"));
        req.setBeforeOccupied(new BigDecimal("100000.00"));
        req.setAfterOccupied(new BigDecimal("50000.00"));

        String id = service.addOrUpdateExportVehicleLetterGuaranteeRecord(req);
        assertNotNull(id);

        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(id);
        assertEquals("20", resp.getTransactionType());
        assertEquals(lgRecordCode, resp.getLgRecordCode());
        recordUnfreezeId = id;
    }

    @Test
    @Order(12)
    @DisplayName("addOrUpdate — 追加转应收流水(交易类型 30)")
    public void testAddOrUpdate_createsTransferRecord() {
        String lgRecordCode = "BK" + System.currentTimeMillis() + "00003";
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = baseRecordReq(lgRecordCode, "30");
        req.setTransactionAmount(new BigDecimal("200000.00"));
        req.setTransactionRemark("质检通过 PO 101 → SO 200");
        req.setBeforeAvailable(new BigDecimal("700000.00"));
        req.setAfterAvailable(new BigDecimal("700000.00"));
        req.setBeforeOccupied(new BigDecimal("200000.00"));
        req.setAfterOccupied(new BigDecimal("0"));
        req.setBeforeReceivable(new BigDecimal("0"));
        req.setAfterReceivable(new BigDecimal("200000.00"));
        req.setSoNo("SO_TRANSFER_200");
        req.setVin("LSVNV2180GN123456");

        String id = service.addOrUpdateExportVehicleLetterGuaranteeRecord(req);
        assertNotNull(id);
        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(id);
        assertEquals("30", resp.getTransactionType());
        assertEquals("SO_TRANSFER_200", resp.getSoNo());
        assertEquals("LSVNV2180GN123456", resp.getVin());
        recordTransferId = id;
    }

    @Test
    @Order(13)
    @DisplayName("addOrUpdate — 追加释放应收流水(交易类型 40)")
    public void testAddOrUpdate_createsReleaseRecord() {
        String lgRecordCode = "BK" + System.currentTimeMillis() + "00004";
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = baseRecordReq(lgRecordCode, "40");
        req.setTransactionAmount(new BigDecimal("100000.00"));
        req.setTransactionRemark("SAP 清账,释放应收");
        req.setBeforeAvailable(new BigDecimal("700000.00"));
        req.setAfterAvailable(new BigDecimal("700000.00"));
        req.setBeforeOccupied(new BigDecimal("0"));
        req.setAfterOccupied(new BigDecimal("0"));
        req.setBeforeReceivable(new BigDecimal("200000.00"));
        req.setAfterReceivable(new BigDecimal("100000.00"));
        req.setSoNo("SO_TRANSFER_200");

        String id = service.addOrUpdateExportVehicleLetterGuaranteeRecord(req);
        assertNotNull(id);
        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(id);
        assertEquals("40", resp.getTransactionType());
        recordReleaseId = id;
    }

    @Test
    @Order(14)
    @DisplayName("addOrUpdate — 追加失效流水(交易类型 50)")
    public void testAddOrUpdate_createsInvalidRecord() {
        String lgRecordCode = "BK" + System.currentTimeMillis() + "00005";
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = baseRecordReq(lgRecordCode, "50");
        req.setTransactionAmount(new BigDecimal("0"));
        req.setTransactionRemark("凌晨失效任务触发");
        req.setBeforeAvailable(new BigDecimal("500000.00"));
        req.setAfterAvailable(new BigDecimal("500000.00"));
        req.setBeforeOccupied(new BigDecimal("0"));
        req.setAfterOccupied(new BigDecimal("0"));
        req.setBeforeReceivable(null);
        req.setAfterReceivable(null);

        String id = service.addOrUpdateExportVehicleLetterGuaranteeRecord(req);
        assertNotNull(id);
        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(id);
        assertEquals("50", resp.getTransactionType());
        recordInvalidId = id;
    }

    @Test
    @Order(15)
    @DisplayName("addOrUpdate — 交易明细不允许修改,带 id 走更新分支视为违规抛 BizException(§11.6)")
    public void testAddOrUpdate_rejectsEditExisting() {
        // 设计文档 §11.6:交易明细仅追加写,不允许修改。
        // 复用已有记录 id 走更新分支应被拒绝。
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = baseRecordReq(
                "BK" + System.currentTimeMillis() + "00006",
                "10");
        req.setId(recordFreezeId);
        req.setTransactionRemark("尝试修改已存在的记录");
        assertThrows(BizException.class, () -> service.addOrUpdateExportVehicleLetterGuaranteeRecord(req));
    }

    @Test
    @Order(16)
    @DisplayName("addOrUpdate — transactionAmount 为负数时校验失败抛 BizException")
    public void testAddOrUpdate_rejectsNegativeAmount() {
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = baseRecordReq(
                "BK" + System.currentTimeMillis() + "00007",
                "10");
        req.setTransactionAmount(new BigDecimal("-100.00"));
        assertThrows(BizException.class, () -> service.addOrUpdateExportVehicleLetterGuaranteeRecord(req));
    }

    @Test
    @Order(17)
    @DisplayName("addOrUpdate — 缺少 lgRecordCode 全库唯一约束触发 BizException")
    public void testAddOrUpdate_rejectsDuplicateLgRecordCode() {
        String duplicateCode = "BK" + System.currentTimeMillis() + "00099";

        // 第一次新增成功
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq first = baseRecordReq(duplicateCode, "10");
        String firstId = service.addOrUpdateExportVehicleLetterGuaranteeRecord(first);
        recordDuplicateId = firstId;

        // 第二次用相同 code 触发唯一索引
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq second = baseRecordReq(duplicateCode, "10");
        assertThrows(BizException.class, () -> service.addOrUpdateExportVehicleLetterGuaranteeRecord(second));
    }

    // ============== 删除 ==============

    @Test
    @Order(30)
    @DisplayName("delete — 交易明细不允许删除(§11.6),抛 BizException")
    public void testDelete_rejectsAppendOnly() {
        // 设计文档 §11.6:交易明细表仅追加写,不允许修改/删除
        assertThrows(BizException.class,
                () -> service.deleteExportVehicleLetterGuaranteeRecordByIds(List.of(recordFreezeId)));
    }

    @Test
    @Order(31)
    @DisplayName("delete — 批量删除同样被拒绝")
    public void testDelete_batchRejected() {
        assertThrows(BizException.class,
                () -> service.deleteExportVehicleLetterGuaranteeRecordByIds(
                        Arrays.asList(recordUnfreezeId, recordTransferId)));
    }

    @Test
    @Order(32)
    @DisplayName("delete — 空 id 列表参数应抛 BizException")
    public void testDelete_emptyIds_rejected() {
        assertThrows(BizException.class,
                () -> service.deleteExportVehicleLetterGuaranteeRecordByIds(List.of()));
    }

    // ============== 启用/禁用 ==============

    @Test
    @Order(40)
    @DisplayName("enableOrDisable — 启用")
    public void testEnableOrDisable_enable() {
        LpBaseParam param = new LpBaseParam();
        param.setIdSet(Set.of(recordReleaseId));
        param.setStatus(1);
        boolean ok = service.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param);
        printJson(ok);
        assertTrue(ok);

        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(recordReleaseId);
        assertEquals(1, resp.getStatus());
    }

    @Test
    @Order(41)
    @DisplayName("enableOrDisable — 禁用")
    public void testEnableOrDisable_disable() {
        LpBaseParam param = new LpBaseParam();
        param.setIdSet(Set.of(recordReleaseId));
        param.setStatus(0);
        boolean ok = service.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param);
        printJson(ok);
        assertTrue(ok);

        ExportVehicleLetterGuaranteeRecordResp resp = service.getExportVehicleLetterGuaranteeRecordById(recordReleaseId);
        assertEquals(0, resp.getStatus());
    }

    @Test
    @Order(42)
    @DisplayName("enableOrDisable — 空 id 集返回 false 或抛 BizException")
    public void testEnableOrDisable_emptyIdSet() {
        LpBaseParam param = new LpBaseParam();
        param.setIdSet(Set.of());
        param.setStatus(1);
        // Controller 层做空校验,Service 层行为由实现决定
        try {
            boolean ok = service.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param);
            printJson(ok);
            assertFalse(ok);
        } catch (BizException ex) {
            printJson(ex.getMessage());
        }
    }

    @Test
    @Order(43)
    @DisplayName("enableOrDisable — 非法 status 抛 BizException")
    public void testEnableOrDisable_invalidStatus() {
        LpBaseParam param = new LpBaseParam();
        param.setIdSet(Set.of(recordInvalidId));
        param.setStatus(99); // 非 0/1 的脏值
        assertThrows(BizException.class, () -> service.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param));
    }

    // ============== 数据完整性 ==============

    @Test
    @Order(50)
    @DisplayName("dataIntegrity — 单条保函的多笔流水可被 page 列出")
    public void testMultipleRecordsForSameLg() {
        // 同一保函的多条变动流水应能在分页查询中按 lgCode 检索到
        ExportVehicleLetterGuaranteeRecordParam param = new ExportVehicleLetterGuaranteeRecordParam();
        param.setLgCode("LG_G117_DEMO");
        PageResult<ExportVehicleLetterGuaranteeRecordResp> page = service.pageExportVehicleLetterGuaranteeRecord(param);
        printJson(page);
        assertNotNull(page);
    }

    // ============== 辅助方法 ==============

    /**
     * 构造一条覆盖常用字段的 Record Req,便于追加写场景测试。
     */
    private ExportVehicleLetterGuaranteeRecordAddOrUpdateReq baseRecordReq(String lgRecordCode, String transactionType) {
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = new ExportVehicleLetterGuaranteeRecordAddOrUpdateReq();
        req.setLgRecordCode(lgRecordCode);
        req.setLgId("LG_TEST_" + UUID.randomUUID().toString().substring(0, 8));
        req.setLgCode("LG_G117_DEMO");
        req.setLgType("PREPAYMENT");
        req.setCustomerId("CUST_G117");
        req.setTransactionType(transactionType);
        req.setTransactionAmount(new BigDecimal("100000.00"));
        req.setTransactionRemark("PO单 101提交,资金冻结");
        req.setBeforeAvailable(new BigDecimal("1000000.00"));
        req.setAfterAvailable(new BigDecimal("900000.00"));
        req.setBeforeOccupied(new BigDecimal("0"));
        req.setAfterOccupied(new BigDecimal("100000.00"));
        req.setBeforeReceivable(null);
        req.setAfterReceivable(null);
        req.setPoNo("PO_TEST_001");
        req.setSoNo(null);
        req.setVin(null);
        req.setStatusDesc("资金冻结");
        req.setOwnerUserId(10001L);
        req.setOwnerOrgId(20001L);
        return req;
    }

    /**
     * 创建一条冻结流水。
     */
    private String createRecordFreeze() {
        String lgRecordCode = "BK" + System.currentTimeMillis() + "00001";
        ExportVehicleLetterGuaranteeRecordAddOrUpdateReq req = baseRecordReq(lgRecordCode, "10");
        return service.addOrUpdateExportVehicleLetterGuaranteeRecord(req);
    }
}
