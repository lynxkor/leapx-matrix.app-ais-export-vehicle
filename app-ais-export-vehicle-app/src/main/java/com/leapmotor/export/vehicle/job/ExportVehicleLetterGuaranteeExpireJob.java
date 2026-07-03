package com.leapmotor.export.vehicle.job;

import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 保函凌晨失效任务
 *
 * <p>扫描条件:approval_status=30(审核通过) AND lg_status=20(有效) AND expiry_date &lt; today
 * <p>动作:lg_status 20 → 30 + 写失效流水 record_type=50
 *
 * <p>XXL-JOB handler key: {@code letterGuaranteeExpireJob}
 *
 * @author wangkai2527148
 * @since 2026-07-03
 */
@Slf4j
@Component
@AllArgsConstructor
public class ExportVehicleLetterGuaranteeExpireJob {

    private ExportVehicleLetterGuaranteeGateway gateway;

    @XxlJob("letterGuaranteeExpireJob")
    @Transactional(rollbackFor = Exception.class)
    public void expire() {
        LocalDate today = LocalDate.now();
        log.info("保函凌晨失效任务开始: today={}", today);
        List<ExportVehicleLetterGuaranteeDo> expired = gateway.listExpiredBefore(today);
        int total = expired == null ? 0 : expired.size();
        log.info("扫描出待失效保函 {} 条", total);

        // 注:gateway.expireEarly 自身已含状态机校验 + 流水写入;
        // 此处逐条调用,任一异常不影响其他条目(便于重试)。整批失败分批提交由外层 @Transactional 控制。
        // 实际为避免大批量任务单事务过长,可改成分批处理(设计文档 §11.1)。
        int success = 0;
        if (expired != null) {
            for (ExportVehicleLetterGuaranteeDo lg : expired) {
                try {
                    gateway.expireEarly(lg.getId(), "凌晨任务自动失效");
                    success++;
                } catch (Exception e) {
                    log.error("保函自动失效失败: id={}, err={}", lg.getId(), e.getMessage(), e);
                }
            }
        }
        log.info("保函凌晨失效任务结束: 共 {} 条,成功 {} 条", total, success);
    }
}
