/**
 * COPYRIGHT ©2019-08-08 浙江零跑科技有限公司 https://www.leapmotor.com/
 * <p>
 * author yang_long@leapmotor.com
 */
package com.leapmotor.export.vehicle.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池配置
 */
@Configuration
public class AsyncConfig {

    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(5000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadGroupName("async");
        executor.setThreadNamePrefix("Async_task:");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("mqTaskExecutor")
    public TaskExecutor mqTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(32);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(5000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadGroupName("mq");
        executor.setThreadNamePrefix("mq_task:");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * SO 下发生产专用线程池（不复用 taskExecutor，避免共享池污染与 CallerRunsPolicy 反噬 HTTP 线程）。
     * AbortPolicy：超量任务直接失败计入 failItems，不反噬提交线程。池参数压测后调整。
     */
    @Bean("soIssueExecutor")
    public TaskExecutor soIssueExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadGroupName("so-issue");
        executor.setThreadNamePrefix("SoIssue:");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}
