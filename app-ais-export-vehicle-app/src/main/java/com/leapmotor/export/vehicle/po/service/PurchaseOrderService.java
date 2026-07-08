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

    /** 4.3 销售提交（4 步编排：数据校验→价格匹配→保函冻结→完成） */
    void submitPurchaseOrder(PurchaseOrderSubmitReq req);

    /** 4.3 销售驳回（终态 + 推红旗） */
    void rejectPurchaseOrder(PurchaseOrderRejectReq req);

}
