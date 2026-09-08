package com.leapmotor.export.vehicle.base.event;

import com.alibaba.fastjson2.JSON;
import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
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
 * 应付清单同步SAP事件处理器：
 * 反序列化 dataParam -> 组装 SAP 契约请求（IV_SYSID + IM_TAB）-> 调 SapGateway
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

        // 构建SAP请求参数
        PayableListSyncSapReq req = buildRequest(dto);

        // 调用SAP接口
        PayableListSyncSapResult result = sapGateway.syncPayableList(req);
        log.info("应付清单同步SAP结果: bizNo={}, result={}", task.getBizNo(), result);

        // 判断SAP调用结果是否成功
        if (!isSapSuccess(result)) {
            throw new BizException("应付清单同步SAP失败" + buildFailReason(result));
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
     * 构建 SAP 契约请求：IV_SYSID 默认 DMS；IM_TAB 每行 = PAYNR(清单编号)/PORDER(红旗PO)/SORDER(销服SO)/VHVIN(VIN)
     */
    private PayableListSyncSapReq buildRequest(PayableListForSapDto dto) {
        PayableListSyncSapReq req = new PayableListSyncSapReq();
        req.setImTab(convertDetails(dto.getPayableListNo(), dto.getDetails()));
        return req;
    }

    /**
     * 转换明细列表为 SAP 行项目（清单编号逐行携带）
     */
    private List<PayableListSyncSapReq.ImTabItem> convertDetails(String payableListNo,
            List<PayableListForSapDto.PayableListDetailForSapDto> dtoDetails) {
        if (dtoDetails == null || dtoDetails.isEmpty()) {
            return Collections.emptyList();
        }

        return dtoDetails.stream().map(dto -> {
            PayableListSyncSapReq.ImTabItem item = new PayableListSyncSapReq.ImTabItem();
            item.setPaynr(payableListNo);
            item.setPorder(dto.getPoNo());
            item.setSorder(dto.getSoNo());
            item.setVhvin(dto.getVin());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 判断 SAP 调用结果是否成功：只看总状态 ES_RET.RET_CODE = S；
     * 总状态成功时无需解析 RET_TAB 明细状态。
     */
    private boolean isSapSuccess(PayableListSyncSapResult result) {
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
