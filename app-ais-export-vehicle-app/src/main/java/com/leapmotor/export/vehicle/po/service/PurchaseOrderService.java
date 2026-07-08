package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;

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
     *
     * @param id
     * @return PurchaseOrder
     */
    PurchaseOrderResp getPurchaseOrderById(String id);

    /**
     * 查询PurchaseOrder列表
     *
     * @param purchaseOrderParam
     * @return PurchaseOrder集合
     */
    List<PurchaseOrderResp> listPurchaseOrder(PurchaseOrderParam purchaseOrderParam);

    /**
     * 分页查询PurchaseOrder列表
     *
     * @param purchaseOrderParam
     * @return PurchaseOrder集合
     */
    PageResult<PurchaseOrderResp> pagePurchaseOrder(PurchaseOrderParam purchaseOrderParam);

    /**
     * 新增/编辑PurchaseOrder
     *
     * @param purchaseOrderAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdatePurchaseOrder(PurchaseOrderAddOrUpdateReq purchaseOrderAddOrUpdateReq);


    /**
     * 删除PurchaseOrder信息
     *
     * @param ids
     * @return 结果
     */
    boolean deletePurchaseOrderByIds(List<String> ids);


    /**
     * 启用/禁用PurchaseOrder信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisablePurchaseOrderByIds(LpBaseParam param);

    /**
     * 4.1 红旗CBU推送创建草稿订单（主表+明细联保存，匹配类字段留空待提交校验）
     *
     * @param req 红旗CBU订单采购申请
     * @return 创建的 PO 主键ID
     */
    String createFromHongqi(HongqiCbuPurchaseOrderReq req);

}
