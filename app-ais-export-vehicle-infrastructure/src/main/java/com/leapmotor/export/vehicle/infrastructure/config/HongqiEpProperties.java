package com.leapmotor.export.vehicle.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 红旗 EP 对接配置（Nacos 键 hongqi.ep.*）
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Component
@ConfigurationProperties(prefix = "hongqi.ep")
@Data
public class HongqiEpProperties {

    /**
     * 固定请求头 tenantId
     */
    private String tenantId;

    /**
     * 固定请求头 useragent
     */
    private String userAgent;

    /**
     * 固定请求头 companyid
     */
    private String companyId;

    /**
     * 应用标识（EP 提供）
     */
    private String appkey;

    /**
     * 应用密钥（EP 提供，仅用于获取 token，禁止打日志）
     */
    private String secretkey;
}