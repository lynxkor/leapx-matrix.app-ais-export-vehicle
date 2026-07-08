package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * PurchaseOrder查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class PurchaseOrderQryExe {

    private PurchaseOrderGateway purchaseOrderGateway;


    public PageResult<PurchaseOrderDo> pagePurchaseOrder(PurchaseOrderParam purchaseOrderParam) {
        PurchaseOrderQo purchaseOrderQo = PurchaseOrderDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderParam);
        return purchaseOrderGateway.pagePurchaseOrder(purchaseOrderQo);
    }
}
