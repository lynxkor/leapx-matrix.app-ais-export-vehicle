package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.dto.PayableListForHqDto;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListSubmitReq;
import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提交付应付清单执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListSubmitExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private EventHandlerHelper eventHandlerHelper;

    @Transactional(rollbackFor = Exception.class)
    public String execute(PayableListSubmitReq req) {
        PayableListDo db = payableListGateway.getPayableListById(req.getId());
        if (db == null) {
            throw new BizException("应付清单不存在");
        }
        // 乐观锁校验
        if (db.getVersion() == null || !db.getVersion().equals(req.getVersion())) {
            throw new BizException("数据已被他人修改，请刷新后重试");
        }
        // 状态前置校验
        PayableListStatusChecker.checkEditable(db);

        // 更新状态为已提交待确认
        db.setBillStatus(PayableListStatusEnum.SUBMITTED.getCode());
        db.setVersion(db.getVersion() + 1);
        LocalDateTime submitTime = LocalDateTime.now();
        db.setSubmitTime(submitTime);
        payableListGateway.updatePayableList(db);

        // 同步更新明细表状态
        payableListDetailGateway.updateBillStatusByPayableListId(
                db.getId(),
                PayableListStatusEnum.SUBMITTED.getCode(),
                submitTime,
                null
        );

        // 发布红旗同步事件（异步，事务提交后执行）
        PayableListForHqDto hqPayload = buildHqPayload(db);
        eventHandlerHelper.publishAsync(
            ThirdTargetPlatformEnum.HONGQI,
            HongqiEventTypeEnum.PAYABLE_LIST_SYNC_HQ_SUBMIT,
            db.getPayableListNo(),
            hqPayload
        );

        log.info("应付清单提交成功，已发布红旗同步事件，id={}, no={}", db.getId(), db.getPayableListNo());
        return db.getId();
    }

    /**
     * 构建红旗同步数据
     */
    private PayableListForHqDto buildHqPayload(PayableListDo db) {
        PayableListForHqDto dto = new PayableListForHqDto();
        dto.setPayableListNo(db.getPayableListNo());
        dto.setCustomerCode(db.getCustomerCode());
        dto.setCustomerName(db.getCustomerName());
        dto.setGenerateMonth(db.getGenerateMonth());
        dto.setTotalVehicles(db.getTotalVehicles());
        dto.setTotalAmount(db.getTotalAmount());

        // 查询明细列表
        List<PayableListDetailDo> detailList = payableListDetailGateway.listByPayableListId(db.getId());
        if (detailList != null && !detailList.isEmpty()) {
            List<PayableListForHqDto.PayableListDetailForHqDto> details = detailList.stream()
                .map(detail -> {
                    PayableListForHqDto.PayableListDetailForHqDto detailDto = new PayableListForHqDto.PayableListDetailForHqDto();
                    detailDto.setSoNo(detail.getSoNo());
                    detailDto.setPoNo(detail.getPoNo());
                    detailDto.setVin(detail.getVin());
                    detailDto.setMaterialCode(detail.getMaterialCode());
                    detailDto.setPrice(detail.getPrice());
                    detailDto.setQcPassTime(detail.getQcPassTime());
                    return detailDto;
                })
                .collect(Collectors.toList());
            dto.setDetails(details);
        }

        return dto;
    }
}
