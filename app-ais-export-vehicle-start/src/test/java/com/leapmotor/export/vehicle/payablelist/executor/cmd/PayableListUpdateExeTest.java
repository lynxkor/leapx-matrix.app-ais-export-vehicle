package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListUpdateReq;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 应付清单基础信息更新执行器单元测试
 *
 * <p>聚焦抬头编辑后的明细冗余字段同步：客户编码/名称、生成月份、提交日期、确认时间
 * 必须与抬头保持一致；未传字段以 null 透传给网关，由网关侧保证明细原值不变。
 *
 * @author huangtao
 * @since 2026-09-11
 */
@DisplayName("应付清单基础信息更新执行器单元测试")
@ExtendWith(MockitoExtension.class)
public class PayableListUpdateExeTest {

    @Mock
    private PayableListGateway payableListGateway;
    @Mock
    private PayableListDetailGateway payableListDetailGateway;

    private PayableListUpdateExe executor;

    private static final String PAYABLE_LIST_ID = "PL-001";
    private static final Integer VERSION = 1;

    @BeforeEach
    public void setUp() {
        executor = new PayableListUpdateExe(payableListGateway, payableListDetailGateway);
    }

    @Test
    @DisplayName("编辑成功 — 抬头字段更新且明细冗余字段同步一致")
    public void testExecute_syncRedundantFields() {
        LocalDateTime submitTime = LocalDateTime.of(2026, 9, 10, 9, 0, 0);
        LocalDateTime confirmTime = LocalDateTime.of(2026, 9, 11, 10, 30, 0);
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildDb(PayableListStatusEnum.DRAFT.getCode()));

        executor.execute(buildReq(submitTime, confirmTime));

        // 抬头更新：字段透传 + 版本号由后端自增
        ArgumentCaptor<PayableListDo> headerCaptor = ArgumentCaptor.forClass(PayableListDo.class);
        verify(payableListGateway).updatePayableList(headerCaptor.capture());
        PayableListDo updated = headerCaptor.getValue();
        assertEquals("CUST-001", updated.getCustomerCode(), "抬头客户编码应更新");
        assertEquals("某客户", updated.getCustomerName(), "抬头客户名称应更新");
        assertEquals("EXT-001", updated.getExternalCustomerCode(), "抬头外部客户编码应更新");
        assertEquals("2026-08", updated.getGenerateMonth(), "抬头生成月份应更新");
        assertEquals(submitTime, updated.getSubmitTime(), "抬头提交时间应更新");
        assertEquals(confirmTime, updated.getConfirmTime(), "抬头确认时间应更新");
        assertEquals(Integer.valueOf(VERSION + 1), updated.getVersion(), "版本号应由后端自增");

        // 明细冗余字段同步：与抬头保持一致
        ArgumentCaptor<PayableListDetailDo> detailCaptor = ArgumentCaptor.forClass(PayableListDetailDo.class);
        verify(payableListDetailGateway).updateRedundantFieldsByPayableListId(
                eq(PAYABLE_LIST_ID), detailCaptor.capture());
        PayableListDetailDo synced = detailCaptor.getValue();
        assertEquals("CUST-001", synced.getCustomerCode(), "明细客户编码应同步");
        assertEquals("某客户", synced.getCustomerName(), "明细客户名称应同步");
        assertEquals("2026-08", synced.getGenerateMonth(), "明细生成月份应同步");
        assertEquals(submitTime, synced.getSubmitDate(), "明细提交日期应同步抬头提交时间");
        assertEquals(confirmTime, synced.getConfirmTime(), "明细确认时间应同步");
    }

    @Test
    @DisplayName("编辑成功 — 未传的冗余字段以 null 透传，由网关保证明细原值不变")
    public void testExecute_nullFieldsPassThrough() {
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildDb(PayableListStatusEnum.REJECTED.getCode()));

        executor.execute(buildReq(null, null));

        ArgumentCaptor<PayableListDetailDo> detailCaptor = ArgumentCaptor.forClass(PayableListDetailDo.class);
        verify(payableListDetailGateway).updateRedundantFieldsByPayableListId(
                eq(PAYABLE_LIST_ID), detailCaptor.capture());
        PayableListDetailDo synced = detailCaptor.getValue();
        assertEquals("CUST-001", synced.getCustomerCode(), "客户编码仍应同步");
        assertNull(synced.getSubmitDate(), "未传提交时间时明细提交日期透传 null");
        assertNull(synced.getConfirmTime(), "未传确认时间时明细确认时间透传 null");
    }

    @Test
    @DisplayName("非草稿/驳回状态 — 拒绝编辑且不做任何更新")
    public void testExecute_notEditable() {
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildDb(PayableListStatusEnum.SUBMITTED.getCode()));

        BizException exception = assertThrows(BizException.class,
                () -> executor.execute(buildReq(null, null)));
        assertEquals("仅草稿/驳回状态的单据可编辑", exception.getMessage());
        verify(payableListGateway, never()).updatePayableList(any());
        verifyNoInteractions(payableListDetailGateway);
    }

    @Test
    @DisplayName("版本号不匹配 — 乐观锁校验失败且不做任何更新")
    public void testExecute_versionConflict() {
        when(payableListGateway.getPayableListById(PAYABLE_LIST_ID))
                .thenReturn(buildDb(PayableListStatusEnum.DRAFT.getCode()));
        PayableListUpdateReq req = buildReq(null, null);
        req.setVersion(99);

        BizException exception = assertThrows(BizException.class, () -> executor.execute(req));
        assertEquals("数据已被他人修改，请刷新后重试", exception.getMessage());
        verify(payableListGateway, never()).updatePayableList(any());
        verifyNoInteractions(payableListDetailGateway);
    }

    private PayableListDo buildDb(String billStatus) {
        PayableListDo db = new PayableListDo();
        db.setId(PAYABLE_LIST_ID);
        db.setPayableListNo("YF202608000001");
        db.setBillStatus(billStatus);
        db.setVersion(VERSION);
        return db;
    }

    private PayableListUpdateReq buildReq(LocalDateTime submitTime, LocalDateTime confirmTime) {
        PayableListUpdateReq req = new PayableListUpdateReq();
        req.setId(PAYABLE_LIST_ID);
        req.setVersion(VERSION);
        req.setCustomerCode("CUST-001");
        req.setCustomerName("某客户");
        req.setExternalCustomerCode("EXT-001");
        req.setGenerateMonth("2026-08");
        req.setTotalVehicles(10);
        req.setTotalAmount(new BigDecimal("500000"));
        req.setSubmitTime(submitTime);
        req.setConfirmTime(confirmTime);
        req.setConfirmRemark("确认无误");
        return req;
    }
}
