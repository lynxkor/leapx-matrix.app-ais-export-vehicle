package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.base.event.EventHandlerHelper;
import com.leapmotor.export.vehicle.base.event.PayableListSyncSapHandler;
import com.leapmotor.export.vehicle.base.event.enums.SapEventTypeEnum;
import com.leapmotor.export.vehicle.base.event.enums.ThirdTargetPlatformEnum;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.enums.SyncResultEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * SAP 开放接口（手动补推应付清单到 SAP）
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Slf4j
@Service
@AllArgsConstructor
public class SapPayableListOpenService {

    private final PayableListGateway payableListGateway;
    private final EventHandlerHelper eventHandlerHelper;
    private final SapGateway sapGateway;
    private final PayableListSyncSapHandler payableListSyncSapHandler;

    /**
     * 按应付清单编号补推SAP：校验通过后重发同步事件。
     *
     * @param payableListNo 应付清单编号
     * @param force         是否强制重推：已同步成功的清单默认拒绝，置 true 才允许再推
     * @return 受理结论与结果查询指引
     */
    public String syncPayableListByNo(String payableListNo, boolean force) {
        PayableListDo payableList = checkAndGetForPush(payableListNo, force);

        eventHandlerHelper.publishAsync(
            ThirdTargetPlatformEnum.SAP,
            SapEventTypeEnum.PAYABLE_LIST_SYNC_SAP_SUBMIT,
            payableListNo,
            (Object) null
        );

        log.info("应付清单补推SAP-事件已发布, payableListNo={}, force={}, 前次同步结果={}",
                payableListNo, force, payableList.getSyncSapResult());

        // 异步链路拿不到 SAP 返回，提示语必须自带结果与留痕的查询路径，否则补推后无人知道去哪核对
        return "补推事件已发布（立即异步执行），清单号=" + payableListNo
                + "；结果请查 export_vehicle_payable_list.sync_sap_result/sync_sap_message"
                + "；事件留痕请查 event_notify_task（biz_no=清单号、type=PAYABLE_LIST_SYNC_SAP_SUBMIT）";
    }

    /**
     * 直连补推SAP：同步调 SAP 接口并原样返回其结果。
     *
     * @param payableListNo 应付清单编号
     * @param force         是否强制重推：已同步成功的清单默认拒，置 true 才允许再推
     * @return SAP 返回（ES_RET 总状态 + RET_TAB 逐行状态）
     */
    public PayableListSyncSapResult syncPayableListDirect(String payableListNo, boolean force) {
        PayableListDo payableList = checkAndGetForPush(payableListNo, force);

        PayableListSyncSapReq req = payableListSyncSapHandler.assembleFromDb(payableList);
        log.info("应付清单直连补推SAP-开始, payableListNo={}, rows={}",
                payableListNo, req.getImTab() == null ? 0 : req.getImTab().size());

        PayableListSyncSapResult result = sapGateway.syncPayableList(req);
        log.info("应付清单直连补推SAP-结束, payableListNo={}, sapSuccess={}",
                payableListNo, PayableListSyncSapHandler.isSyncSuccess(result));

        // 结果回写
        payableListSyncSapHandler.writeBackSyncResult(payableList, result);
        return result;
    }

    /**
     * 两个补推入口共用的前置校验。
     */
    private PayableListDo checkAndGetForPush(String payableListNo, boolean force) {
        if (StringUtils.isBlank(payableListNo)) {
            throw new BizException("应付清单编号不能为空");
        }

        PayableListDo payableList = payableListGateway.getPayableListByNo(payableListNo);
        if (payableList == null) {
            throw new BizException("应付清单不存在: " + payableListNo);
        }

        if (!PayableListStatusEnum.CONFIRMED.getCode().equals(payableList.getBillStatus())) {
            throw new BizException("应付清单非已确认状态，不可补推SAP: payableListNo=" + payableListNo
                    + ", billStatus=" + payableList.getBillStatus());
        }

        if (!force && SyncResultEnum.SUCCESS.getCode().equals(payableList.getSyncSapResult())) {
            throw new BizException("应付清单已同步SAP成功，如需重推请传 force=true: payableListNo=" + payableListNo);
        }
        return payableList;
    }
}
