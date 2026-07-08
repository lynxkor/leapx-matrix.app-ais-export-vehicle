package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.PurchaseOrderDetailCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.PurchaseOrderDetailQryExe;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailParam;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderDetailQo;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderDetailResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderDetailAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderDetailService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderDetailGateway;
/**
 * <p>
 * PO订单明细表 服务实现类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class PurchaseOrderDetailServiceImpl implements PurchaseOrderDetailService {


    private PurchaseOrderDetailCmdExe purchaseOrderDetailCmdExe;
    private PurchaseOrderDetailQryExe purchaseOrderDetailQryExe;
    private PurchaseOrderDetailGateway purchaseOrderDetailGateway;

    @Override
    public PurchaseOrderDetailResp getPurchaseOrderDetailById(String id) {
        PurchaseOrderDetailDo purchaseOrderDetailDo = purchaseOrderDetailGateway.getPurchaseOrderDetailById(id);
        return PurchaseOrderDetailDTOStructMapper.INSTANCE.domainToResp(purchaseOrderDetailDo);
    }

    @Override
    public List<PurchaseOrderDetailResp> listPurchaseOrderDetail(PurchaseOrderDetailParam purchaseOrderDetailParam) {
    PurchaseOrderDetailQo purchaseOrderDetailQo = PurchaseOrderDetailDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderDetailParam);
        List<PurchaseOrderDetailDo> purchaseOrderDetailDoList =  purchaseOrderDetailGateway.listPurchaseOrderDetail(purchaseOrderDetailQo);
        return PurchaseOrderDetailDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderDetailDoList);
    }

    @Override
    public PageResult<PurchaseOrderDetailResp> pagePurchaseOrderDetail(PurchaseOrderDetailParam purchaseOrderDetailParam) {
        PageResult<PurchaseOrderDetailDo> page = purchaseOrderDetailQryExe.pagePurchaseOrderDetail(purchaseOrderDetailParam);
        return PurchaseOrderDetailDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdatePurchaseOrderDetail(PurchaseOrderDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq) {
        PurchaseOrderDetailDo purchaseOrderDetailDo = PurchaseOrderDetailDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderDetailAddOrUpdateReq);
        return purchaseOrderDetailCmdExe.addOrUpdatePurchaseOrderDetail(purchaseOrderDetailDo);
    }

    @Override
    public boolean deletePurchaseOrderDetailByIds(List<String> ids) {
        return purchaseOrderDetailCmdExe.deletePurchaseOrderDetailByIds(ids);
    }

    @Override
    public boolean enableOrDisablePurchaseOrderDetailByIds(LpBaseParam param) {
        return purchaseOrderDetailCmdExe.enableOrDisablePurchaseOrderDetailByIds(param);
    }
}
