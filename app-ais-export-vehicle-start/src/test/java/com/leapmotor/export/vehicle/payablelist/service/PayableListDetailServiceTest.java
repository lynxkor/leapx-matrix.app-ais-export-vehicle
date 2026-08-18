// package com.leapmotor.export.vehicle.payablelist.service;
//
// import com.leapmotor.export.vehicle.base.BaseServiceTest;
// import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
// import com.leapmotor.export.vehicle.payablelist.resp.PayableListDetailResp;
// import com.leapmotor.paltform.ais.common.model.response.PageResult;
// import org.junit.jupiter.api.*;
// import org.springframework.util.CollectionUtils;
//
// import java.time.LocalDateTime;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// /**
//  * 应付清单明细 Service 集成测试
//  *
//  * @author 赵吴涯
//  * @since 2026-08-07
//  */
// @DisplayName("应付清单明细 Service 集成测试")
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// public class PayableListDetailServiceTest extends BaseServiceTest<PayableListDetailService> {
//
//     private static String testPayableListId;
//
//     @Test
//     @Order(1)
//     @DisplayName("分页查询 — 正常查询应返回分页结果")
//     public void testPagePayableListDetail_normalQuery() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result, "分页结果不应为 null");
//         assertNotNull(result.getRecords(), "记录列表不应为 null");
//         assertTrue(result.getRecords().size() <= param.getPageSize(), "返回记录数应不超过分页大小");
//
//         // 如果有数据，记录第一个明细对应的应付清单ID，供后续测试使用
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             testPayableListId = result.getRecords().get(0).getPayableListId();
//         }
//     }
//
//     @Test
//     @Order(2)
//     @DisplayName("分页查询 — 按应付清单ID查询")
//     public void testPagePayableListDetail_queryByPayableListId() {
//         if (testPayableListId == null) {
//             // 没有数据，跳过
//             return;
//         }
//
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(20);
//         param.setPayableListId(testPayableListId);
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             result.getRecords().forEach(detail -> {
//                 assertEquals(
//                         testPayableListId,
//                         detail.getPayableListId(),
//                         "返回明细的应付清单ID应与查询参数一致"
//                 );
//                 assertNotNull(detail.getId(), "明细ID不应为 null");
//                 assertNotNull(detail.getVin(), "VIN号不应为 null");
//                 assertNotNull(detail.getSoNo(), "SO订单号不应为 null");
//             });
//         }
//     }
//
//     @Test
//     @Order(3)
//     @DisplayName("分页查询 — 按SO订单号查询")
//     public void testPagePayableListDetail_queryBySoNo() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//         param.setSoNo("SO202607");
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             result.getRecords().forEach(detail -> {
//                 assertTrue(
//                         detail.getSoNo() != null && detail.getSoNo().contains("SO202607"),
//                         "返回明细的SO订单号应包含查询关键字"
//                 );
//             });
//         }
//     }
//
//     @Test
//     @Order(4)
//     @DisplayName("分页查询 — 按PO单号查询")
//     public void testPagePayableListDetail_queryByPoNo() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//         param.setPoNo("PO202607");
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             result.getRecords().forEach(detail -> {
//                 assertTrue(
//                         detail.getPoNo() != null && detail.getPoNo().contains("PO202607"),
//                         "返回明细的PO单号应包含查询关键字"
//                 );
//             });
//         }
//     }
//
//     @Test
//     @Order(5)
//     @DisplayName("分页查询 — 按VIN号查询")
//     public void testPagePayableListDetail_queryByVin() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//         param.setVin("LSVAA2182N2123456");
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             result.getRecords().forEach(detail -> {
//                 assertTrue(
//                         detail.getVin() != null && detail.getVin().contains("LSVAA2182N2123456"),
//                         "返回明细的VIN号应包含查询关键字"
//                 );
//             });
//         }
//     }
//
//     @Test
//     @Order(6)
//     @DisplayName("分页查询 — 按是否收款查询")
//     public void testPagePayableListDetail_queryByIsReceived() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//         param.setIsReceived(0); // 未收款
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             result.getRecords().forEach(detail -> {
//                 assertEquals(
//                         0,
//                         detail.getIsReceived(),
//                         "返回明细的收款状态应为未收款"
//                 );
//             });
//         }
//     }
//
//     @Test
//     @Order(7)
//     @DisplayName("分页查询 — 按质检通过时间范围查询")
//     public void testPagePayableListDetail_queryByQcPassTime() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//         param.setQcPassTimeStart(LocalDateTime.now().minusMonths(1));
//         param.setQcPassTimeEnd(LocalDateTime.now());
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             result.getRecords().forEach(detail -> {
//                 if (detail.getQcPassTime() != null) {
//                     assertTrue(
//                             detail.getQcPassTime().isAfter(param.getQcPassTimeStart().minusSeconds(1)) &&
//                                     detail.getQcPassTime().isBefore(param.getQcPassTimeEnd().plusSeconds(1)),
//                             "质检通过时间应在查询范围内"
//                     );
//                 }
//             });
//         }
//     }
//
//     @Test
//     @Order(8)
//     @DisplayName("分页查询 — 多条件组合查询")
//     public void testPagePayableListDetail_multipleConditions() {
//         if (testPayableListId == null) {
//             return;
//         }
//
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(20);
//         param.setPayableListId(testPayableListId);
//         param.setIsReceived(0);
//         param.setQcPassTimeStart(LocalDateTime.now().minusMonths(3));
//         param.setQcPassTimeEnd(LocalDateTime.now());
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             result.getRecords().forEach(detail -> {
//                 assertEquals(testPayableListId, detail.getPayableListId(), "应付清单ID应匹配");
//                 assertEquals(0, detail.getIsReceived(), "收款状态应为未收款");
//             });
//         }
//     }
//
//     @Test
//     @Order(9)
//     @DisplayName("分页查询 — 大数据量分页")
//     public void testPagePayableListDetail_largePage() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(100); // 测试较大分页
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         assertNotNull(result.getRecords());
//         assertTrue(result.getRecords().size() <= 100, "返回记录数不应超过分页大小");
//     }
//
//     @Test
//     @Order(10)
//     @DisplayName("分页查询 — 第二页数据")
//     public void testPagePayableListDetail_secondPage() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(2);
//         param.setPageSize(10);
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         // 验证分页逻辑正常，不抛异常即可
//     }
//
//     @Test
//     @Order(11)
//     @DisplayName("分页查询 — 空查询条件")
//     public void testPagePayableListDetail_emptyConditions() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//         // 不设置任何查询条件
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//         assertNotNull(result.getRecords());
//         // 验证无查询条件时不抛异常
//     }
//
//     @Test
//     @Order(12)
//     @DisplayName("验证明细数据完整性")
//     public void testPayableListDetail_dataIntegrity() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(5);
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         if (!CollectionUtils.isEmpty(result.getRecords())) {
//             PayableListDetailResp detail = result.getRecords().get(0);
//
//             // 验证关键字段不为 null
//             assertNotNull(detail.getId(), "ID不应为 null");
//             assertNotNull(detail.getPayableListId(), "应付清单ID不应为 null");
//             assertNotNull(detail.getDetailNo(), "明细编号不应为 null");
//             assertNotNull(detail.getSoNo(), "SO订单号不应为 null");
//             assertNotNull(detail.getVin(), "VIN号不应为 null");
//             assertNotNull(detail.getMaterialCode(), "物料编码不应为 null");
//
//             // 验证枚举字段值合法
//             if (detail.getIsReceived() != null) {
//                 assertTrue(
//                         detail.getIsReceived() == 0 || detail.getIsReceived() == 1,
//                         "收款状态应为 0(否) 或 1(是)"
//                 );
//             }
//
//             // 验证价格字段
//             if (detail.getPrice() != null) {
//                 assertTrue(
//                         detail.getPrice().compareTo(java.math.BigDecimal.ZERO) >= 0,
//                         "价格应 >= 0"
//                 );
//             }
//         }
//     }
//
//     @Test
//     @Order(13)
//     @DisplayName("分页查询 — 验证分页统计信息")
//     public void testPagePayableListDetail_pageStatistics() {
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(10);
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//
//         // 验证分页统计字段
//         assertNotNull(result.getTotal(), "总记录数不应为 null");
//         assertTrue(result.getTotal() >= 0, "总记录数应 >= 0");
//
//         if (result.getTotal() > 0) {
//             // 计算总页数
//             int expectedPages = (int) Math.ceil((double) result.getTotal() / param.getPageSize());
//             assertTrue(expectedPages >= 1, "总页数应 >= 1");
//         }
//     }
//
//     @Test
//     @Order(14)
//     @DisplayName("分页查询 — 验证排序")
//     public void testPagePayableListDetail_sorting() {
//         if (testPayableListId == null) {
//             return;
//         }
//
//         PayableListDetailPageParam param = new PayableListDetailPageParam();
//         param.setPageNum(1);
//         param.setPageSize(20);
//         param.setPayableListId(testPayableListId);
//
//         PageResult<PayableListDetailResp> result = service.pagePayableListDetail(param);
//
//         printJson(result);
//         assertNotNull(result);
//
//         if (!CollectionUtils.isEmpty(result.getRecords()) && result.getRecords().size() > 1) {
//             // 验证按创建时间倒序排列
//             for (int i = 0; i < result.getRecords().size() - 1; i++) {
//                 PayableListDetailResp current = result.getRecords().get(i);
//                 PayableListDetailResp next = result.getRecords().get(i + 1);
//
//                 if (current.getCreateTime() != null && next.getCreateTime() != null) {
//                     assertTrue(
//                             current.getCreateTime().isAfter(next.getCreateTime()) ||
//                                     current.getCreateTime().equals(next.getCreateTime()),
//                             "明细应按创建时间倒序排列"
//                     );
//                 }
//             }
//         }
//     }
// }
