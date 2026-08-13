package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.SapResult;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.dto.PayableListForSapDto;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.base.ThirdTargetPlatform;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应付清单同步SAP事件处理器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListSyncSapHandler extends AbstractEventHandler {

    private SapGateway sapGateway;

    @Override
    protected BizEventType getSupportEvent() {
        return SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT;
    }

    @Override
    protected ThirdTargetPlatform getSupportPlatform() {
        return ThirdTargetPlatformEnum.SAP;
    }

    @Override
    protected void handle(EventNotifyTask task) {
        log.info("应付清单同步SAP-开始, bizNo={}", task.getBizNo());

        // 反序列化业务数据
        PayableListForSapDto dto = JSON.parseObject(task.getDataParam(), PayableListForSapDto.class);
        if (dto == null) {
            throw new BizException("应付清单同步SAP失败: 业务数据为空");
        }

        // 构建请求参数
        PayableListSyncSapReq req = buildRequest(dto);

        // 调用SAP接口
        SapResult result = sapGateway.syncPayableList(req);
        log.info("应付清单同步SAP结果: bizNo={}, result={}", task.getBizNo(), result);

        // 失败抛异常，触发重试
        if (result == null || result.getCode() != 200) {
            throw new BizException("应付清单同步SAP失败: " + JSON.toJSONString(result));
        }

        log.info("应付清单同步SAP成功, bizNo={}", task.getBizNo());
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        PayableListForSapDto dto = JSON.parseObject(task.getDataParam(), PayableListForSapDto.class);
        if (dto == null) {
            return null;
        }
        return buildRequest(dto);
    }

    /**
     * 构建SAP请求参数
     */
    private PayableListSyncSapReq buildRequest(PayableListForSapDto dto) {
        PayableListSyncSapReq req = new PayableListSyncSapReq();
        req.setPayableListNo(dto.getPayableListNo());
        req.setCustomerCode(dto.getCustomerCode());
        req.setCustomerName(dto.getCustomerName());
        req.setTotalAmount(dto.getTotalAmount());

        // 转换明细列表
        List<PayableListSyncSapReq.Detail> details = convertDetails(dto.getDetails());
        req.setDetails(details);

        return req;
    }

    /**
     * 转换明细列表
     */
    private List<PayableListSyncSapReq.Detail> convertDetails(List<PayableListForSapDto.PayableListDetailForSapDto> dtoDetails) {
        if (dtoDetails == null || dtoDetails.isEmpty()) {
            return Collections.emptyList();
        }

        return dtoDetails.stream().map(dto -> {
            PayableListSyncSapReq.Detail detail = new PayableListSyncSapReq.Detail();
            detail.setSoNo(dto.getSoNo());
            detail.setVin(dto.getVin());
            detail.setPrice(dto.getPrice());
            return detail;
        }).collect(Collectors.toList());
    }
}