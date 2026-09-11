package com.leapmotor.export.vehicle.infrastructure.payablelist.gatewayimpl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.leapmotor.export.vehicle.infrastructure.payablelist.dao.PayableListDao;
import com.leapmotor.export.vehicle.infrastructure.payablelist.entity.PayableList;
import com.leapmotor.export.vehicle.infrastructure.payablelist.mapper.PayableListMapper;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

/**
 * 应付清单抬头领域网关实现单元测试
 *
 * <p>聚焦清账场景的状态单调前进更新：更新语句必须带"当前状态编码小于目标状态"的条件，
 * 避免并发下已收款(60)被后算出的部分收款(50)覆盖。
 *
 * @author huangtao
 * @since 2026-09-11
 */
@DisplayName("PayableListGatewayImpl 状态前进更新单元测试")
@ExtendWith(MockitoExtension.class)
public class PayableListGatewayImplTest {

    @Mock
    private PayableListMapper payableListMapper;
    @Mock
    private PayableListDao payableListDao;

    private PayableListGatewayImpl gateway;

    private static final String PAYABLE_LIST_ID = "PL-001";

    @BeforeAll
    public static void initTableInfoCache() {
        // 纯单测无 Spring 上下文，需手动初始化 MyBatis-Plus 表信息缓存，Lambda 列名解析才可用
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                PayableList.class);
    }

    @BeforeEach
    public void setUp() {
        gateway = new PayableListGatewayImpl(payableListMapper, payableListDao);
    }

    @Test
    @DisplayName("状态前进更新 — 只 SET 单据状态，且 WHERE 带低于目标状态的条件")
    public void testAdvanceBillStatus_forwardCondition() {
        when(payableListMapper.update(isNull(), any())).thenReturn(1);

        int rows = gateway.advanceBillStatus(PAYABLE_LIST_ID, PayableListStatusEnum.RECEIVED.getCode());

        assertEquals(1, rows, "应透传影响行数");
        LambdaUpdateWrapper<PayableList> wrapper = captureUpdateWrapper();
        String sqlSet = wrapper.getSqlSet();
        assertEquals("bill_status=#{ew.paramNameValuePairs.MPGENVAL1}", sqlSet,
                "SET 子句只能包含单据状态，不得全量回写其它列");
        String sqlSegment = wrapper.getSqlSegment();
        assertTrue(sqlSegment.contains("id"), "WHERE 应包含主键条件");
        assertTrue(sqlSegment.contains("bill_status"), "WHERE 应包含当前状态小于目标状态的条件");
        assertTrue(wrapper.getParamNameValuePairs().containsValue(PAYABLE_LIST_ID), "主键参数应传入");
        assertTrue(wrapper.getParamNameValuePairs().containsValue(PayableListStatusEnum.RECEIVED.getCode()),
                "目标状态参数应传入");
    }

    @Test
    @DisplayName("状态未前进 — 条件不满足时影响行数为 0，状态不更新")
    public void testAdvanceBillStatus_noForward() {
        when(payableListMapper.update(isNull(), any())).thenReturn(0);

        int rows = gateway.advanceBillStatus(PAYABLE_LIST_ID, PayableListStatusEnum.PARTIAL_RECEIVED.getCode());

        assertEquals(0, rows, "未前进时不应更新（如当前已是已收款60，目标部分收款50）");
        verify(payableListMapper).update(isNull(), any());
    }

    @Test
    @DisplayName("同步红旗结果回写 — 只 SET 同步三列，不回写单据状态与版本号")
    public void testUpdateSyncHqResultById_onlySyncFields() {
        when(payableListMapper.update(isNull(), any())).thenReturn(1);

        int rows = gateway.updateSyncHqResultById(PAYABLE_LIST_ID, LocalDateTime.of(2026, 9, 11, 10, 30, 0),
                "SUCCESS", "{\"code\":200}");

        assertEquals(1, rows, "应透传影响行数");
        LambdaUpdateWrapper<PayableList> wrapper = captureUpdateWrapper();
        String sqlSet = wrapper.getSqlSet();
        assertTrue(sqlSet.contains("sync_hq_time"), "SET 应包含同步时间列");
        assertTrue(sqlSet.contains("sync_hq_result"), "SET 应包含同步结果列");
        assertTrue(sqlSet.contains("sync_hq_message"), "SET 应包含同步报文列");
        assertFalse(sqlSet.contains("bill_status"), "不得回写单据状态（并发下会覆盖已前进的状态）");
        assertFalse(sqlSet.contains("version"), "不得回写版本号");
        assertTrue(wrapper.getSqlSegment().contains("id"), "WHERE 应包含主键条件");
    }

    @Test
    @DisplayName("同步SAP结果回写 — 只 SET 同步三列，不回写单据状态与版本号")
    public void testUpdateSyncSapResultById_onlySyncFields() {
        when(payableListMapper.update(isNull(), any())).thenReturn(1);

        int rows = gateway.updateSyncSapResultById(PAYABLE_LIST_ID, LocalDateTime.of(2026, 9, 11, 10, 30, 0),
                "FAIL", "{\"esRet\":{\"retCode\":\"E\"}}");

        assertEquals(1, rows, "应透传影响行数");
        LambdaUpdateWrapper<PayableList> wrapper = captureUpdateWrapper();
        String sqlSet = wrapper.getSqlSet();
        assertTrue(sqlSet.contains("sync_sap_time"), "SET 应包含同步时间列");
        assertTrue(sqlSet.contains("sync_sap_result"), "SET 应包含同步结果列");
        assertTrue(sqlSet.contains("sync_sap_message"), "SET 应包含同步报文列");
        assertFalse(sqlSet.contains("bill_status"), "不得回写单据状态");
        assertFalse(sqlSet.contains("version"), "不得回写版本号");
    }

    @Test
    @DisplayName("同步结果回写 — 报文为 null 时不写入该列，其余列正常")
    public void testUpdateSyncHqResultById_nullMessageKeepColumn() {
        when(payableListMapper.update(isNull(), any())).thenReturn(1);

        gateway.updateSyncHqResultById(PAYABLE_LIST_ID, LocalDateTime.now(), "SUCCESS", null);

        LambdaUpdateWrapper<PayableList> wrapper = captureUpdateWrapper();
        assertFalse(wrapper.getSqlSet().contains("sync_hq_message"), "报文为 null 时不应写入该列");
        assertTrue(wrapper.getSqlSet().contains("sync_hq_time"), "时间列应正常写入");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private LambdaUpdateWrapper<PayableList> captureUpdateWrapper() {
        ArgumentCaptor<Wrapper> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(payableListMapper).update(isNull(), captor.capture());
        return (LambdaUpdateWrapper<PayableList>) captor.getValue();
    }
}
