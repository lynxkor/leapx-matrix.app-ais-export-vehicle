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
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public void process(String payableListNo, String confirmResult, LocalDateTime confirmTime, String confirmRemark) {
        PayableListDo db = payableListGateway.getPayableListByNo(payableListNo);
        if (db == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }
        // 已确认状态不允许再同步
        if (PayableListStatusEnum.CONFIRMED.getCode().equals(db.getBillStatus())) {
            throw new BizException(ResultEnum.BAD_REQUEST, "不允许已确认之后再同步");
        }

        db.setConfirmTime(confirmTime);
        db.setConfirmRemark(confirmRemark);

        if (PayableListStatusEnum.REJECTED.getCode().equals(confirmResult)) {
            // 驳回
            db.setBillStatus(PayableListStatusEnum.REJECTED.getCode());
            payableListGateway.updatePayableList(db);
            log.info("红旗驳回，payableListNo={}", payableListNo);
        } else if (PayableListStatusEnum.CONFIRMED.getCode().equals(confirmResult)) {
            // 确认通过
            db.setBillStatus(PayableListStatusEnum.CONFIRMED.getCode());
            payableListGateway.updatePayableList(db);

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
            throw new BizException(ResultEnum.BAD_REQUEST, "无效的确认结果：" + confirmResult);
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
