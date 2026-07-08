package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderSaveReq;

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

    /**
     * 查询PurchaseOrder
     */
    PurchaseOrderResp getPurchaseOrderById(String id);

    /**
     * 查询PurchaseOrder列表
     */
    List<PurchaseOrderResp> listPurchaseOrder(PurchaseOrderParam purchaseOrderParam);

    /**
     * 分页查询PurchaseOrder列表
     */
    PageResult<PurchaseOrderResp> pagePurchaseOrder(PurchaseOrderParam purchaseOrderParam);

    /**
     * 新增/编辑PurchaseOrder
     */
    String addOrUpdatePurchaseOrder(PurchaseOrderAddOrUpdateReq purchaseOrderAddOrUpdateReq);

    /**
     * 删除PurchaseOrder信息
     */
    boolean deletePurchaseOrderByIds(List<String> ids);

    /**
     * 启用/禁用PurchaseOrder信息
     */
    boolean enableOrDisablePurchaseOrderByIds(LpBaseParam param);

    /**
     * 4.1 红旗CBU推送创建草稿订单
     */
    String createFromHongqi(HongqiCbuPurchaseOrderReq req);

    /**
     * 4.2 草稿态编辑保存（仅销售公司 + 明细生产备注）
     */
    void savePurchaseOrder(PurchaseOrderSaveReq req);

}
