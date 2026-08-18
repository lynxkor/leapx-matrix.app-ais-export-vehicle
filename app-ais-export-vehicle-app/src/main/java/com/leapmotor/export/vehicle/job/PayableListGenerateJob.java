package com.leapmotor.export.vehicle.job;

import com.leapmotor.export.vehicle.payablelist.executor.cmd.PayableListGenerateExe;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 应付清单月度生成定时任务，查询所有状态=质检通过 且 无活动明细（无 delete_status=0 的明细记录）的SO车辆
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListGenerateJob {

    private PayableListGenerateExe payableListGenerateExe;

    @XxlJob("payableListGenerateJob")
    public void execute() {
        log.info("应付清单月度生成任务开始执行");
        try {
            payableListGenerateExe.generateMonthly();
            log.info("应付清单月度生成任务执行完成");
        } catch (Exception e) {
            log.error("应付清单月度生成任务执行失败", e);
            throw e;
        }
    }
}
