package com.leapmotor.export.vehicle.payablelist.service;

import com.leapmotor.export.vehicle.acl.rpc.sap.gateway.SapGateway;
import com.leapmotor.export.vehicle.acl.rpc.sap.req.PayableListSyncSapReq;
import com.leapmotor.export.vehicle.acl.rpc.sap.resp.PayableListSyncSapResult;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SAP 开放接口辅助服务（手动补推应付清单到 SAP，供排查问题/补推数据使用）
 *
 * @author huangtao
 * @since 2026-09-14
 */
@Slf4j
@Service
@AllArgsConstructor
public class SapPayableListOpenService {

    private final SapGateway sapGateway;
    private final PayableListGateway payableListGateway;
    private final PayableListDetailGateway payableListDetailGateway;

    /**
     * 按应付清单编号手动补推SAP
     *
     * @param payableListNo 应付清单编号
     * @return SAP 返回（ES_RET 总状态 + RET_TAB 明细状态，RET_CODE=S 成功）
     */
    public PayableListSyncSapResult syncPayableListByNo(String payableListNo) {
        PayableListDo payableList = payableListGateway.getPayableListByNo(payableListNo);
        if (payableList == null) {
            throw new BizException("应付清单不存在: " + payableListNo);
        }
        List<PayableListDetailDo> detailList = payableListDetailGateway.listByPayableListId(payableList.getId());

        PayableListSyncSapReq req = buildRequest(payableList, detailList);
        log.info("应付清单手动补推SAP, payableListNo={}, req={}", payableListNo, req);

        PayableListSyncSapResult result = sapGateway.syncPayableList(req);
        log.info("应付清单手动补推SAP结果, payableListNo={}, result={}", payableListNo, result);
        return result;
    }

    /**
     * 构建SAP参数
     */
    private PayableListSyncSapReq buildRequest(PayableListDo payableList, List<PayableListDetailDo> detailList) {
        PayableListSyncSapReq req = new PayableListSyncSapReq();
        if (detailList == null || detailList.isEmpty()) {
            return req;
        }
        List<PayableListSyncSapReq.ImTabItem> rows = detailList.stream().map(detail -> {
            PayableListSyncSapReq.ImTabItem item = new PayableListSyncSapReq.ImTabItem();
            item.setPaynr(payableList.getPayableListNo());
            item.setPorder(detail.getPoNo());
            item.setSorder(detail.getSoNo());
            item.setVhvin(detail.getVin());
            return item;
        }).collect(Collectors.toList());
        req.setImTab(rows);
        return req;
    }
}