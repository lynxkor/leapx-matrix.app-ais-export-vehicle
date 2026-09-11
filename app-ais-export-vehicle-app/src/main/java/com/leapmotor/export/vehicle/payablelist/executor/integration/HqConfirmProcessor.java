package com.leapmotor.export.vehicle.payablelist.executor.integration;

import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.dto.PayableListForSapDto;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.platform.ais.common.redis.BlockLock;
import com.leapmotor.platform.ais.common.redis.DistributedLockTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 红旗确认结果处理器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class HqConfirmProcessor {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private EventHandlerHelper eventHandlerHelper;
    private DistributedLockTemplate distributedLockTemplate;
    private TransactionTemplate transactionTemplate;

    public void process(String payableListNo, String confirmResult, LocalDateTime confirmTime, String confirmRemark) {
        // 按清单单号加分布式锁：
        // 同一清单的并发确认/驳回串行化，锁释放晚于事务提交
        String lockKey = "hq:confirm:" + payableListNo;
        BlockLock lock;
        try {
            lock = distributedLockTemplate.blockLock(lockKey, 30000, 60000);
        } catch (Exception e) {
            throw new BizException("获取锁失败，请稍后重试：" + e.getMessage());
        }
        try {
            transactionTemplate.execute(status -> {
                doConfirm(payableListNo, confirmResult, confirmTime, confirmRemark);
                return null;
            });
        } finally {
            closeLock(lock, lockKey);
        }
    }

    /**
     * 锁内执行确认/驳回（独立事务）：先幂等判断，再进行状态迁移
     */
    private void doConfirm(String payableListNo, String confirmResult, LocalDateTime confirmTime, String confirmRemark) {
        PayableListDo db = payableListGateway.getPayableListByNo(payableListNo);
        if (db == null) {
            throw new BizException("应付清单不存在");
        }

        if ("R".equals(confirmResult)) {
            // 驳回
            if (StringUtils.isBlank(confirmRemark)) {
                throw new BizException("驳回原因不能为空");
            }
            // 幂等：已是驳回状态，重复回调直接成功
            if (PayableListStatusEnum.REJECTED.getCode().equals(db.getBillStatus())) {
                log.info("红旗驳回幂等跳过：payableListNo={}，状态已是驳回", payableListNo);
                return;
            }
            // 状态机校验：仅已提交待确认的清单可驳回
            if (!PayableListStatusEnum.SUBMITTED.getCode().equals(db.getBillStatus())) {
                log.info("当前状态不允许驳回: billStatus={}", db.getBillStatus());
                throw new BizException("当前状态不允许驳回");
            }
            db.setConfirmTime(confirmTime);
            db.setConfirmRemark(confirmRemark);
            db.setBillStatus(PayableListStatusEnum.REJECTED.getCode());
            payableListGateway.updatePayableList(db);
            // 同步明细表字段（单据状态、确认时间）
            payableListDetailGateway.updateBillStatusByPayableListId(
                    db.getId(), PayableListStatusEnum.REJECTED.getCode(), null, confirmTime);
            log.info("红旗驳回，payableListNo={}", payableListNo);
        } else if ("A".equals(confirmResult)) {
            // 确认
            // 幂等：已是确认状态，重复回调直接成功，不再重复发布 SAP 事件
            if (PayableListStatusEnum.CONFIRMED.getCode().equals(db.getBillStatus())) {
                log.info("红旗确认幂等跳过：payableListNo={}，状态已是确认", payableListNo);
                return;
            }
            // 状态机校验：仅已提交待确认的清单可确认
            if (!PayableListStatusEnum.SUBMITTED.getCode().equals(db.getBillStatus())) {
                log.info("当前状态不允许确认: billStatus={}", db.getBillStatus());
                throw new BizException("当前状态不允许确认");
            }
            db.setConfirmTime(confirmTime);
            db.setConfirmRemark(confirmRemark);
            db.setBillStatus(PayableListStatusEnum.CONFIRMED.getCode());
            payableListGateway.updatePayableList(db);
            // 同步明细表字段（单据状态、确认时间）
            payableListDetailGateway.updateBillStatusByPayableListId(
                    db.getId(), PayableListStatusEnum.CONFIRMED.getCode(), null, confirmTime);
            // 发布 SAP 同步事件（异步，事务提交后执行）
            PayableListForSapDto sapPayload = buildSapPayload(db);
            eventHandlerHelper.publishAsync(
                ThirdTargetPlatformEnum.SAP,
                SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT,
                db.getPayableListNo(),
                sapPayload
            );

            log.info("红旗确认通过，payableListNo={}，已发布SAP同步事件", payableListNo);
        } else {
            throw new BizException("无效的确认结果：" + confirmResult);
        }
    }

    /**
     * 释放分布式锁
     */
    private void closeLock(BlockLock lock, String lockKey) {
        if (lock == null) {
            return;
        }
        try {
            lock.close();
        } catch (Exception e) {
            log.warn("释放锁异常, lockKey={}, 可能已提前过期, msg={}", lockKey, e.getMessage());
        }
    }

    /**
     * 构建 SAP 同步数据
     */
    private PayableListForSapDto buildSapPayload(PayableListDo db) {
        PayableListForSapDto dto = new PayableListForSapDto();
        dto.setPayableListNo(db.getPayableListNo());
        dto.setCustomerCode(db.getCustomerCode());
        dto.setCustomerName(db.getCustomerName());
        dto.setTotalAmount(db.getTotalAmount());

        // 查询明细列表
        List<PayableListDetailDo> detailList = payableListDetailGateway.listByPayableListId(db.getId());
        if (detailList != null && !detailList.isEmpty()) {
            List<PayableListForSapDto.PayableListDetailForSapDto> details = detailList.stream()
                .map(detail -> {
                    PayableListForSapDto.PayableListDetailForSapDto detailDto = new PayableListForSapDto.PayableListDetailForSapDto();
                    detailDto.setSoNo(detail.getSoNo());
                    detailDto.setPoNo(detail.getPoNo());
                    detailDto.setVin(detail.getVin());
                    detailDto.setPrice(detail.getPrice());
                    return detailDto;
                })
                .collect(Collectors.toList());
            dto.setDetails(details);
        }

        return dto;
    }
}
