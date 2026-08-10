package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.base.BaseServiceTest;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.*;
import com.leapmotor.export.vehicle.payablelist.resp.ExcelBaseResult;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListResp;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.junit.jupiter.api.*;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应付清单管理 Service 集成测试
 *
 * @author 赵吴涯
 * @since 2026-08-07
 */
@DisplayName("应付清单管理 Service 集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PayableListServiceTest extends BaseServiceTest<PayableListService> {

    private static String testPayableListId;
    private static String testPayableListNo;

    @Test
    @Order(1)
    @DisplayName("分页查询 — 正常查询应返回分页结果")
    public void testPagePayableList_normalQuery() {
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setGenerateMonth("202607");

        PageResult<PayableListResp> result = service.pagePayableList(param);

        printJson(result);
        assertNotNull(result, "分页结果不应为 null");
        assertNotNull(result.getRecords(), "记录列表不应为 null");
        assertTrue(result.getRecords().size() <= param.getPageSize(), "返回记录数应不超过分页大小");
    }

    @Test
    @Order(2)
    @DisplayName("分页查询 — 多条件组合查询")
    public void testPagePayableList_multipleConditions() {
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(20);
        param.setBillStatusList(Arrays.asList(
                PayableListStatusEnum.DRAFT.getCode(),
                PayableListStatusEnum.SUBMITTED.getCode()
        ));
        param.setCreateTimeStart(LocalDateTime.now().minusMonths(1));
        param.setCreateTimeEnd(LocalDateTime.now());

        PageResult<PayableListResp> result = service.pagePayableList(param);

        printJson(result);
        assertNotNull(result);
        if (!CollectionUtils.isEmpty(result.getRecords())) {
            result.getRecords().forEach(resp -> {
                assertNotNull(resp.getId(), "记录ID不应为 null");
                assertNotNull(resp.getPayableListNo(), "应付清单编号不应为 null");
                assertTrue(
                        param.getBillStatusList().contains(resp.getBillStatus()),
                        "返回记录的单据状态应在查询条件范围内"
                );
            });
        }
    }

    @Test
    @Order(3)
    @DisplayName("根据ID查询 — 正常查询应返回详情")
    public void testGetPayableListById_normal() {
        // 先通过分页查询获取一条记录
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(1);

        PageResult<PayableListResp> pageResult = service.pagePayableList(param);
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            // 如果没有数据，跳过此测试
            return;
        }

        String existingId = pageResult.getRecords().get(0).getId();
        PayableListResp result = service.getPayableListById(existingId);

        printJson(result);
        assertNotNull(result, "查询结果不应为 null");
        assertEquals(existingId, result.getId(), "返回的ID应与查询参数一致");
        assertNotNull(result.getPayableListNo(), "应付清单编号不应为 null");
        assertNotNull(result.getBillStatus(), "单据状态不应为 null");
    }

    @Test
    @Order(4)
    @DisplayName("根据ID查询 — 不存在的ID应抛出业务异常")
    public void testGetPayableListById_notExists() {
        String notExistId = UUID.randomUUID().toString();

        BizException exception = assertThrows(
                BizException.class,
                () -> service.getPayableListById(notExistId),
                "查询不存在的应付清单应抛出业务异常"
        );

        assertTrue(
                exception.getMessage().contains("应付清单不存在"),
                "异常消息应包含 '应付清单不存在'"
        );
    }

    @Test
    @Order(5)
    @DisplayName("提交应付清单 — 正常提交")
    public void testSubmitPayableList_normal() {
        // 获取一条草稿状态的应付清单
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setBillStatusList(Arrays.asList(PayableListStatusEnum.DRAFT.getCode()));

        PageResult<PayableListResp> pageResult = service.pagePayableList(param);
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            // 如果没有草稿状态的记录，跳过此测试
            return;
        }

        PayableListResp draftRecord = pageResult.getRecords().get(0);
        testPayableListId = draftRecord.getId();
        testPayableListNo = draftRecord.getPayableListNo();

        PayableListSubmitReq req = new PayableListSubmitReq();
        req.setId(testPayableListId);
        req.setVersion(draftRecord.getVersion());

        String result = service.submitPayableList(req);

        printJson(result);
        assertNotNull(result, "返回结果不应为 null");
        assertEquals(testPayableListId, result, "返回的ID应与提交的ID一致");

        // 验证状态已变更
        PayableListResp submitted = service.getPayableListById(testPayableListId);
        assertEquals(
                PayableListStatusEnum.SUBMITTED.getCode(),
                submitted.getBillStatus(),
                "提交后状态应为已提交"
        );
        assertNotNull(submitted.getSubmitTime(), "提交时间不应为 null");
        assertEquals(draftRecord.getVersion() + 1, submitted.getVersion(), "版本号应递增1");
    }

    @Test
    @Order(6)
    @DisplayName("提交应付清单 — 版本号不匹配应抛出异常")
    public void testSubmitPayableList_versionMismatch() {
        if (testPayableListId == null) {
            return; // 前置测试未执行，跳过
        }

        PayableListSubmitReq req = new PayableListSubmitReq();
        req.setId(testPayableListId);
        req.setVersion(0); // 错误的版本号

        assertThrows(
                BizException.class,
                () -> service.submitPayableList(req),
                "版本号不匹配应抛出业务异常"
        );
    }

    @Test
    @Order(7)
    @DisplayName("提交应付清单 — 非草稿状态提交应抛出异常")
    public void testSubmitPayableList_invalidStatus() {
        if (testPayableListId == null) {
            return;
        }

        PayableListResp submitted = service.getPayableListById(testPayableListId);
        PayableListSubmitReq req = new PayableListSubmitReq();
        req.setId(testPayableListId);
        req.setVersion(submitted.getVersion());

        assertThrows(
                BizException.class,
                () -> service.submitPayableList(req),
                "非草稿状态提交应抛出业务异常"
        );
    }

    @Test
    @Order(8)
    @DisplayName("导入最新价格 — 正常导入")
    public void testImportPrice_normal() {
        PayableListImportPriceReq req = new PayableListImportPriceReq();
        req.setPayableListId(testPayableListId);
        // 构造导入数据
        List<Object> importData = new ArrayList<>();
        req.setImportData(importData);

        ExcelBaseResult result = service.importPrice(req);

        printJson(result);
        // 验证导入结果结构
        if (result != null) {
            assertNotNull(result.getSuccessCount(), "成功数量不应为 null");
            assertNotNull(result.getFailCount(), "失败数量不应为 null");
            assertTrue(result.getSuccessCount() >= 0, "成功数量应 >= 0");
            assertTrue(result.getFailCount() >= 0, "失败数量应 >= 0");
        }
    }

    @Test
    @Order(9)
    @DisplayName("删除应付清单 — 正常删除")
    public void testDeletePayableListByIds_normal() {
        // 先查询一条草稿状态的记录用于删除
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setBillStatusList(Arrays.asList(PayableListStatusEnum.DRAFT.getCode()));

        PageResult<PayableListResp> pageResult = service.pagePayableList(param);
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            // 没有可删除的记录，跳过
            return;
        }

        String idToDelete = pageResult.getRecords().get(0).getId();
        List<String> ids = Arrays.asList(idToDelete);

        boolean result = service.deletePayableListByIds(ids);

        printJson(result);
        assertTrue(result, "删除应返回 true");

        // 验证已删除（查询应抛异常）
        assertThrows(
                BizException.class,
                () -> service.getPayableListById(idToDelete),
                "删除后查询应抛出业务异常"
        );
    }

    @Test
    @Order(10)
    @DisplayName("删除应付清单 — 空列表应返回 false")
    public void testDeletePayableListByIds_emptyList() {
        boolean result = service.deletePayableListByIds(new ArrayList<>());

        printJson(result);
        assertFalse(result, "空列表删除应返回 false");
    }

    @Test
    @Order(11)
    @DisplayName("删除应付清单 — null列表应返回 false")
    public void testDeletePayableListByIds_nullList() {
        boolean result = service.deletePayableListByIds(null);

        printJson(result);
        assertFalse(result, "null列表删除应返回 false");
    }

    @Test
    @Order(12)
    @DisplayName("分页查询 — 按应付清单编号模糊查询")
    public void testPagePayableList_searchByNo() {
        if (testPayableListNo == null) {
            return;
        }

        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setPayableListNo(testPayableListNo.substring(0, 6)); // 使用前缀模糊查询

        PageResult<PayableListResp> result = service.pagePayableList(param);

        printJson(result);
        assertNotNull(result);
        if (!CollectionUtils.isEmpty(result.getRecords())) {
            boolean found = result.getRecords().stream()
                    .anyMatch(r -> r.getPayableListNo().contains(testPayableListNo.substring(0, 6)));
            assertTrue(found, "查询结果应包含匹配的记录");
        }
    }

    @Test
    @Order(13)
    @DisplayName("分页查询 — 按客户名称模糊查询")
    public void testPagePayableList_searchByCustomerName() {
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setCustomerName("测试");

        PageResult<PayableListResp> result = service.pagePayableList(param);

        printJson(result);
        assertNotNull(result);
        if (!CollectionUtils.isEmpty(result.getRecords())) {
            result.getRecords().forEach(r -> {
                assertTrue(
                        r.getCustomerName() != null && r.getCustomerName().contains("测试"),
                        "返回记录的客户名称应包含查询关键字"
                );
            });
        }
    }

    @Test
    @Order(14)
    @DisplayName("分页查询 — 按VIN查询")
    public void testPagePayableList_searchByVin() {
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setVin("LSVAA2182N2123456");

        PageResult<PayableListResp> result = service.pagePayableList(param);

        printJson(result);
        assertNotNull(result);
        // VIN 查询关联明细表，验证不抛异常即可
    }

    @Test
    @Order(15)
    @DisplayName("分页查询 — 按提交时间范围查询")
    public void testPagePayableList_searchBySubmitTime() {
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setSubmitTimeStart(LocalDateTime.now().minusDays(7));
        param.setSubmitTimeEnd(LocalDateTime.now());

        PageResult<PayableListResp> result = service.pagePayableList(param);

        printJson(result);
        assertNotNull(result);
        if (!CollectionUtils.isEmpty(result.getRecords())) {
            result.getRecords().forEach(r -> {
                if (r.getSubmitTime() != null) {
                    assertTrue(
                            r.getSubmitTime().isAfter(param.getSubmitTimeStart().minusSeconds(1)) &&
                                    r.getSubmitTime().isBefore(param.getSubmitTimeEnd().plusSeconds(1)),
                            "提交时间应在查询范围内"
                    );
                }
            });
        }
    }

    @Test
    @Order(16)
    @DisplayName("分页查询 — 按确认时间范围查询")
    public void testPagePayableList_searchByConfirmTime() {
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setConfirmTimeStart(LocalDateTime.now().minusMonths(1));
        param.setConfirmTimeEnd(LocalDateTime.now());

        PageResult<PayableListResp> result = service.pagePayableList(param);

        printJson(result);
        assertNotNull(result);
        if (!CollectionUtils.isEmpty(result.getRecords())) {
            result.getRecords().forEach(r -> {
                if (r.getConfirmTime() != null) {
                    assertTrue(
                            r.getConfirmTime().isAfter(param.getConfirmTimeStart().minusSeconds(1)) &&
                                    r.getConfirmTime().isBefore(param.getConfirmTimeEnd().plusSeconds(1)),
                            "确认时间应在查询范围内"
                    );
                }
            });
        }
    }

    @Test
    @Order(17)
    @DisplayName("删除应付清单 — 批量删除")
    public void testDeletePayableListByIds_batch() {
        // 查询多条草稿状态的记录
        PayableListPageParam param = new PayableListPageParam();
        param.setPageNum(1);
        param.setPageSize(10);
        param.setBillStatusList(Arrays.asList(PayableListStatusEnum.DRAFT.getCode()));

        PageResult<PayableListResp> pageResult = service.pagePayableList(param);
        if (CollectionUtils.isEmpty(pageResult.getRecords()) || pageResult.getRecords().size() < 2) {
            return; // 记录不足，跳过
        }

        List<String> idsToDelete = Arrays.asList(
                pageResult.getRecords().get(0).getId(),
                pageResult.getRecords().get(1).getId()
        );

        boolean result = service.deletePayableListByIds(idsToDelete);

        printJson(result);
        assertTrue(result, "批量删除应返回 true");

        // 验证已删除
        idsToDelete.forEach(id -> {
            assertThrows(
                    BizException.class,
                    () -> service.getPayableListById(id),
                    "删除后查询应抛出业务异常"
            );
        });
    }
}
