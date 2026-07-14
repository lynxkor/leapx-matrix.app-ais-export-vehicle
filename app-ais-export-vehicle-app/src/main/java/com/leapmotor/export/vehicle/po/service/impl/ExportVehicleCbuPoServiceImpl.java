package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportVehicleCbuPoApprovalCmdExe;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportVehicleCbuPoCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.ExportVehicleCbuPoQryExe;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDTOStructMapper;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailFullResp;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoService;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class ExportVehicleCbuPoServiceImpl implements ExportVehicleCbuPoService {

    private ExportVehicleCbuPoCmdExe purchaseOrderCmdExe;
    private ExportVehicleCbuPoApprovalCmdExe purchaseOrderApprovalCmdExe;
    private ExportVehicleCbuPoQryExe purchaseOrderQryExe;
    private ExportVehicleCbuPoGateway purchaseOrderGateway;
    private ExportVehicleCbuPoDetailGateway purchaseOrderDetailGateway;
    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;

    @Override
    public ExportVehicleCbuPoResp getExportVehicleCbuPoById(String id) {
        ExportVehicleCbuPoDo purchaseOrderDo = purchaseOrderGateway.getExportVehicleCbuPoById(id);
        return ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainToResp(purchaseOrderDo);
    }

    @Override
    public ExportVehicleCbuPoDetailFullResp getExportVehicleCbuPoDetailById(String id) {
        ExportVehicleCbuPoDo order = purchaseOrderGateway.getExportVehicleCbuPoById(id);
        if (order == null) {
            return null;
        }
        ExportVehicleCbuPoDetailQo detailQo = new ExportVehicleCbuPoDetailQo();
        detailQo.setPoNo(order.getPoNo());
        List<ExportVehicleCbuPoDetailDo> detailDos = purchaseOrderDetailGateway.listExportVehicleCbuPoDetail(detailQo);

        ExportVehicleCbuPoGuaranteeQo guaranteeQo = new ExportVehicleCbuPoGuaranteeQo();
        guaranteeQo.setPoNo(order.getPoNo());
        List<ExportVehicleCbuPoGuaranteeDo> guaranteeDos = purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(guaranteeQo);

        ExportVehicleCbuPoDetailFullResp resp = new ExportVehicleCbuPoDetailFullResp();
        resp.setOrder(ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainToResp(order));
        resp.setDetails(ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.domainsToResps(detailDos));
        resp.setGuarantees(ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainsToResps(guaranteeDos));
        return resp;
    }

    @Override
    public List<ExportVehicleCbuPoResp> listExportVehicleCbuPo(ExportVehicleCbuPoParam purchaseOrderParam) {
        ExportVehicleCbuPoQo purchaseOrderQo = ExportVehicleCbuPoDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderParam);
        List<ExportVehicleCbuPoDo> purchaseOrderDoList = purchaseOrderGateway.listExportVehicleCbuPo(purchaseOrderQo);
        return ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderDoList);
    }

    @Override
    public PageResult<ExportVehicleCbuPoResp> pageExportVehicleCbuPo(ExportVehicleCbuPoParam purchaseOrderParam) {
        PageResult<ExportVehicleCbuPoDo> page = purchaseOrderQryExe.pageExportVehicleCbuPo(purchaseOrderParam);
        return ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportVehicleCbuPo(ExportVehicleCbuPoAddOrUpdateReq purchaseOrderAddOrUpdateReq) {
        ExportVehicleCbuPoDo purchaseOrderDo = ExportVehicleCbuPoDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderAddOrUpdateReq);
        return purchaseOrderCmdExe.addOrUpdateExportVehicleCbuPo(purchaseOrderDo);
    }

    @Override
    public boolean deleteExportVehicleCbuPoByIds(List<String> ids) {
        return purchaseOrderCmdExe.deleteExportVehicleCbuPoByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleCbuPoByIds(LpBaseParam param) {
        return purchaseOrderCmdExe.enableOrDisableExportVehicleCbuPoByIds(param);
    }

    @Override
    public String createFromHongqi(HongqiCbuPurchaseOrderReq req) {
        return purchaseOrderCmdExe.createFromHongqi(req);
    }

    @Override
    public void saveExportVehicleCbuPo(ExportVehicleCbuPoSaveReq req) {
        purchaseOrderCmdExe.saveExportVehicleCbuPo(req);
    }

    @Override
    public void submitExportVehicleCbuPo(ExportVehicleCbuPoSubmitReq req) {
        purchaseOrderApprovalCmdExe.submitExportVehicleCbuPo(req);
    }

    @Override
    public void rejectExportVehicleCbuPo(ExportVehicleCbuPoRejectReq req) {
        purchaseOrderApprovalCmdExe.rejectExportVehicleCbuPo(req);
    }

    @Override
    public void supplyApprove(ExportVehicleCbuPoSupplyApproveReq req) {
        purchaseOrderApprovalCmdExe.supplyApprove(req);
    }

    @Override
    public void supplyReject(ExportVehicleCbuPoSupplyRejectReq req) {
        purchaseOrderApprovalCmdExe.supplyReject(req);
    }

    @Override
    public void balanceApprove(ExportVehicleCbuPoBalanceApproveReq req) {
        purchaseOrderApprovalCmdExe.balanceApprove(req);
    }

    @Override
    public void balanceReject(ExportVehicleCbuPoBalanceRejectReq req) {
        purchaseOrderApprovalCmdExe.balanceReject(req);
    }
}
