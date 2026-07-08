package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeParam;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderGuaranteeQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * PurchaseOrderGuarantee查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class PurchaseOrderGuaranteeQryExe {

    private PurchaseOrderGuaranteeGateway purchaseOrderGuaranteeGateway;


    public PageResult<PurchaseOrderGuaranteeDo> pagePurchaseOrderGuarantee(PurchaseOrderGuaranteeParam purchaseOrderGuaranteeParam) {
        PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderGuaranteeParam);
        return purchaseOrderGuaranteeGateway.pagePurchaseOrderGuarantee(purchaseOrderGuaranteeQo);
    }
}
