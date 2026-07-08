package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderSaveReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderSubmitReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderRejectReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderSupplyRejectReq;

import java.util.List;

/**
 * <p>
 * PO订单主表 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface PurchaseOrderService {

    PurchaseOrderResp getPurchaseOrderById(String id);

    List<PurchaseOrderResp> listPurchaseOrder(PurchaseOrderParam purchaseOrderParam);

    PageResult<PurchaseOrderResp> pagePurchaseOrder(PurchaseOrderParam purchaseOrderParam);

    String addOrUpdatePurchaseOrder(PurchaseOrderAddOrUpdateReq purchaseOrderAddOrUpdateReq);

    boolean deletePurchaseOrderByIds(List<String> ids);

    boolean enableOrDisablePurchaseOrderByIds(LpBaseParam param);

    /** 4.1 红旗CBU推送创建草稿订单 */
    String createFromHongqi(HongqiCbuPurchaseOrderReq req);

    /** 4.2 草稿态编辑保存（仅销售公司 + 明细生产备注） */
    void savePurchaseOrder(PurchaseOrderSaveReq req);

    /** 4.3 销售提交 */
    void submitPurchaseOrder(PurchaseOrderSubmitReq req);

    /** 4.3 销售驳回 */
    void rejectPurchaseOrder(PurchaseOrderRejectReq req);

    /** 4.4 供需审核通过（生成 SO 单） */
    void supplyApprove(PurchaseOrderSupplyApproveReq req);

    /** 4.4 供需审核驳回（解冻保函 + 推红旗） */
    void supplyReject(PurchaseOrderSupplyRejectReq req);

}
