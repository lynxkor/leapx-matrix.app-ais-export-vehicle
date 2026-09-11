package com.leapmotor.export.vehicle.infrastructure.payablelist.gatewayimpl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.leapmotor.export.vehicle.infrastructure.payablelist.dao.PayableListDetailDao;
import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableListDetail;
import com.leapmotor.export.vehicle.infrastructure.payablelist.mapper.PayableListDetailMapper;
import com.leapmotor.export.vehicle.payablelist.enums.IsReceivedEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * 应付清单明细网关实现单元测试
 *
 * <p>聚焦明细表冗余字段的回写边界：只更新本次业务实际变更的字段，
 * 入参为 null 的字段不得出现在 SET 子句中（如确认场景不得清空提交日期）。
 *
 * @author huangtao
 * @since 2026-09-11
 */
@DisplayName("PayableListDetailGatewayImpl 冗余字段更新单元测试")
@ExtendWith(MockitoExtension.class)
public class PayableListDetailGatewayImplTest {

    @Mock
    private PayableListDetailMapper payableListDetailMapper;
    @Mock
    private PayableListDetailDao payableListDetailDao;

    private PayableListDetailGatewayImpl gateway;

    private static final String PAYABLE_LIST_ID = "PL-001";

    @BeforeAll
    public static void initTableInfoCache() {
        // 纯单测无 Spring 上下文，需手动初始化 MyBatis-Plus 表信息缓存，Lambda 列名解析才可用
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                PayableListDetail.class);
    }

    @BeforeEach
    public void setUp() {
        gateway = new PayableListDetailGatewayImpl(payableListDetailMapper, payableListDetailDao);
    }

    @Test
    @DisplayName("确认场景 — 只更新单据状态与确认时间，不触碰提交日期")
    public void testUpdateBillStatus_confirmOnly() {
        LocalDateTime confirmTime = LocalDateTime.of(2026, 9, 11, 10, 30, 0);

        gateway.updateBillStatusByPayableListId(PAYABLE_LIST_ID,
                PayableListStatusEnum.CONFIRMED.getCode(), null, confirmTime);

        LambdaUpdateWrapper<PayableListDetail> wrapper = captureUpdateWrapper();
        String sqlSet = wrapper.getSqlSet();
        assertTrue(sqlSet.contains("bill_status"), "应更新单据状态");
        assertTrue(sqlSet.contains("confirm_time"), "应更新确认时间");
        assertFalse(sqlSet.contains("submit_date"), "提交日期应保持原值，不得出现在 SET 子句中");
        assertTrue(wrapper.getSqlSegment().contains("payable_list_id"), "应按应付清单ID定位明细");
    }

    @Test
    @DisplayName("提交场景 — 只更新单据状态与提交日期，不触碰确认时间")
    public void testUpdateBillStatus_submitOnly() {
        LocalDateTime submitDate = LocalDateTime.of(2026, 9, 11, 9, 0, 0);

        gateway.updateBillStatusByPayableListId(PAYABLE_LIST_ID,
                PayableListStatusEnum.SUBMITTED.getCode(), submitDate, null);

        String sqlSet = captureUpdateWrapper().getSqlSet();
        assertTrue(sqlSet.contains("bill_status"), "应更新单据状态");
        assertTrue(sqlSet.contains("submit_date"), "应更新提交日期");
        assertFalse(sqlSet.contains("confirm_time"), "确认时间应保持原值，不得出现在 SET 子句中");
    }

    @Test
    @DisplayName("字段齐全 — 三个冗余字段均进入 SET 子句")
    public void testUpdateBillStatus_allFields() {
        LocalDateTime submitDate = LocalDateTime.of(2026, 9, 11, 9, 0, 0);
        LocalDateTime confirmTime = LocalDateTime.of(2026, 9, 11, 10, 30, 0);

        gateway.updateBillStatusByPayableListId(PAYABLE_LIST_ID,
                PayableListStatusEnum.CONFIRMED.getCode(), submitDate, confirmTime);

        String sqlSet = captureUpdateWrapper().getSqlSet();
        assertTrue(sqlSet.contains("bill_status"), "应更新单据状态");
        assertTrue(sqlSet.contains("submit_date"), "应更新提交日期");
        assertTrue(sqlSet.contains("confirm_time"), "应更新确认时间");
    }

    @Test
    @DisplayName("全部字段为 null — 不执行任何更新并返回 0")
    public void testUpdateBillStatus_allNull() {
        int rows = gateway.updateBillStatusByPayableListId(PAYABLE_LIST_ID, null, null, null);

        assertEquals(0, rows, "无字段可更新时应直接返回 0，避免生成非法 SQL");
        verifyNoInteractions(payableListDetailMapper);
    }

    @Test
    @DisplayName("编辑场景 — 客户信息与生成月份变更同步到明细，未传字段不进入 SET 子句")
    public void testUpdateRedundantFields_customerInfo() {
        PayableListDetailDo redundantFields = new PayableListDetailDo();
        redundantFields.setCustomerCode("CUST-001");
        redundantFields.setCustomerName("某客户");
        redundantFields.setGenerateMonth("2026-08");

        gateway.updateRedundantFieldsByPayableListId(PAYABLE_LIST_ID, redundantFields);

        LambdaUpdateWrapper<PayableListDetail> wrapper = captureUpdateWrapper();
        String sqlSet = wrapper.getSqlSet();
        assertTrue(sqlSet.contains("customer_code"), "应同步客户编码");
        assertTrue(sqlSet.contains("customer_name"), "应同步客户名称");
        assertTrue(sqlSet.contains("generate_month"), "应同步生成月份");
        assertFalse(sqlSet.contains("bill_status"), "单据状态本次不涉及，不得进入 SET 子句");
        assertFalse(sqlSet.contains("submit_date"), "提交日期本次不涉及，不得进入 SET 子句");
        assertFalse(sqlSet.contains("confirm_time"), "确认时间本次不涉及，不得进入 SET 子句");
        assertTrue(wrapper.getSqlSegment().contains("payable_list_id"), "应按应付清单ID定位明细");
    }

    @Test
    @DisplayName("编辑场景 — 提交日期与确认时间变更同步到明细")
    public void testUpdateRedundantFields_submitAndConfirm() {
        LocalDateTime submitDate = LocalDateTime.of(2026, 9, 11, 9, 0, 0);
        LocalDateTime confirmTime = LocalDateTime.of(2026, 9, 11, 10, 30, 0);
        PayableListDetailDo redundantFields = new PayableListDetailDo();
        redundantFields.setSubmitDate(submitDate);
        redundantFields.setConfirmTime(confirmTime);

        gateway.updateRedundantFieldsByPayableListId(PAYABLE_LIST_ID, redundantFields);

        String sqlSet = captureUpdateWrapper().getSqlSet();
        assertTrue(sqlSet.contains("submit_date"), "应同步提交日期");
        assertTrue(sqlSet.contains("confirm_time"), "应同步确认时间");
        assertFalse(sqlSet.contains("customer_code"), "客户编码本次不涉及，不得进入 SET 子句");
    }

    @Test
    @DisplayName("冗余字段入参为 null 或无字段 — 不执行任何更新并返回 0")
    public void testUpdateRedundantFields_allNull() {
        assertEquals(0, gateway.updateRedundantFieldsByPayableListId(PAYABLE_LIST_ID, null),
                "入参为 null 时应直接返回 0");
        assertEquals(0, gateway.updateRedundantFieldsByPayableListId(PAYABLE_LIST_ID, new PayableListDetailDo()),
                "无字段可更新时应直接返回 0，避免生成非法 SQL");
        verifyNoInteractions(payableListDetailMapper);
    }

    @Test
    @DisplayName("收款更新 — 只写 is_received 与 receive_time，不触碰冗余列")
    public void testUpdateReceiveStatusById_onlyReceiveFields() {
        LocalDateTime receiveTime = LocalDateTime.of(2026, 9, 11, 15, 0, 0);

        gateway.updateReceiveStatusById("D-001", IsReceivedEnum.YES.getCode(), receiveTime);

        LambdaUpdateWrapper<PayableListDetail> wrapper = captureUpdateWrapper();
        String sqlSet = wrapper.getSqlSet();
        assertTrue(sqlSet.contains("is_received"), "应更新收款状态");
        assertTrue(sqlSet.contains("receive_time"), "应更新收款时间");
        assertFalse(sqlSet.contains("bill_status"), "单据状态不得被覆盖");
        assertFalse(sqlSet.contains("customer_code"), "客户编码不得被覆盖");
        assertFalse(sqlSet.contains("submit_date"), "提交日期不得被覆盖");
        assertTrue(wrapper.getSqlSegment().contains("id"), "WHERE 应包含明细主键");
    }

    @Test
    @DisplayName("收款更新 — 收款时间为 null 时不写入该字段")
    public void testUpdateReceiveStatusById_nullReceiveTime() {
        gateway.updateReceiveStatusById("D-001", IsReceivedEnum.YES.getCode(), null);

        String sqlSet = captureUpdateWrapper().getSqlSet();
        assertTrue(sqlSet.contains("is_received"), "应更新收款状态");
        assertFalse(sqlSet.contains("receive_time"), "收款时间为空时该字段应保持原值");
    }

    @Test
    @DisplayName("状态前进同步 — 只 SET 单据状态，且 WHERE 带低于目标状态的条件")
    public void testAdvanceBillStatusByPayableListId() {
        gateway.advanceBillStatusByPayableListId(PAYABLE_LIST_ID, PayableListStatusEnum.RECEIVED.getCode());

        LambdaUpdateWrapper<PayableListDetail> wrapper = captureUpdateWrapper();
        String sqlSet = wrapper.getSqlSet();
        assertTrue(sqlSet.contains("bill_status"), "应更新冗余单据状态");
        assertFalse(sqlSet.contains("is_received"), "收款状态不得被覆盖");
        assertFalse(sqlSet.contains("receive_time"), "收款时间不得被覆盖");
        String sqlSegment = wrapper.getSqlSegment();
        assertTrue(sqlSegment.contains("payable_list_id"), "WHERE 应按应付清单ID定位明细");
        assertTrue(sqlSegment.contains("bill_status"), "WHERE 应包含明细状态低于目标状态的条件");
    }

    /**
     * 捕获本次下发的更新包装器
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private LambdaUpdateWrapper<PayableListDetail> captureUpdateWrapper() {
        ArgumentCaptor<Wrapper> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(payableListDetailMapper).update(isNull(), captor.capture());
        return (LambdaUpdateWrapper<PayableListDetail>) captor.getValue();
    }
}
