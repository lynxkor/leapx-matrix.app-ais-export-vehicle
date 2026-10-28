package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.enums.SyncResultEnum;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应付清单同步SAP事件处理器：
 * 按 bizNo 实时回查清单及全量明细 -> 组装 SAP 契约请求（IV_SYSID + IM_TAB）-> 调 SapGateway
 * -> 总状态 ES_RET.RET_CODE != S 抛异常触发重试（SAP 返回成功后，同一清单只传一次）。
 * 失败提示：总状态报错时具体原因在 RET_TAB 明细失败行上，优先汇总明细行 RET_REASON。
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListSyncSapHandler extends AbstractEventHandler {

    private SapGateway sapGateway;
    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;

    /**
     * sync_sap_message 报文留存字符上限，与 sync_hq_message(varchar(2000)) 口径保持一致
     */
    private static final int SYNC_SAP_MESSAGE_MAX_CHARS = 2000;

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
        String bizNo = task.getBizNo();
        log.info("应付清单同步SAP-开始, bizNo={}", bizNo);

        // 按清单编号实时回查
        PayableListDo payableList = payableListGateway.getPayableListByNo(bizNo);
        if (payableList == null) {
            throw new BizException("应付清单同步SAP失败: 应付清单不存在, bizNo=" + bizNo);
        }

        // 构建SAP请求参数
        PayableListSyncSapReq req = assembleFromDb(payableList);

        // 调用SAP接口
        PayableListSyncSapResult result = sapGateway.syncPayableList(req);
        log.info("应付清单同步SAP结果: bizNo={}, result={}", bizNo, result);

        // 成功/失败结果均先落库留痕，再决定是否抛异常触发重试
        writeBackSyncResult(payableList, result);
        if (!isSyncSuccess(result)) {
            throw new BizException("应付清单同步SAP失败" + buildFailReason(result));
        }

        log.info("应付清单同步SAP成功, bizNo={}", bizNo);
    }

    /**
     * 回写本次推送结果（成功口径判定 + 报文截断 + 只写同步三列）。
     */
    public void writeBackSyncResult(PayableListDo payableList, PayableListSyncSapResult result) {
        // 返回报文：无返回值时留空，避免把字符串 "null" 落库
        String message = result == null ? null : JSON.toJSONString(result);
        // 结果取值统一用 SyncResultEnum：补推入口的防重守卫比的就是这一列，两边必须同一口径
        updateSyncResult(payableList,
                isSyncSuccess(result) ? SyncResultEnum.SUCCESS.getCode() : SyncResultEnum.FAIL.getCode(), message);
    }

    @Override
    public Object buildRequest(EventNotifyTask task) {
        // 事件不预构建载荷（对齐 PayableListSyncHqHandler）：发布侧 data_param 留空，
        // 明细一律在消费时按 biz_no 回查，避免全量明细写入 TEXT 列
        return null;
    }

    /**
     * 回查清单全量明细并组装 SAP 契约请求：
     * IV_SYSID 取模型默认值 AIS；IM_TAB 每行 = PAYNR(清单编号)/PORDER(红旗PO)/SORDER(销服SO)/VHVIN(VIN)。
     *
     * @throws BizException 清单无有效明细——推空表给 SAP 无意义，且会被整单拒绝
     */
    public PayableListSyncSapReq assembleFromDb(PayableListDo payableList) {
        String payableListNo = payableList.getPayableListNo();
        List<PayableListDetailDo> detailList =
                payableListDetailGateway.listByPayableListId(payableList.getId());
        if (detailList == null || detailList.isEmpty()) {
            throw new BizException("应付清单无有效明细，终止推送SAP: payableListNo=" + payableListNo);
        }

        Integer totalVehicles = payableList.getTotalVehicles();
        if (totalVehicles != null && totalVehicles.intValue() != detailList.size()) {
            log.warn("应付清单抬头台数与明细行数不一致, payableListNo={}, totalVehicles={}, detailRows={}",
                    payableListNo, totalVehicles, detailList.size());
        }

        List<PayableListSyncSapReq.ImTabItem> rows = new ArrayList<>(detailList.size());
        for (PayableListDetailDo detail : detailList) {
            if (StringUtils.isBlank(detail.getVin())) {
                log.warn("应付清单明细VIN为空，SAP可能逐行拒绝该条: payableListNo={}, soNo={}, poNo={}",
                        payableListNo, detail.getSoNo(), detail.getPoNo());
            }
            PayableListSyncSapReq.ImTabItem item = new PayableListSyncSapReq.ImTabItem();
            item.setPaynr(payableListNo);
            item.setPorder(detail.getPoNo());
            item.setSorder(detail.getSoNo());
            item.setVhvin(detail.getVin());
            rows.add(item);
        }

        PayableListSyncSapReq req = new PayableListSyncSapReq();
        req.setImTab(rows);
        log.info("应付清单同步SAP载荷组装完成, payableListNo={}, rows={}", payableListNo, rows.size());
        return req;
    }

    /**
     * 判断 SAP 调用结果是否成功：只看总状态 ES_RET.RET_CODE = S；
     * 总状态成功时无需解析 RET_TAB 明细状态。
     *
     */
    public static boolean isSyncSuccess(PayableListSyncSapResult result) {
        if (result == null || !result.isSuccess()) {
            return false;
        }
        if (result.getBody() == null || result.getBody().getEsRet() == null) {
            return false;
        }
        return "S".equals(result.getBody().getEsRet().getRetCode());
    }

    /**
     * 组装失败提示后缀（仅在总状态失败时调用）：
     *
     * 优先汇总 RET_TAB 明细失败行（RET_CODE != S）的 RET_REASON——总状态报错时具体原因在明细上；
     * 明细缺失或无失败原因时回退总状态 ES_RET.RET_REASON（兼容无明细返回）；
     *  均为空（null/空串/body/ES_RET 缺失）时无后缀，仅提示同步失败。
     *
     */
    private String buildFailReason(PayableListSyncSapResult result) {
        if (result != null && result.getBody() != null) {
            String detailReason = buildDetailFailReason(result.getBody().getRetTab());
            if (detailReason != null) {
                return ": " + detailReason;
            }
            if (result.getBody().getEsRet() != null) {
                String retReason = result.getBody().getEsRet().getRetReason();
                if (retReason != null && !retReason.isEmpty()) {
                    return ": " + retReason;
                }
            }
        }
        return "";
    }

    /**
     * 回写同步SAP结果到应付清单表
     *
     * 回写异常仅告警不阻断同步主流程：避免结果落库异常导致"已成功的推送被重试重复执行"。
     */
    private void updateSyncResult(PayableListDo payableList, String result, String message) {
        try {
            payableListGateway.updateSyncSapResultById(payableList.getId(), LocalDateTime.now(), result,
                    truncateMessage(message));
        } catch (Exception e) {
            log.error("应付清单同步SAP结果回写异常, payableListNo={}", payableList.getPayableListNo(), e);
        }
    }

    /**
     * SAP 返回报文按字符上限截断
     */
    private String truncateMessage(String message) {
        return StringUtils.left(message, SYNC_SAP_MESSAGE_MAX_CHARS);
    }

    /**
     * 汇总明细失败原因：收集 RET_TAB 中失败行（RET_CODE != S）的消息文本，去空去重后分号拼接；
     * 无失败行或失败行均无消息文本时返回 null，便于上层回退。
     */
    private String buildDetailFailReason(List<PayableListSyncSapResult.RetTabItem> retTab) {
        if (retTab == null || retTab.isEmpty()) {
            return null;
        }
        String joined = retTab.stream()
                .filter(item -> item != null && !"S".equals(item.getRetCode()))
                .map(PayableListSyncSapResult.RetTabItem::getRetReason)
                .filter(reason -> reason != null && !reason.isEmpty())
                .distinct()
                .collect(Collectors.joining("; "));
        return joined.isEmpty() ? null : joined;
    }
}
