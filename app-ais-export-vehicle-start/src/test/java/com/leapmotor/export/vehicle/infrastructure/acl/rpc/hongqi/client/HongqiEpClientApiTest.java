package com.leapmotor.export.vehicle.infrastructure.acl.rpc.hongqi.client;

import com.leapmotor.export.vehicle.ExportVehicleApplication;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.HongqiTokenReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiTokenResult;
import com.leapmotor.export.vehicle.infrastructure.config.HongqiEpProperties;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 红旗 EP 获取 Token 端到端集成测试。
 *
 * <p>加载完整 Spring Boot 上下文，经 app-ais-xuanniao 出口网关（/external/hongqi-ep）
 * 真实调用 EP validateAuthByAppKey 接口，验证：Nacos 配置注入（缺省由下方测试属性兜底）→
 * Feign 请求头/参数 → 网关转发 → EP 返回 token 全链路。
 * getToken 是整个应付清单同步链路的前置依赖，此处优先验证。
 *
 * <p>注意：真实调用依赖 test 环境网关路由已配置且 EP 可达；Nacos 若已配 hongqi.ep.* 以其为准，
 * 未配则由 @SpringBootTest properties 兜底。任一前置不满足会抛异常/断言失败，属预期。
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Slf4j
@DisplayName("HongqiEpClientApi#getToken 集成测试")
@SpringBootTest(classes = ExportVehicleApplication.class, properties = {
        "spring.profiles.active=dev",
        "hongqi.ep.tenant-id=3",
        "hongqi.ep.user-agent=pc",
        "hongqi.ep.company-id=3450",
        "hongqi.ep.appkey=JCKEP_LEAPMOTOR",
        "hongqi.ep.secretkey=jckepleap2026"
})
class HongqiEpClientApiTest {

    @Resource
    private HongqiEpClientApi hongqiEpClientApi;

    @Resource
    private HongqiEpProperties hongqiEpProperties;

    @Test
    @DisplayName("getToken - 真实调用EP获取访问令牌")
    @SneakyThrows
    void testGetToken() {
        assertNotNull(hongqiEpProperties.getAppkey(), "Nacos 缺少 hongqi.ep.appkey 配置");
        assertNotNull(hongqiEpProperties.getSecretkey(), "Nacos 缺少 hongqi.ep.secretkey 配置");

        HongqiTokenReq req = new HongqiTokenReq();
        req.setAppkey(hongqiEpProperties.getAppkey());
        req.setSecretkey(hongqiEpProperties.getSecretkey());
        req.setLoginTimestamp(System.currentTimeMillis());

        HongqiTokenResult result = hongqiEpClientApi.getToken(
                hongqiEpProperties.getTenantId(),
                hongqiEpProperties.getUserAgent(),
                req);
        log.info("getToken 响应: status={}, code={}, token前缀={}",
                result == null ? null : result.getStatus(),
                result == null ? null : result.getCode(),
                result != null && result.getData() != null && result.getData().getToken() != null
                        ? result.getData().getToken().substring(0, Math.min(20, result.getData().getToken().length()))
                        : null);

        assertNotNull(result, "getToken 响应不应为空");
        assertTrue("S".equals(result.getStatus()) && result.getCode() != null && result.getCode() == 200,
                "token 获取失败: status=" + result.getStatus() + ", code=" + result.getCode());
        assertNotNull(result.getData(), "data 不应为空");
        assertNotNull(result.getData().getToken(), "token 不应为空");
    }
}