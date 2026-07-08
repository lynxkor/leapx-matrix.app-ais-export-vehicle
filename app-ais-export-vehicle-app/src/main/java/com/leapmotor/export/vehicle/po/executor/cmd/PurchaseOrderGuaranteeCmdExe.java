package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * PurchaseOrderGuarantee 写操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class PurchaseOrderGuaranteeCmdExe {

    private PurchaseOrderGuaranteeGateway purchaseOrderGuaranteeGateway;


    public String addOrUpdatePurchaseOrderGuarantee(PurchaseOrderGuaranteeDo purchaseOrderGuaranteeDo) {
        return purchaseOrderGuaranteeDo.getId() == null
                    ? purchaseOrderGuaranteeGateway.addPurchaseOrderGuarantee(purchaseOrderGuaranteeDo)
                    : purchaseOrderGuaranteeGateway.updatePurchaseOrderGuarantee(purchaseOrderGuaranteeDo);
    }
    public boolean deletePurchaseOrderGuaranteeByIds(List<String> ids) {
        return purchaseOrderGuaranteeGateway.deletePurchaseOrderGuaranteeByIds(ids);
    }
    public boolean enableOrDisablePurchaseOrderGuaranteeByIds(LpBaseParam param) {
        return purchaseOrderGuaranteeGateway.enableOrDisablePurchaseOrderGuaranteeByIds(param);
    }
}
