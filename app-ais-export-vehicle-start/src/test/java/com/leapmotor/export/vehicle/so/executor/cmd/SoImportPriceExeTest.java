package com.leapmotor.export.vehicle.so.executor.cmd;

import com.alibaba.fastjson.JSONObject;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.pricechangerecord.gateway.PriceChangeRecordGateway;
import com.leapmotor.export.vehicle.so.dto.SoImportPriceDto;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.paltform.ais.common.model.excel.in.ExcelBaseResult;
import com.leapmotor.paltform.ais.common.model.excel.in.ImportExcelDTO;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.user.LoginUser;
import com.leapmotor.paltform.ais.common.util.LoginUserUtils;
import com.leapmotor.platform.ais.common.redis.BlockLock;
import com.leapmotor.platform.ais.common.redis.DistributedLockTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("SO订单导入最新价格执行器单元测试")
@ExtendWith(MockitoExtension.class)
public class SoImportPriceExeTest {

    @Mock
    private VehicleCbuSoGateway vehicleCbuSoGateway;
    @Mock
    private VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;
    @Mock
    private PayableListDetailGateway payableListDetailGateway;
    @Mock
    private PayableListGateway payableListGateway;
    @Mock
    private PriceChangeRecordGateway priceChangeRecordGateway;
    @Mock
    private DistributedLockTemplate distributedLockTemplate;
    @Mock
    private TransactionTemplate transactionTemplate;

    private SoImportPriceExe executor;

    @BeforeEach
    void setUp() {
        executor = new SoImportPriceExe(
                vehicleCbuSoGateway,
                vehicleCbuSoDetailGateway,
                payableListDetailGateway,
                payableListGateway,
                priceChangeRecordGateway,
                distributedLockTemplate,
                transactionTemplate
        );
    }

    // ==================== Context 链式校验测试 ====================

    @Nested
    @DisplayName("SoImportPriceContext 链式校验")
    class ContextValidationTests {

        @Test
        @DisplayName("checkSoExists — SO存在时通过并设置so引用")
        void checkSoExists_soFound_passes() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("100"));
            Map<String, VehicleCbuSoDo> soNoMap = new HashMap<>();
            soNoMap.put("SO001", buildSo("SO001", VehicleCbuSoStatusEnum.NOT_ISSUED.getCode()));

            ctx.checkSoExists(soNoMap);

