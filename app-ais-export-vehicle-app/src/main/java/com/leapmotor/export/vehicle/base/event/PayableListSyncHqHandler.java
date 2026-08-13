package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.dto.PayableListForHqDto;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.base.ThirdTargetPlatform;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应付清单同步红旗事件处理器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListSyncHqHandler extends AbstractEventHandler {

    private HongqiGateway hongqiGateway;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected BizEventType getSupportEvent() {
        return HongqiEventTypeEnum.PAYABLE_LIST_SYNC_HQ_SUBMIT;
    }

    @Override
    protected ThirdTargetPlatform getSupportPlatform() {
        return ThirdTargetPlatformEnum.HONGQI;
    }

    @Override
    protected void handle(EventNotifyTask task) {
        log.info("应付清单同步红旗-开始, bizNo={}", task.getBizNo());

        // 反序列化业务数据
        PayableListForHqDto dto = JSON.parseObject(task.getDataParam(), PayableListForHqDto.class);
        if (dto == null) {
            throw new BizException("应付清单同步红旗失败: 业务数据为空");
        }

        // 构建请求参数
        PayableListSyncHqReq req = buildRequest(dto);

        // 调用红旗接口
        HongqiResult result = hongqiGateway.syncPayableList(req);
        log.info("应付清单同步红旗结果: bizNo={}, result={}", task.getBizNo(), result);

        // 失败抛异常，触发重试
        if (result == null || result.getCode() != 200) {
            throw new BizException("应付清单同步红旗失败: " + JSON.toJSONString(result));
        }

        log.info("应付清单同步红旗成功, bizNo={}", task.getBizNo());
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        PayableListForHqDto dto = JSON.parseObject(task.getDataParam(), PayableListForHqDto.class);
        if (dto == null) {
            return null;
        }
        return buildRequest(dto);
    }

    /**
     * 构建红旗请求参数
     */
    private PayableListSyncHqReq buildRequest(PayableListForHqDto dto) {
        PayableListSyncHqReq req = new PayableListSyncHqReq();
        req.setPayableListNo(dto.getPayableListNo());
        req.setCustomerCode(dto.getCustomerCode());
        req.setCustomerName(dto.getCustomerName());
        req.setGenerateMonth(dto.getGenerateMonth());
        req.setTotalVehicles(dto.getTotalVehicles());
        req.setTotalAmount(dto.getTotalAmount());

        // 转换明细列表
        List<PayableListSyncHqReq.Detail> details = convertDetails(dto.getDetails());
        req.setDetails(details);

        return req;
    }

    /**
     * 转换明细列表
     */
    private List<PayableListSyncHqReq.Detail> convertDetails(List<PayableListForHqDto.PayableListDetailForHqDto> dtoDetails) {
        if (dtoDetails == null || dtoDetails.isEmpty()) {
            return Collections.emptyList();
        }

        return dtoDetails.stream().map(dto -> {
            PayableListSyncHqReq.Detail detail = new PayableListSyncHqReq.Detail();
            detail.setSoNo(dto.getSoNo());
            detail.setPoNo(dto.getPoNo());
            detail.setVin(dto.getVin());
            detail.setMaterialCode(dto.getMaterialCode());
            detail.setPrice(dto.getPrice());
            // LocalDateTime 转 String
            if (dto.getQcPassTime() != null) {
                detail.setQcPassTime(dto.getQcPassTime().format(DATE_TIME_FORMATTER));
            }
            return detail;
        }).collect(Collectors.toList());
    }
}