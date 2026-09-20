package com.leapmotor.export.vehicle.payablelist.executor.cmd;

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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 同步SAP执行器（已确认后主动推送，事件驱动重试场景）
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListSyncToSapExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private EventHandlerHelper eventHandlerHelper;

    @Transactional(rollbackFor = Exception.class)
    public boolean execute(String payableListId) {
        PayableListDo db = payableListGateway.getPayableListById(payableListId);
        if (db == null) {
            throw new BizException("应付清单不存在");
        }
        // 仅已确认状态可推送 SAP
        if (!PayableListStatusEnum.CONFIRMED.getCode().equals(db.getBillStatus())) {
            throw new BizException("仅已确认状态的单据可推送SAP");
        }

        // 发布 SAP 同步事件（异步，事务提交后执行）
        PayableListForSapDto sapPayload = buildSapPayload(db);
        eventHandlerHelper.publishAsync(
            ThirdTargetPlatformEnum.SAP,
            SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT,
            db.getPayableListNo(),
            sapPayload
        );

        log.info("推送SAP事件已发布，payableListId={}", payableListId);
        return true;
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
