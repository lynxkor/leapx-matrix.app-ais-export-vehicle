package com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.gatewayimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leapmotor.export.vehicle.ExportVehicleApplication;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiProductionNodeNotifyReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiProductionNodeResultData;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link HongqiGateway#pushProductionNodeSync} 集成测试。
 *
 * <p>加载完整 Spring Boot 上下文，真实注入 HongqiGateway（实现 HongqiGatewayImpl），调用
 * pushProductionNodeSync 验证端到端链路：HongqiGatewayImpl -> HongqiClientApi(Feign)
 * -> app-ais-xuanniao 出口网关 -> 红旗 /external/hongqi/JT/MA/MA-0203/001/IVDO/receiveM1Data。
 * HongqiGatewayImpl 会将 Feign 调用异常包装成 BizException。
 *
 * <p>注意：真实调用依赖 test 环境 app-ais-xuanniao 网关与红旗端点可用；若不可用会抛 BizException，
 * 属预期，可据此排查链路/契约。无需 @RunWith(SpringRunner.class)：JUnit 5 + @SpringBootTest
 * 已内含 @ExtendWith(SpringExtension.class)。
 *
 * @author lijianuo
 * @since 2026-07-23
 */
@Slf4j
@DisplayName("HongqiGateway#pushProductionNodeSync 集成测试")
@TestPropertySource(locations = "file:D:/ais/nacos/ais-config-local.properties")
@SpringBootTest(classes = ExportVehicleApplication.class, properties = "spring.profiles.active=dev")
class HongqiGatewayTest {

    @Resource
    private HongqiGateway hongqiGateway;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("pushProductionNodeSync - 真实调用红旗生产节点同步接口")
    @SneakyThrows
    void testPushProductionNodeSync() {
        List<HongqiProductionNodeNotifyReq> reqs = List.of(
                buildReq("PO-TEST-001", "SO-TEST-001", "VINTEST001"));
        HongqiResult<List<HongqiProductionNodeResultData>> result = hongqiGateway.pushProductionNodeSync(reqs);
        log.info("pushProductionNodeSync 响应: {}", objectMapper.writeValueAsString(result));
        assertNotNull(result, "红旗应返回非空结果");
    }

    @Test
    @DisplayName("pushProductionNodeSync - 批量多条真实调用")
    @SneakyThrows
    void testPushProductionNodeSync_batch() {
        List<HongqiProductionNodeNotifyReq> reqs = List.of(
                buildReq("PO-TEST-001", "SO-TEST-001", "VINTEST001"),
                buildReq("PO-TEST-002", "SO-TEST-002", "VINTEST002"));
        HongqiResult<List<HongqiProductionNodeResultData>> result = hongqiGateway.pushProductionNodeSync(reqs);
        log.info("pushProductionNodeSync 批量响应: {}", objectMapper.writeValueAsString(result));
        assertNotNull(result, "红旗应返回非空结果");
    }

    private HongqiProductionNodeNotifyReq buildReq(String poNo, String soNo, String vin) {
        HongqiProductionNodeNotifyReq req = new HongqiProductionNodeNotifyReq();
        req.setPoNo(poNo);
        req.setSoNo(soNo);
        req.setVin(vin);
        req.setLpMtlNo("LPMTL0001");
        req.setPlanOfflineDate("20260801");
        req.setActOnlineTime("20260801100000");
        req.setActOfflineTime("20260801120000");
        return req;
    }
}
