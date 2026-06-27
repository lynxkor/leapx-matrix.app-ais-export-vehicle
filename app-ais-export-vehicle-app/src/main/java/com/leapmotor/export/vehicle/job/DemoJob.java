package com.leapmotor.export.vehicle.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 定时任务示例
 */
@Component
@Slf4j
public class DemoJob {

    @XxlJob("demoJob")
    public void demoJob() {
        log.info("开始执行定时任务");

        log.info("定时任务执行结束");
    }
}
