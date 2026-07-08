package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * PurchaseOrderDetail 写操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class PurchaseOrderDetailCmdExe {

    private PurchaseOrderDetailGateway purchaseOrderDetailGateway;


    public String addOrUpdatePurchaseOrderDetail(PurchaseOrderDetailDo purchaseOrderDetailDo) {
        return purchaseOrderDetailDo.getId() == null
                    ? purchaseOrderDetailGateway.addPurchaseOrderDetail(purchaseOrderDetailDo)
                    : purchaseOrderDetailGateway.updatePurchaseOrderDetail(purchaseOrderDetailDo);
    }
    public boolean deletePurchaseOrderDetailByIds(List<String> ids) {
        return purchaseOrderDetailGateway.deletePurchaseOrderDetailByIds(ids);
    }
    public boolean enableOrDisablePurchaseOrderDetailByIds(LpBaseParam param) {
        return purchaseOrderDetailGateway.enableOrDisablePurchaseOrderDetailByIds(param);
    }
}
