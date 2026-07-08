package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * PurchaseOrder 写操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class PurchaseOrderCmdExe {

    private PurchaseOrderGateway purchaseOrderGateway;


    public String addOrUpdatePurchaseOrder(PurchaseOrderDo purchaseOrderDo) {
        return purchaseOrderDo.getId() == null
                    ? purchaseOrderGateway.addPurchaseOrder(purchaseOrderDo)
                    : purchaseOrderGateway.updatePurchaseOrder(purchaseOrderDo);
    }
    public boolean deletePurchaseOrderByIds(List<String> ids) {
        return purchaseOrderGateway.deletePurchaseOrderByIds(ids);
    }
    public boolean enableOrDisablePurchaseOrderByIds(LpBaseParam param) {
        return purchaseOrderGateway.enableOrDisablePurchaseOrderByIds(param);
    }
}
