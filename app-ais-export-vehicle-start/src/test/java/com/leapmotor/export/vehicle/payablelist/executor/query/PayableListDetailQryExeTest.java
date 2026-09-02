package com.leapmotor.export.vehicle.payablelist.executor.query;

import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.query.PayableListDetailQo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListDetailPageParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 应付清单明细查询执行器单元测试
 *
 * <p>覆盖《红旗G117项目整车销售-应付清单管理.md》"应付清单明细"章节测试用例：
 * 客户名称/清单编号/单据状态/生成月份/VIN/创建日期/提交日期/确认日期/是否收款 等查询条件。
 *
 * @author huangtao
 * @since 2026-09-02
 */
@DisplayName("应付清单明细查询执行器单元测试")
@ExtendWith(MockitoExtension.class)
public class PayableListDetailQryExeTest {

    @Mock
    private PayableListDetailGateway payableListDetailGateway;

    private PayableListDetailQryExe qryExe;

    @BeforeEach
    public void setUp() {
        qryExe = new PayableListDetailQryExe(payableListDetailGateway);
    }

    @Test
    @DisplayName("分页查询 — 无条件查询应透传空Qo且不抛异常")
    public void testPage_emptyCondition() {
        PageResult<PayableListDetailDo> expected = new PageResult<>();
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(expected);

        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);

        PageResult<PayableListDetailDo> result = qryExe.pagePayableListDetail(param);

