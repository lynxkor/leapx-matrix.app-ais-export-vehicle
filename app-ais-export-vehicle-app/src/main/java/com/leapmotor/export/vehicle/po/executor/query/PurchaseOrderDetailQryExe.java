package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailParam;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderDetailQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * PurchaseOrderDetail查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class PurchaseOrderDetailQryExe {

    private PurchaseOrderDetailGateway purchaseOrderDetailGateway;


    public PageResult<PurchaseOrderDetailDo> pagePurchaseOrderDetail(PurchaseOrderDetailParam purchaseOrderDetailParam) {
        PurchaseOrderDetailQo purchaseOrderDetailQo = PurchaseOrderDetailDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderDetailParam);
        return purchaseOrderDetailGateway.pagePurchaseOrderDetail(purchaseOrderDetailQo);
    }
}
