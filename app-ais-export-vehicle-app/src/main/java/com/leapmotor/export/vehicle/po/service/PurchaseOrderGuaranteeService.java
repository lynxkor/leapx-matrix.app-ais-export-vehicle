package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeParam;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * PO-保函关联表(一个PO多保函) 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface PurchaseOrderGuaranteeService {

    /**
     * 查询PurchaseOrderGuarantee
     *
     * @param id
     * @return PurchaseOrderGuarantee
     */
    PurchaseOrderGuaranteeResp getPurchaseOrderGuaranteeById(String id);

    /**
     * 查询PurchaseOrderGuarantee列表
     *
     * @param purchaseOrderGuaranteeParam
     * @return PurchaseOrderGuarantee集合
     */
    List<PurchaseOrderGuaranteeResp> listPurchaseOrderGuarantee(PurchaseOrderGuaranteeParam purchaseOrderGuaranteeParam);

    /**
     * 分页查询PurchaseOrderGuarantee列表
     *
     * @param purchaseOrderGuaranteeParam
     * @return PurchaseOrderGuarantee集合
     */
    PageResult<PurchaseOrderGuaranteeResp> pagePurchaseOrderGuarantee(PurchaseOrderGuaranteeParam purchaseOrderGuaranteeParam);

    /**
     * 新增/编辑PurchaseOrderGuarantee
     *
     * @param purchaseOrderGuaranteeAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdatePurchaseOrderGuarantee(PurchaseOrderGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq);


    /**
     * 删除PurchaseOrderGuarantee信息
     *
     * @param ids
     * @return 结果
     */
    boolean deletePurchaseOrderGuaranteeByIds(List<String> ids);


    /**
     * 启用/禁用PurchaseOrderGuarantee信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisablePurchaseOrderGuaranteeByIds(LpBaseParam param);

}