            assertTrue(ctx.isValid());
            assertNotNull(ctx.getSo());
        }

        @Test
        @DisplayName("checkSoExists — SO不存在时失败")
        void checkSoExists_soNotFound_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO99", new BigDecimal("100"));
            Map<String, VehicleCbuSoDo> soNoMap = new HashMap<>();
            soNoMap.put("SO001", buildSo("SO001", VehicleCbuSoStatusEnum.NOT_ISSUED.getCode()));

            ctx.checkSoExists(soNoMap);

            assertFalse(ctx.isValid());
            assertEquals("SO订单不存在或状态为已收款、已作废，请确认", ctx.getFailReason());
        }

        @Test
        @DisplayName("checkOrderStatus — 已收款状态阻断")
        void checkOrderStatus_received_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("100"));
            Map<String, VehicleCbuSoDo> soNoMap = new HashMap<>();
            soNoMap.put("SO001", buildSo("SO001", VehicleCbuSoStatusEnum.RECEIVED.getCode()));

            ctx.checkSoExists(soNoMap).checkOrderStatus();

            assertFalse(ctx.isValid());
            assertEquals("SO订单不存在或状态为已收款、已作废，请确认", ctx.getFailReason());
        }

        @Test
        @DisplayName("checkOrderStatus — 已作废状态阻断")
        void checkOrderStatus_cancelled_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO01", new BigDecimal("100"));
            Map<String, VehicleCbuSoDo> soNoMap = new HashMap<>();
            soNoMap.put("SO001", buildSo("SO001", VehicleCbuSoStatusEnum.CANCELLED.getCode()));

            ctx.checkSoExists(soNoMap).checkOrderStatus();

            assertFalse(ctx.isValid());
        }

        @Test
        @DisplayName("checkOrderStatus — 未下发生产状态通过")
        void checkOrderStatus_notIssued_passes() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("100"));
            Map<String, VehicleCbuSoDo> soNoMap = new HashMap<>();
            soNoMap.put("SO001", buildSo("SO001", VehicleCbuSoStatusEnum.NOT_ISSUED.getCode()));

            ctx.checkSoExists(soNoMap).checkOrderStatus();

            assertTrue(ctx.isValid());
        }

        @Test
        @DisplayName("checkOrderStatus — 已下发生产状态通过")
        void checkOrderStatus_issued_passes() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("100"));
            Map<String, VehicleCbuSoDo> soNoMap = new HashMap<>();
            soNoMap.put("SO001", buildSo("SO001", VehicleCbuSoStatusEnum.ISSUED.getCode()));

            ctx.checkSoExists(soNoMap).checkOrderStatus();

            assertTrue(ctx.isValid());
        }

        @Test
        @DisplayName("checkPrice — 价格为null时失败")
        void checkPrice_null_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", null);

            ctx.checkPrice();

            assertFalse(ctx.isValid());
            assertEquals("车辆价格必须大于0", ctx.getFailReason());
        }

        @Test
        @DisplayName("checkPrice — 价格为0时失败")
        void checkPrice_zero_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", BigDecimal.ZERO);

            ctx.checkPrice();

            assertFalse(ctx.isValid());
            assertEquals("车辆价格必须大于0", ctx.getFailReason());
        }

        @Test
        @DisplayName("checkPrice — 价格为负数时失败")
        void checkPrice_negative_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("-100"));

            ctx.checkPrice();

            assertFalse(ctx.isValid());
            assertEquals("车辆价格必须大于0", ctx.getFailReason());
        }

        @Test
        @DisplayName("checkPrice — 价格超过两位小数时失败")
        void checkPrice_tooManyDecimals_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO01", new BigDecimal("100.123"));

            ctx.checkPrice();

            assertFalse(ctx.isValid());
            assertEquals("车辆价格最多两位小数", ctx.getFailReason());
        }

        @Test
        @DisplayName("checkPrice — 价格超过上限时失败")
        void checkPrice_exceedsMax_fails() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("10000000000000000.00"));

            ctx.checkPrice();

            assertFalse(ctx.isValid());
            assertEquals("车辆价格超出允许的最大值", ctx.getFailReason());
        }

        @Test
        @DisplayName("checkPrice — 合法价格通过")
        void checkPrice_valid_passes() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("100.12"));

            ctx.checkPrice();

            assertTrue(ctx.isValid());
        }

        @Test
        @DisplayName("checkPrice — 整数价格通过")
        void checkPrice_integer_passes() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO001", new BigDecimal("100"));

            ctx.checkPrice();

            assertTrue(ctx.isValid());
        }

        @Test
        @DisplayName("链式校验短路 — 前置失败后不再执行后续校验")
        void chain_shortCircuit_skipsSubsequentChecks() {
            SoImportPriceExe.SoImportPriceContext ctx = buildContext("SO999", null);
            Map<String, VehicleCbuSoDo> soNoMap = new HashMap<>();

            ctx.checkSoExists(soNoMap).checkOrderStatus().checkPrice();

            assertFalse(ctx.isValid());
            assertEquals("SO订单不存在或状态为已收款、已作废，请确认", ctx.getFailReason());
        }
    }

    // ==================== execute 边界测试 ====================

    @Nested
    @DisplayName("execute 边界场景")
    class ExecuteBoundaryTests {

        @Test
        @DisplayName("DTO为null时返回0/0")
        void execute_nullDto_returnsZeros() {
            ExcelBaseResult result = executor.execute(null);

            assertEquals(0, result.getSuccessNum());
            assertEquals(0, result.getFailNum());
        }

        @Test
        @DisplayName("Excel为空字符串时返回0/0")
        void execute_emptyExcel_returnsZeros() {
            ImportExcelDTO dto = new ImportExcelDTO();
            dto.setExcel("");

            ExcelBaseResult result = executor.execute(dto);

            assertEquals(0, result.getSuccessNum());
            assertEquals(0, result.getFailNum());
        }

        @Test
        @DisplayName("Excel解析为空列表时返回0/0")
        void execute_emptyList_returnsZeros() {
            ImportExcelDTO dto = new ImportExcelDTO();
            dto.setExcel("[]");

            ExcelBaseResult result = executor.execute(dto);

            assertEquals(0, result.getSuccessNum());
            assertEquals(0, result.getFailNum());
        }

        @Test
        @DisplayName("导入行数超过上限 — 抛出业务异常")
        void execute_rowLimitExceeded_throwsBizException() {
            List<SoImportPriceDto> items = new ArrayList<>();
            for (int i = 0; i < 5001; i++) {
                items.add(buildImportDto("SO" + i, new BigDecimal("100")));
            }

            ImportExcelDTO dto = new ImportExcelDTO();
            dto.setExcel(JSONObject.toJSONString(items));

            assertThrows(BizException.class, () -> executor.execute(dto));
            verify(vehicleCbuSoGateway, never()).listVehicleCbuSo(any());
        }

        @Test
        @DisplayName("全部校验失败 — 不执行任何更新，失败明细含失败原因")
        void execute_allFailValidation_noUpdates() {
            List<SoImportPriceDto> items = List.of(
                    buildImportDto("SO99", new BigDecimal("100"))
            );

            when(vehicleCbuSoGateway.listVehicleCbuSo(any(VehicleCbuSoQo.class)))
                    .thenReturn(Collections.emptyList());

            ImportExcelDTO dto = new ImportExcelDTO();
            dto.setExcel(JSONObject.toJSONString(items));
            ExcelBaseResult result = executor.execute(dto);

            assertEquals(0, result.getSuccessNum());
            assertEquals(1, result.getFailNum());
            verify(vehicleCbuSoGateway, never()).updateBatchById(any());
            verify(priceChangeRecordGateway, never()).addBatchPriceChangeRecord(any());
            verify(transactionTemplate, never()).execute(any(TransactionCallback.class));

            @SuppressWarnings("unchecked")
            List<SoImportPriceDto> data = (List<SoImportPriceDto>) result.getData();
            assertNotNull(data);
            assertEquals(1, data.size());
            assertEquals("SO99", data.get(0).getSoNo());
            assertEquals("SO订单不存在或状态为已收款、已作废，请确认", data.get(0).getFailReason());
        }
    }

    // ==================== execute 流程测试 ====================

    @Nested
    @DisplayName("execute 导入流程（价格未变场景，避开NoGenerator静态依赖）")
    class ExecuteFlowTests {

        @Test
        @DisplayName("价格未变化 — 稀疏更新SO价格且不生成变更记录")
        void execute_priceUnchanged_sparseUpdateNoChangeRecord() {
            VehicleCbuSoDo so = buildSo("SO001", VehicleCbuSoStatusEnum.NOT_ISSUED.getCode());
            so.setId("SO-ID-001");
            so.setVehiclePrice(new BigDecimal("100.00"));

            when(vehicleCbuSoGateway.listVehicleCbuSo(any(VehicleCbuSoQo.class)))
                    .thenReturn(List.of(so));
            when(vehicleCbuSoDetailGateway.listVehicleCbuSoDetail(any(VehicleCbuSoDetailQo.class)))
                    .thenReturn(Collections.emptyList());
            when(payableListDetailGateway.listBySoNos(any()))
                    .thenReturn(Collections.emptyList());

            try (MockedStatic<LoginUserUtils> loginMock = mockLoginUser()) {
                BlockLock blockLock = mockLock();
                mockTransactionExecution();

                ImportExcelDTO dto = new ImportExcelDTO();
                dto.setExcel(JSONObject.toJSONString(List.of(buildImportDto("SO001", new BigDecimal("100.00")))));

                ExcelBaseResult result = executor.execute(dto);

                assertEquals(1, result.getSuccessNum());
                assertEquals(0, result.getFailNum());

                ArgumentCaptor<List<VehicleCbuSoDo>> soCaptor = ArgumentCaptor.forClass(List.class);
                verify(vehicleCbuSoGateway).updateBatchById(soCaptor.capture());
                assertEquals(1, soCaptor.getValue().size());
                assertEquals("SO-ID-001", soCaptor.getValue().get(0).getId());
                assertEquals(new BigDecimal("100.00"), soCaptor.getValue().get(0).getVehiclePrice());
                assertNull(soCaptor.getValue().get(0).getOrderStatus(), "稀疏更新不应携带状态列");

                verify(priceChangeRecordGateway, never()).addBatchPriceChangeRecord(any());
                verify(payableListDetailGateway, never()).updateBatchById(any());
                verify(payableListGateway, never()).updateBatchById(any());

                // 锁释放必须晚于事务提交（事务模板先于锁close执行）
                InOrder inOrder = inOrder(transactionTemplate, blockLock);
                inOrder.verify(transactionTemplate).execute(any(TransactionCallback.class));
                inOrder.verify(blockLock).close();
            }
        }

        @Test
        @DisplayName("SO无关联应付清单 — 不触发清单查询与更新")
        void execute_noPayableDetail_skipsPayableListOps() {
            VehicleCbuSoDo so = buildSo("SO001", VehicleCbuSoStatusEnum.NOT_ISSUED.getCode());
            so.setId("SO-ID-001");
            so.setVehiclePrice(new BigDecimal("100.00"));

            when(vehicleCbuSoGateway.listVehicleCbuSo(any(VehicleCbuSoQo.class)))
                    .thenReturn(List.of(so));
            when(vehicleCbuSoDetailGateway.listVehicleCbuSoDetail(any(VehicleCbuSoDetailQo.class)))
                    .thenReturn(Collections.emptyList());
            when(payableListDetailGateway.listBySoNos(any()))
                    .thenReturn(Collections.emptyList());

            try (MockedStatic<LoginUserUtils> loginMock = mockLoginUser()) {
                mockLock();
                mockTransactionExecution();

                ImportExcelDTO dto = new ImportExcelDTO();
                dto.setExcel(JSONObject.toJSONString(List.of(buildImportDto("SO001", new BigDecimal("100.00")))));

                ExcelBaseResult result = executor.execute(dto);

                assertEquals(1, result.getSuccessNum());
                verify(payableListGateway, never()).listByIds(any());
                verify(payableListDetailGateway, never()).sumAmountByPayableListId(anyString());
            }
        }
    }

    // 注意：涉及价格变化的execute全流程测试（生成变更记录、联动应付清单明细、总额重算）
    // 因NoGenerator静态初始化依赖Spring上下文，需通过@SpringBootTest集成测试覆盖。

    // ==================== 辅助方法 ====================

    private SoImportPriceExe.SoImportPriceContext buildContext(String soNo, BigDecimal price) {
        SoImportPriceExe.SoImportPriceContext ctx = new SoImportPriceExe.SoImportPriceContext();
        ctx.setSoNo(soNo);
        ctx.setPrice(price);
        return ctx;
    }

    private VehicleCbuSoDo buildSo(String soNo, Integer status) {
        VehicleCbuSoDo so = new VehicleCbuSoDo();
        so.setSoNo(soNo);
        so.setOrderStatus(status);
        return so;
    }

    private SoImportPriceDto buildImportDto(String soNo, BigDecimal price) {
        SoImportPriceDto dto = new SoImportPriceDto();
        dto.setSoNo(soNo);
        dto.setPrice(price);
        return dto;
    }

    private MockedStatic<LoginUserUtils> mockLoginUser() {
        MockedStatic<LoginUserUtils> mock = mockStatic(LoginUserUtils.class);
        LoginUser user = new LoginUser();
        user.setUserId(1L);
        mock.when(LoginUserUtils::getCurrentUserOrSysDefault).thenReturn(user);
        return mock;
    }

    private BlockLock mockLock() {
        BlockLock blockLock = mock(BlockLock.class);
        when(distributedLockTemplate.blockLock(anyString(), anyLong(), anyLong()))
                .thenReturn(blockLock);
        return blockLock;
    }

    private void mockTransactionExecution() {
        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        }).when(transactionTemplate).execute(any());
    }
}