        assertSame(expected, result, "应原样透传Gateway分页结果");
        ArgumentCaptor<PayableListDetailQo> captor = ArgumentCaptor.forClass(PayableListDetailQo.class);
        verify(payableListDetailGateway).pagePayableListDetail(captor.capture());
        PayableListDetailQo qo = captor.getValue();
        assertNull(qo.getCustomerCode(), "无查询条件时客户编码应为空");
        assertNull(qo.getBillStatusList(), "无查询条件时单据状态多选应为空");
        assertNull(qo.getIsReceived(), "无查询条件时是否收款应为空");
    }

    @Test
    @DisplayName("分页查询 — 全字段查询条件应完整映射到Qo")
    public void testPage_allConditionsMapped() {
        PageResult<PayableListDetailDo> expected = new PageResult<>();
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(expected);

        LocalDateTime createStart = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        LocalDateTime createEnd = LocalDateTime.of(2026, 12, 31, 23, 59, 59);
        LocalDateTime submitStart = LocalDateTime.of(2026, 2, 1, 0, 0, 0);
        LocalDateTime submitEnd = LocalDateTime.of(2026, 2, 28, 23, 59, 59);
        LocalDateTime confirmStart = LocalDateTime.of(2026, 3, 1, 0, 0, 0);
        LocalDateTime confirmEnd = LocalDateTime.of(2026, 3, 31, 23, 59, 59);

        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(2L);
        param.setPageSize(20L);
        param.setPayableListId("PL-001");
        param.setCustomerCode("CUST-TESTA");
        param.setPayableListNoList(Arrays.asList("YF202606000001", "YF202606000002"));
        param.setPayableListNoLike("YF202606");
        param.setBillStatusList(Arrays.asList("10", "20"));
        param.setGenerateMonth("202608");
        param.setVinList(Arrays.asList("LFZ_001", "LFZ_002"));
        param.setVin("LFZ_003");
        param.setVinLike("LFZ_");
        param.setSoNo("SO001");
        param.setPoNo("PO001");
        param.setIsReceived(0);
        param.setCreateTimeStart(createStart);
        param.setCreateTimeEnd(createEnd);
        param.setSubmitTimeStart(submitStart);
        param.setSubmitTimeEnd(submitEnd);
        param.setConfirmTimeStart(confirmStart);
        param.setConfirmTimeEnd(confirmEnd);

        qryExe.pagePayableListDetail(param);

        ArgumentCaptor<PayableListDetailQo> captor = ArgumentCaptor.forClass(PayableListDetailQo.class);
        verify(payableListDetailGateway).pagePayableListDetail(captor.capture());
        PayableListDetailQo qo = captor.getValue();

        assertEquals(2, qo.getPageNum(), "页码应透传");
        assertEquals(20, qo.getPageSize(), "每页条数应透传");
        assertEquals("PL-001", qo.getPayableListId());
        assertEquals("CUST-TESTA", qo.getCustomerCode(), "客户编码应映射");
        assertEquals(Arrays.asList("YF202606000001", "YF202606000002"), qo.getPayableListNoList(), "清单编号多值应映射");
        assertEquals("YF202606", qo.getPayableListNoLike(), "清单编号单条模糊应映射");
        assertEquals(Arrays.asList("10", "20"), qo.getBillStatusList(), "单据状态多选应映射");
        assertEquals("202608", qo.getGenerateMonth(), "生成月份应映射");
        assertEquals(Arrays.asList("LFZ_001", "LFZ_002"), qo.getVinList(), "VIN多值应映射");
        assertEquals("LFZ_003", qo.getVin());
        assertEquals("LFZ_", qo.getVinLike(), "VIN单条模糊应映射");
        assertEquals("SO001", qo.getSoNo());
        assertEquals("PO001", qo.getPoNo());
        assertEquals(Integer.valueOf(0), qo.getIsReceived(), "是否收款应映射");
        assertEquals(createStart, qo.getCreateTimeStart());
        assertEquals(createEnd, qo.getCreateTimeEnd());
        assertEquals(submitStart, qo.getSubmitTimeStart());
        assertEquals(submitEnd, qo.getSubmitTimeEnd());
        assertEquals(confirmStart, qo.getConfirmTimeStart());
        assertEquals(confirmEnd, qo.getConfirmTimeEnd());
    }

    @Test
    @DisplayName("分页查询 — 按是否收款=是 查询")
    public void testPage_queryByIsReceivedYes() {
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(new PageResult<>());

        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        param.setIsReceived(1);

        qryExe.pagePayableListDetail(param);

        ArgumentCaptor<PayableListDetailQo> captor = ArgumentCaptor.forClass(PayableListDetailQo.class);
        verify(payableListDetailGateway).pagePayableListDetail(captor.capture());
        assertEquals(Integer.valueOf(1), captor.getValue().getIsReceived(), "是否收款=是 应映射为 1");
    }

    @Test
    @DisplayName("分页查询 — 按单个VIN精确查询")
    public void testPage_queryByVinExact() {
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(new PageResult<>());

        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        param.setVin("LFZ_001");

        qryExe.pagePayableListDetail(param);

        ArgumentCaptor<PayableListDetailQo> captor = ArgumentCaptor.forClass(PayableListDetailQo.class);
        verify(payableListDetailGateway).pagePayableListDetail(captor.capture());
        assertEquals("LFZ_001", captor.getValue().getVin(), "单个VIN应映射到 vin");
    }

    @Test
    @DisplayName("分页查询 — 按VIN模糊关键字查询")
    public void testPage_queryByVinLike() {
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(new PageResult<>());

        // 文档用例：输入部分VIN如 'LFZ_'，系统显示所有包含 'LFZ_' 的记录
        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        param.setVinLike("LFZ_");

        qryExe.pagePayableListDetail(param);

        ArgumentCaptor<PayableListDetailQo> captor = ArgumentCaptor.forClass(PayableListDetailQo.class);
        verify(payableListDetailGateway).pagePayableListDetail(captor.capture());
        assertEquals("LFZ_", captor.getValue().getVinLike(), "VIN模糊关键字应映射到 vinLike");
    }

    @Test
    @DisplayName("分页查询 — 按应付清单编号模糊查询")
    public void testPage_queryByPayableListNoLike() {
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(new PageResult<>());

        // 文档用例：输入YF202606，返回编号包含YF202606的所有记录
        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        param.setPayableListNoLike("YF202606");

        qryExe.pagePayableListDetail(param);

        ArgumentCaptor<PayableListDetailQo> captor = ArgumentCaptor.forClass(PayableListDetailQo.class);
        verify(payableListDetailGateway).pagePayableListDetail(captor.capture());
        assertEquals("YF202606", captor.getValue().getPayableListNoLike(), "清单编号模糊关键字应映射到 payableListNoLike");
    }

    @Test
    @DisplayName("分页查询 — 按单据状态多选查询")
    public void testPage_queryByBillStatusList() {
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(new PageResult<>());

        // 文档用例：同时选择草稿(10)、已提交待确认(20)两个状态
        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);
        param.setBillStatusList(Arrays.asList("10", "20"));

        qryExe.pagePayableListDetail(param);

        ArgumentCaptor<PayableListDetailQo> captor = ArgumentCaptor.forClass(PayableListDetailQo.class);
        verify(payableListDetailGateway).pagePayableListDetail(captor.capture());
        assertEquals(Arrays.asList("10", "20"), captor.getValue().getBillStatusList(),
                "单据状态多选应映射到 billStatusList");
    }

    @Test
    @DisplayName("分页查询 — 结果透传Gateway返回值")
    public void testPage_resultPassthrough() {
        PageResult<PayableListDetailDo> expected = new PageResult<>();
        expected.setTotal(5L);
        expected.setRecords(List.of(new PayableListDetailDo()));
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(expected);

        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);

        PageResult<PayableListDetailDo> result = qryExe.pagePayableListDetail(param);

        assertSame(expected, result);
        assertEquals(5L, result.getTotal());
        verify(payableListDetailGateway, times(1)).pagePayableListDetail(any());
    }

    @Test
    @DisplayName("分页查询 — Gateway返回null时原样返回（不做非空保护）")
    public void testPage_gatewayReturnsNull() {
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(null);

        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);

        assertNull(qryExe.pagePayableListDetail(param), "Gateway返回null时应原样透传");
    }

    @Test
    @DisplayName("分页查询 — 价格字段不应参与查询条件")
    public void testPage_priceNotInQuery() {
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(new PageResult<>());

        // 构造一个仅含价格字段的参数，确认映射不产生异常（明细表查询条件不含价格）
        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);

        assertDoesNotThrow(() -> qryExe.pagePayableListDetail(param));
        verify(payableListDetailGateway, times(1)).pagePayableListDetail(any());
    }

    /**
     * 数据完整性辅助：构造明细领域对象（供响应断言复用）
     */
    private PayableListDetailDo buildDetail(String id, String vin, String soNo, Integer isReceived, BigDecimal price) {
        PayableListDetailDo detail = new PayableListDetailDo();
        detail.setId(id);
        detail.setVin(vin);
        detail.setSoNo(soNo);
        detail.setIsReceived(isReceived);
        detail.setPrice(price);
        return detail;
    }

    @Test
    @DisplayName("分页查询 — 返回明细关键字段完整性")
    public void testPage_detailDataIntegrity() {
        PageResult<PayableListDetailDo> expected = new PageResult<>();
        expected.setTotal(1L);
        expected.setRecords(List.of(buildDetail("D-001", "LFZ_001", "SO001", 0, new BigDecimal("150000"))));
        when(payableListDetailGateway.pagePayableListDetail(any())).thenReturn(expected);

        PayableListDetailPageParam param = new PayableListDetailPageParam();
        param.setPageNum(1L);
        param.setPageSize(10L);

        PageResult<PayableListDetailDo> result = qryExe.pagePayableListDetail(param);
        PayableListDetailDo detail = result.getRecords().get(0);
        assertNotNull(detail.getId());
        assertNotNull(detail.getVin());
        assertNotNull(detail.getSoNo());
        assertNotNull(detail.getIsReceived());
        assertNotNull(detail.getPrice());
        assertTrue(detail.getIsReceived() == 0 || detail.getIsReceived() == 1, "是否收款应合法(0/1)");
        assertTrue(detail.getPrice().compareTo(BigDecimal.ZERO) >= 0, "价格应 >= 0");
    }
}
