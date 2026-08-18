package com.leapmotor.export.vehicle.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * APS 回传批量限制配置。
 *
 * @author leapmotor
 * @since 2026-07-21
 */
@Component
public class ApsReceiveProperties {

    /**
     * 接收 APS 生产状态回传单次批量上限，默认 100，可经 nacos 配置 aps.receive.batch-size-limit 覆盖。
     */
    @Value("${aps.receive.batch-size-limit:100}")
    private int batchSizeLimit;

    public int getBatchSizeLimit() {
        return batchSizeLimit;
    }
}
