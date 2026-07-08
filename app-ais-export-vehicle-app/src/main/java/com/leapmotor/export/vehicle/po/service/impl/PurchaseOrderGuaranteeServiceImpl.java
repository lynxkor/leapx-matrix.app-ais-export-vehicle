package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.PurchaseOrderGuaranteeCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.PurchaseOrderGuaranteeQryExe;
import com.leapmotor.export.vehicle.po.convertor.PurchaseOrderGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeParam;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderGuaranteeQo;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;
import com.leapmotor.export.vehicle.po.resp.PurchaseOrderGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.PurchaseOrderGuaranteeAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.PurchaseOrderGuaranteeService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGuaranteeGateway;
/**
 * <p>
 * PO-保函关联表(一个PO多保函) 服务实现类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class PurchaseOrderGuaranteeServiceImpl implements PurchaseOrderGuaranteeService {


    private PurchaseOrderGuaranteeCmdExe purchaseOrderGuaranteeCmdExe;
    private PurchaseOrderGuaranteeQryExe purchaseOrderGuaranteeQryExe;
    private PurchaseOrderGuaranteeGateway purchaseOrderGuaranteeGateway;

    @Override
    public PurchaseOrderGuaranteeResp getPurchaseOrderGuaranteeById(String id) {
        PurchaseOrderGuaranteeDo purchaseOrderGuaranteeDo = purchaseOrderGuaranteeGateway.getPurchaseOrderGuaranteeById(id);
        return PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.domainToResp(purchaseOrderGuaranteeDo);
    }

    @Override
    public List<PurchaseOrderGuaranteeResp> listPurchaseOrderGuarantee(PurchaseOrderGuaranteeParam purchaseOrderGuaranteeParam) {
    PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderGuaranteeParam);
        List<PurchaseOrderGuaranteeDo> purchaseOrderGuaranteeDoList =  purchaseOrderGuaranteeGateway.listPurchaseOrderGuarantee(purchaseOrderGuaranteeQo);
        return PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderGuaranteeDoList);
    }

    @Override
    public PageResult<PurchaseOrderGuaranteeResp> pagePurchaseOrderGuarantee(PurchaseOrderGuaranteeParam purchaseOrderGuaranteeParam) {
        PageResult<PurchaseOrderGuaranteeDo> page = purchaseOrderGuaranteeQryExe.pagePurchaseOrderGuarantee(purchaseOrderGuaranteeParam);
        return PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdatePurchaseOrderGuarantee(PurchaseOrderGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq) {
        PurchaseOrderGuaranteeDo purchaseOrderGuaranteeDo = PurchaseOrderGuaranteeDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderGuaranteeAddOrUpdateReq);
        return purchaseOrderGuaranteeCmdExe.addOrUpdatePurchaseOrderGuarantee(purchaseOrderGuaranteeDo);
    }

    @Override
    public boolean deletePurchaseOrderGuaranteeByIds(List<String> ids) {
        return purchaseOrderGuaranteeCmdExe.deletePurchaseOrderGuaranteeByIds(ids);
    }

    @Override
    public boolean enableOrDisablePurchaseOrderGuaranteeByIds(LpBaseParam param) {
        return purchaseOrderGuaranteeCmdExe.enableOrDisablePurchaseOrderGuaranteeByIds(param);
    }
}
