package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderGuaranteeQo;
import java.util.Collection;
import java.util.List;

/**
 * PO-保函关联表(一个PO多保函)领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface PurchaseOrderGuaranteeGateway  {

    /**
     * 新增PurchaseOrderGuarantee
     *
     * @param purchaseOrderGuaranteeDo
     * @return 结果
     */
    String addPurchaseOrderGuarantee(PurchaseOrderGuaranteeDo purchaseOrderGuaranteeDo);

    /**
     * 新增PurchaseOrderGuarantee
     *
     * @param purchaseOrderGuaranteeDos
     * @return 结果
     */
    int addBatchPurchaseOrderGuarantee(List<PurchaseOrderGuaranteeDo> purchaseOrderGuaranteeDos);

    /**
     * 修改PurchaseOrderGuarantee
     *
     * @param purchaseOrderGuaranteeDo
     * @return 结果
     */
    String updatePurchaseOrderGuarantee(PurchaseOrderGuaranteeDo purchaseOrderGuaranteeDo);

    /**
     * 查询PurchaseOrderGuarantee
     *
     * @param id
     * @return 结果
     */
    PurchaseOrderGuaranteeDo getPurchaseOrderGuaranteeById(String id);

    /**
     * 唯一查询PurchaseOrderGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    PurchaseOrderGuaranteeDo getOnePurchaseOrderGuarantee(PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 列表查询PurchaseOrderGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    List<PurchaseOrderGuaranteeDo> listPurchaseOrderGuarantee(PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 分页查询PurchaseOrderGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    PageResult<PurchaseOrderGuaranteeDo> pagePurchaseOrderGuarantee(PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 删除PurchaseOrderGuarantee
     *
     * @param id
     * @return 结果
     */
     boolean deletePurchaseOrderGuaranteeById(String id);

    /**
     * 批量删除PurchaseOrderGuarantee
     *
     * @param ids
     * @return 结果
     */
    boolean deletePurchaseOrderGuaranteeByIds(Collection<String> ids);

    /**
     * 启用/禁用PurchaseOrderGuarantee
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisablePurchaseOrderGuaranteeByIds(LpBaseParam param);
}
