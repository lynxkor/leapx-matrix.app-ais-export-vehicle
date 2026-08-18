package com.leapmotor.export.vehicle.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SO 下发生产配置。
 *
 * @author leapmotor
 * @since 2026-07-22
 */
@Component
public class SoIssueProperties {

    /**
     * 下发生产分批大小，默认 100，可经 nacos 配置 so.issue.batch-size 覆盖。
     *
     * <p>与 soIssueExecutor（core8/max16/queue500）匹配：单批不超过 queue 容量，
     * 避免 AbortPolicy 误拒；同时控制 batchQueryDetails 的 IN 数量对 SQL 友好。
     */
    @Value("${so.issue.batch-size:100}")
    private int batchSize;

    public int getBatchSize() {
        return batchSize;
    }
}
