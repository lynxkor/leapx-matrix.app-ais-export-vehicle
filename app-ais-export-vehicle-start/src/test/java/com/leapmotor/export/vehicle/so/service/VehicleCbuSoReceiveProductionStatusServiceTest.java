package com.leapmotor.export.vehicle.so.service;

import com.leapmotor.export.vehicle.ExportVehicleApplication;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoStatusEnum;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link VehicleCbuSoService#receiveProductionStatus} 集成测试。
 *
 * <p>加载完整 Spring Boot 上下文，真实注入 VehicleCbuSoService，基于 test 环境已存在的 SO 数据
 * 调用 receiveProductionStatus 验证批量 APS 状态回传的状态机流转与返回结果汇总。不 mock 任何依赖。
 *
 * <p>测试数据（test 环境 DB 已存在）：
 * <ul>
 *   <li>SO1: soNo=SO20260720200700132, poNo=HQ20260720000011, 当前状态 1004(焊装上线)</li>
 *   <li>SO2: soNo=SO20260722172500162, poNo=PO-CB-a17d6d69, 当前状态 1003(排产中)</li>
 * </ul>
 *
 * @author lijianuo
 * @since 2026-07-23
 */
@Slf4j
@DisplayName("VehicleCbuSoService#receiveProductionStatus 集成测试")
@TestPropertySource(locations = "file:D:/ais/nacos/ais-config-local.properties")
@SpringBootTest(classes = ExportVehicleApplication.class, properties = "spring.profiles.active=dev")
public class VehicleCbuSoReceiveProductionStatusServiceTest {

    /**
     * SO1: 当前 1004，回传状态5 -> 1007
     */
    private static final String SO_NO_1 = "SO20260720200700132";
    private static final String PO_NO_1 = "HQ20260720000011";

    /**
     * SO2: 当前 1003，回传状态5 -> 1007
     */
    private static final String SO_NO_2 = "SO20260722172500162";
    private static final String PO_NO_2 = "PO-CB-a17d6d69";

    @Resource
    private VehicleCbuSoService service;

    @Test
    @DisplayName("receiveProductionStatus - 批量回传多 SO 状态流转，返回成功/失败汇总")
    public void testReceiveProductionStatus_batch() {
        // SO1(1004) -> 状态5(1007)；SO2(1003) -> 状态5(1007)，均触发红旗同步节点
        VehicleCbuSoReceiveStatusReq req1 = buildStatusReq(SO_NO_1, 5, "VIN_TEST_1");
        req1.setAssemblyInTime(LocalDateTime.now().minusHours(2));
        req1.setAssemblyOutTime(LocalDateTime.now());
        VehicleCbuSoReceiveStatusReq req2 = buildStatusReq(SO_NO_2, 5, "VIN_TEST_2");
        req2.setAssemblyInTime(LocalDateTime.now().minusHours(2));
        req2.setAssemblyOutTime(LocalDateTime.now());

        VehicleCbuSoOperateResult result = service.receiveProductionStatus(List.of(req1, req2));

        assertNotNull(result);
        log.info("receiveProductionStatus 结果: {}", result);
        assertEquals(2, result.getTotalCount(), "总数应为 2");
        assertEquals(2, result.getSuccessCount(), "2 条均成功");
        assertEquals(0, result.getFailCount(), "无失败");

        assertEquals(VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode(),
                getSoBySoNo(PO_NO_1, SO_NO_1).getOrderStatus(), "SO1 应流转到总装下线(1007)");
        assertEquals(VehicleCbuSoStatusEnum.ASSEMBLY_OUT.getCode(),
                getSoBySoNo(PO_NO_2, SO_NO_2).getOrderStatus(), "SO2 应流转到总装下线(1007)");
    }

    @Test
    @DisplayName("receiveProductionStatus - 含无效状态码，成功/失败分别汇总")
    public void testReceiveProductionStatus_partialFail() {
        // SO1 状态5 成功；SO2 状态99 无效失败
        VehicleCbuSoReceiveStatusReq okReq = buildStatusReq(SO_NO_1, 5, "VIN_TEST_1");
        okReq.setAssemblyInTime(LocalDateTime.now().minusHours(2));
        okReq.setAssemblyOutTime(LocalDateTime.now());
        VehicleCbuSoReceiveStatusReq badReq = buildStatusReq(SO_NO_2, 99, null);

        VehicleCbuSoOperateResult result = service.receiveProductionStatus(List.of(okReq, badReq));

        assertEquals(2, result.getTotalCount());
        assertEquals(1, result.getSuccessCount(), "状态5 成功");
        assertEquals(1, result.getFailCount(), "无效状态码 99 失败");
    }

    @Test
    @DisplayName("receiveProductionStatus - 空列表直接返回空结果")
    public void testReceiveProductionStatus_empty() {
        VehicleCbuSoOperateResult result = service.receiveProductionStatus(List.of());

        assertNotNull(result);
        assertEquals(0, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
    }

    private VehicleCbuSoReceiveStatusReq buildStatusReq(String soNo, int status, String vin) {
        VehicleCbuSoReceiveStatusReq req = new VehicleCbuSoReceiveStatusReq();
        req.setSoNo(soNo);
        req.setStatus(status);
        req.setVin(vin);
        if (status == 1) {
            req.setPlanOfflineDate(LocalDate.now().plusMonths(1));
        }
        req.setScheduleTime(LocalDateTime.now());
        req.setBarcodePrintTime(LocalDateTime.now());
        req.setBodyInTime(LocalDateTime.now());
        req.setPaintInTime(LocalDateTime.now());
        req.setAssemblyInTime(LocalDateTime.now());
        req.setAssemblyOutTime(LocalDateTime.now());
        return req;
    }

    private VehicleCbuSoDo getSoBySoNo(String poNo, String soNo) {
        return service.listVehicleCbuSoByPoNo(poNo).stream()
                .filter(so -> soNo.equals(so.getSoNo()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("未找到 SO: " + soNo));
    }
}
