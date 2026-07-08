package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.PurchaseOrderCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.PurchaseOrderQryExe;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderParam;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderQo;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGateway;
/**
 * <p>
 * PO订单主表 服务实现类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {


    private PurchaseOrderCmdExe purchaseOrderCmdExe;
    private PurchaseOrderQryExe purchaseOrderQryExe;
    private PurchaseOrderGateway purchaseOrderGateway;

    @Override
    public PurchaseOrderResp getPurchaseOrderById(String id) {
        PurchaseOrderDo purchaseOrderDo = purchaseOrderGateway.getPurchaseOrderById(id);
        return PurchaseOrderDTOStructMapper.INSTANCE.domainToResp(purchaseOrderDo);
    }

    @Override
    public List<PurchaseOrderResp> listPurchaseOrder(PurchaseOrderParam purchaseOrderParam) {
    PurchaseOrderQo purchaseOrderQo = PurchaseOrderDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderParam);
        List<PurchaseOrderDo> purchaseOrderDoList =  purchaseOrderGateway.listPurchaseOrder(purchaseOrderQo);
        return PurchaseOrderDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderDoList);
    }

    @Override
    public PageResult<PurchaseOrderResp> pagePurchaseOrder(PurchaseOrderParam purchaseOrderParam) {
        PageResult<PurchaseOrderDo> page = purchaseOrderQryExe.pagePurchaseOrder(purchaseOrderParam);
        return PurchaseOrderDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdatePurchaseOrder(PurchaseOrderAddOrUpdateReq purchaseOrderAddOrUpdateReq) {
        PurchaseOrderDo purchaseOrderDo = PurchaseOrderDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderAddOrUpdateReq);
        return purchaseOrderCmdExe.addOrUpdatePurchaseOrder(purchaseOrderDo);
    }

    @Override
    public boolean deletePurchaseOrderByIds(List<String> ids) {
        return purchaseOrderCmdExe.deletePurchaseOrderByIds(ids);
    }

    @Override
    public boolean enableOrDisablePurchaseOrderByIds(LpBaseParam param) {
        return purchaseOrderCmdExe.enableOrDisablePurchaseOrderByIds(param);
    }

    @Override
    public String createFromHongqi(HongqiCbuPurchaseOrderReq req) {
        return purchaseOrderCmdExe.createFromHongqi(req);
    }
}
