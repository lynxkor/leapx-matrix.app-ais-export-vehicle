package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderDetailQo;
import java.util.Collection;
import java.util.List;

/**
 * PO订单明细表领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface PurchaseOrderDetailGateway  {

    /**
     * 新增PurchaseOrderDetail
     *
     * @param purchaseOrderDetailDo
     * @return 结果
     */
    String addPurchaseOrderDetail(PurchaseOrderDetailDo purchaseOrderDetailDo);

    /**
     * 新增PurchaseOrderDetail
     *
     * @param purchaseOrderDetailDos
     * @return 结果
     */
    int addBatchPurchaseOrderDetail(List<PurchaseOrderDetailDo> purchaseOrderDetailDos);

    /**
     * 修改PurchaseOrderDetail
     *
     * @param purchaseOrderDetailDo
     * @return 结果
     */
    String updatePurchaseOrderDetail(PurchaseOrderDetailDo purchaseOrderDetailDo);

    /**
     * 查询PurchaseOrderDetail
     *
     * @param id
     * @return 结果
     */
    PurchaseOrderDetailDo getPurchaseOrderDetailById(String id);

    /**
     * 唯一查询PurchaseOrderDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    PurchaseOrderDetailDo getOnePurchaseOrderDetail(PurchaseOrderDetailQo purchaseOrderDetailQo);

    /**
     * 列表查询PurchaseOrderDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    List<PurchaseOrderDetailDo> listPurchaseOrderDetail(PurchaseOrderDetailQo purchaseOrderDetailQo);

    /**
     * 分页查询PurchaseOrderDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    PageResult<PurchaseOrderDetailDo> pagePurchaseOrderDetail(PurchaseOrderDetailQo purchaseOrderDetailQo);

    /**
     * 删除PurchaseOrderDetail
     *
     * @param id
     * @return 结果
     */
     boolean deletePurchaseOrderDetailById(String id);

    /**
     * 批量删除PurchaseOrderDetail
     *
     * @param ids
     * @return 结果
     */
    boolean deletePurchaseOrderDetailByIds(Collection<String> ids);

    /**
     * 启用/禁用PurchaseOrderDetail
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisablePurchaseOrderDetailByIds(LpBaseParam param);
}
