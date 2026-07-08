package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderQo;
import java.util.Collection;
import java.util.List;

/**
 * PO订单主表领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface PurchaseOrderGateway  {

    /**
     * 新增PurchaseOrder
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String addPurchaseOrder(PurchaseOrderDo purchaseOrderDo);

    /**
     * 新增PurchaseOrder
     *
     * @param purchaseOrderDos
     * @return 结果
     */
    int addBatchPurchaseOrder(List<PurchaseOrderDo> purchaseOrderDos);

    /**
     * 修改PurchaseOrder
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String updatePurchaseOrder(PurchaseOrderDo purchaseOrderDo);

    /**
     * 查询PurchaseOrder
     *
     * @param id
     * @return 结果
     */
    PurchaseOrderDo getPurchaseOrderById(String id);

    /**
     * 唯一查询PurchaseOrder
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    PurchaseOrderDo getOnePurchaseOrder(PurchaseOrderQo purchaseOrderQo);

    /**
     * 列表查询PurchaseOrder
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    List<PurchaseOrderDo> listPurchaseOrder(PurchaseOrderQo purchaseOrderQo);

    /**
     * 分页查询PurchaseOrder
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    PageResult<PurchaseOrderDo> pagePurchaseOrder(PurchaseOrderQo purchaseOrderQo);

    /**
     * 删除PurchaseOrder
     *
     * @param id
     * @return 结果
     */
     boolean deletePurchaseOrderById(String id);

    /**
     * 批量删除PurchaseOrder
     *
     * @param ids
     * @return 结果
     */
    boolean deletePurchaseOrderByIds(Collection<String> ids);

    /**
     * 启用/禁用PurchaseOrder
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisablePurchaseOrderByIds(LpBaseParam param);
}
