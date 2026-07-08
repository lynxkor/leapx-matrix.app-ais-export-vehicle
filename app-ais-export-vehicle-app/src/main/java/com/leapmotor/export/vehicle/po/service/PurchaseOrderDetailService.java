package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailParam;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderDetailResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * PO订单明细表 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface PurchaseOrderDetailService {

    /**
     * 查询PurchaseOrderDetail
     *
     * @param id
     * @return PurchaseOrderDetail
     */
    PurchaseOrderDetailResp getPurchaseOrderDetailById(String id);

    /**
     * 查询PurchaseOrderDetail列表
     *
     * @param purchaseOrderDetailParam
     * @return PurchaseOrderDetail集合
     */
    List<PurchaseOrderDetailResp> listPurchaseOrderDetail(PurchaseOrderDetailParam purchaseOrderDetailParam);

    /**
     * 分页查询PurchaseOrderDetail列表
     *
     * @param purchaseOrderDetailParam
     * @return PurchaseOrderDetail集合
     */
    PageResult<PurchaseOrderDetailResp> pagePurchaseOrderDetail(PurchaseOrderDetailParam purchaseOrderDetailParam);

    /**
     * 新增/编辑PurchaseOrderDetail
     *
     * @param purchaseOrderDetailAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdatePurchaseOrderDetail(PurchaseOrderDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq);


    /**
     * 删除PurchaseOrderDetail信息
     *
     * @param ids
     * @return 结果
     */
    boolean deletePurchaseOrderDetailByIds(List<String> ids);


    /**
     * 启用/禁用PurchaseOrderDetail信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisablePurchaseOrderDetailByIds(LpBaseParam param);

}
