package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListBatchSubmitReq;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量提交应付清单执行器
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListBatchSubmitExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private EventHandlerHelper eventHandlerHelper;

    @Transactional(rollbackFor = Exception.class)
    public List<String> execute(PayableListBatchSubmitReq req) {
        List<String> successIds = new ArrayList<>();
        List<String> failedItems = new ArrayList<>();

        LocalDateTime submitTime = LocalDateTime.now();

        for (PayableListBatchSubmitReq.PayableListSubmitItem item : req.getItems()) {
            try {
                // 查询应付清单
                PayableListDo payableList = payableListGateway.getPayableListById(item.getId());
                if (payableList == null) {
                    failedItems.add(item.getId() + ": 应付清单不存在");
                    continue;
                }

                // 校验状态（只有草稿或驳回状态可以提交）
                PayableListStatusChecker.checkCanSubmit(payableList);

                // 校验版本号（乐观锁）
                PayableListStatusChecker.checkVersion(payableList, item.getVersion());

                // 更新状态和提交时间
                payableList.setBillStatus(PayableListStatusEnum.SUBMITTED.getCode());
                payableList.setSubmitTime(submitTime);
                payableList.setVersion(payableList.getVersion() + 1);

                // 保存
                payableListGateway.updatePayableList(payableList);

                // 同步更新明细表状态
                payableListDetailGateway.updateBillStatusByPayableListId(
                        item.getId(),
                        PayableListStatusEnum.SUBMITTED.getCode(),
                        submitTime,
                        null
                );

                // 发布红旗同步事件（异步，事务提交后执行）
                // 第4个参数必须强转 Object 以命中带 afterCommit 保护的 data 重载（null 字面量会命中 String relatedNo 重载，事务回滚后事件仍会消费）
                eventHandlerHelper.publishAsync(
                        ThirdTargetPlatformEnum.HONGQI,
                        HongqiEventTypeEnum.PAYABLE_LIST_SYNC_HQ_SUBMIT,
                        payableList.getPayableListNo(),
                        (Object) null
                );

                successIds.add(item.getId());

                log.info("应付清单提交成功，id={}, status={}, submitTime={}",
                        item.getId(), PayableListStatusEnum.SUBMITTED.getMessage(), submitTime);

            } catch (BizException e) {
                failedItems.add(item.getId() + ": " + e.getMessage());
                log.warn("应付清单提交失败，id={}, reason={}", item.getId(), e.getMessage());
            } catch (Exception e) {
                failedItems.add(item.getId() + ": 系统异常");
                log.error("应付清单提交异常，id={}", item.getId(), e);
            }
        }

        // 如果有失败的项，抛出异常回滚整个事务
        if (!failedItems.isEmpty()) {
            throw new BizException("批量提交失败，失败的项: " + String.join("; ", failedItems));
        }

        log.info("批量提交应付清单成功，成功数量={}", successIds.size());
        return successIds;
    }
}
