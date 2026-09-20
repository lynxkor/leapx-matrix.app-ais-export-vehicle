package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.gateway.HongqiGateway;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.req.PayableListSyncHqRecordReq;
import com.leapmotor.export.vehicle.acl.rpc.hongqi.response.HongqiResult;
import com.leapmotor.export.vehicle.base.event.enums.HongqiEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.event.core.base.BizEventType;
import com.leapmotor.paltform.ais.common.event.core.base.ThirdTargetPlatform;
import com.leapmotor.paltform.ais.common.event.core.model.EventNotifyTask;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * sync_hq_message 字段长度上限
     */
    private static final int SYNC_HQ_MESSAGE_MAX_LENGTH = 2000;

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
        String bizNo = task.getBizNo();
        log.info("应付清单同步红旗-开始, bizNo={}", bizNo);

        // 根据应付清单编号实时查询清单及明细
        PayableListDo payableList = payableListGateway.getPayableListByNo(bizNo);
        if (payableList == null) {
            throw new BizException("应付清单同步红旗失败: 应付清单不存在, bizNo=" + bizNo);
        }
        List<PayableListDetailDo> detailList = payableListDetailGateway.listByPayableListId(payableList.getId());

        // 构建请求参数
        PayableListSyncHqRecordReq req = buildRequest(payableList, detailList);

        // 调用红旗 EP 接口
        HongqiResult result = hongqiGateway.syncPayableListEp(req);
        log.info("应付清单同步红旗结果: bizNo={}, result={}", bizNo, result);

        // 回写同步结果
        String message = result == null ? null : JSON.toJSONString(result);
        if (result == null || result.getCode() != 200) {
            updateSyncResult(payableList, "FAIL", message);
            throw new BizException("应付清单同步红旗失败: " + JSON.toJSONString(result));
        }
        updateSyncResult(payableList, "SUCCESS", message);

        log.info("应付清单同步红旗成功, bizNo={}", bizNo);
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        // 事件不预构建 payload，消费时根据编号实时查库组装
        return null;
    }

    /**
     * 构建红旗请求参数
     */
    private PayableListSyncHqRecordReq buildRequest(PayableListDo payableList, List<PayableListDetailDo> detailList) {
        PayableListSyncHqRecordReq req = new PayableListSyncHqRecordReq();
        if (detailList == null || detailList.isEmpty()) {
            return req;
        }

        List<PayableListSyncHqRecordReq.Record> records = new ArrayList<>(detailList.size());
        for (PayableListDetailDo detail : detailList) {
            PayableListSyncHqRecordReq.Record record = new PayableListSyncHqRecordReq.Record();
            // 清单头信息
            record.setPayableListNo(payableList.getPayableListNo());
            record.setCustomerCode(payableList.getCustomerCode());
            record.setTotalVehicles(payableList.getTotalVehicles());
            record.setTotalAmount(payableList.getTotalAmount());
            record.setGenerateMonth(payableList.getGenerateMonth());
            record.setVersion(payableList.getVersion());
            // 明细信息
            record.setPoNo(detail.getPoNo());
            record.setPoCreateTime(formatDateTime(detail.getPoCreateTime()));
            record.setSoNo(detail.getSoNo());
            record.setVin(detail.getVin());
            record.setMaterialCode(detail.getMaterialCode());
            record.setPrice(detail.getPrice());
            record.setQcPassTime(formatDateTime(detail.getQcPassTime()));
            records.add(record);
        }
        req.setRecords(records);
        return req;
    }

    /**
     * LocalDateTime 转 String（yyyy-MM-dd HH:mm:ss）
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 回写同步红旗结果到应付清单表（只写同步维度三列）
     *
     * <p>不用快照全量回写：查询快照到回写之间可能跨越红旗接口网络调用，期间并发变更的单据状态、版本号等字段
     * 会被旧快照覆盖，导致抬头与明细不一致。
     */
    private void updateSyncResult(PayableListDo payableList, String result, String message) {
        payableListGateway.updateSyncHqResultById(payableList.getId(), LocalDateTime.now(), result,
                truncateMessage(message));
    }

    /**
     * 截断同步报文至字段长度上限，防止落库异常
     */
    private String truncateMessage(String message) {
        if (message == null) {
            return null;
        }
        if (message.length() > SYNC_HQ_MESSAGE_MAX_LENGTH) {
            return message.substring(0, SYNC_HQ_MESSAGE_MAX_LENGTH);
        }
        return message;
    }
}
