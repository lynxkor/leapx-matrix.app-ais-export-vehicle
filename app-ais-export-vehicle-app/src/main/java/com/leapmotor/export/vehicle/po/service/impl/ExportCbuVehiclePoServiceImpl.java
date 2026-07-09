package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportCbuVehiclePoApprovalCmdExe;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportCbuVehiclePoCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.ExportCbuVehiclePoQryExe;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoParam;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoQo;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDo;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoAddOrUpdateReq;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoService;
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
public class ExportCbuVehiclePoServiceImpl implements ExportCbuVehiclePoService {

    private ExportCbuVehiclePoCmdExe purchaseOrderCmdExe;
    private ExportCbuVehiclePoApprovalCmdExe purchaseOrderApprovalCmdExe;
    private ExportCbuVehiclePoQryExe purchaseOrderQryExe;
    private ExportCbuVehiclePoGateway purchaseOrderGateway;

    @Override
    public ExportCbuVehiclePoResp getExportCbuVehiclePoById(String id) {
        ExportCbuVehiclePoDo purchaseOrderDo = purchaseOrderGateway.getExportCbuVehiclePoById(id);
        return ExportCbuVehiclePoDTOStructMapper.INSTANCE.domainToResp(purchaseOrderDo);
    }

    @Override
    public List<ExportCbuVehiclePoResp> listExportCbuVehiclePo(ExportCbuVehiclePoParam purchaseOrderParam) {
        ExportCbuVehiclePoQo purchaseOrderQo = ExportCbuVehiclePoDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderParam);
        List<ExportCbuVehiclePoDo> purchaseOrderDoList = purchaseOrderGateway.listExportCbuVehiclePo(purchaseOrderQo);
        return ExportCbuVehiclePoDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderDoList);
    }

    @Override
    public PageResult<ExportCbuVehiclePoResp> pageExportCbuVehiclePo(ExportCbuVehiclePoParam purchaseOrderParam) {
        PageResult<ExportCbuVehiclePoDo> page = purchaseOrderQryExe.pageExportCbuVehiclePo(purchaseOrderParam);
        return ExportCbuVehiclePoDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportCbuVehiclePo(ExportCbuVehiclePoAddOrUpdateReq purchaseOrderAddOrUpdateReq) {
        ExportCbuVehiclePoDo purchaseOrderDo = ExportCbuVehiclePoDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderAddOrUpdateReq);
        return purchaseOrderCmdExe.addOrUpdateExportCbuVehiclePo(purchaseOrderDo);
    }

    @Override
    public boolean deleteExportCbuVehiclePoByIds(List<String> ids) {
        return purchaseOrderCmdExe.deleteExportCbuVehiclePoByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportCbuVehiclePoByIds(LpBaseParam param) {
        return purchaseOrderCmdExe.enableOrDisableExportCbuVehiclePoByIds(param);
    }

    @Override
    public String createFromHongqi(HongqiCbuPurchaseOrderReq req) {
        return purchaseOrderCmdExe.createFromHongqi(req);
    }

    @Override
    public void saveExportCbuVehiclePo(ExportCbuVehiclePoSaveReq req) {
        purchaseOrderCmdExe.saveExportCbuVehiclePo(req);
    }

    @Override
    public void submitExportCbuVehiclePo(ExportCbuVehiclePoSubmitReq req) {
        purchaseOrderApprovalCmdExe.submitExportCbuVehiclePo(req);
    }

    @Override
    public void rejectExportCbuVehiclePo(ExportCbuVehiclePoRejectReq req) {
        purchaseOrderApprovalCmdExe.rejectExportCbuVehiclePo(req);
    }

    @Override
    public void supplyApprove(ExportCbuVehiclePoSupplyApproveReq req) {
        purchaseOrderApprovalCmdExe.supplyApprove(req);
    }

    @Override
    public void supplyReject(ExportCbuVehiclePoSupplyRejectReq req) {
        purchaseOrderApprovalCmdExe.supplyReject(req);
    }

    @Override
    public void balanceApprove(ExportCbuVehiclePoBalanceApproveReq req) {
        purchaseOrderApprovalCmdExe.balanceApprove(req);
    }

    @Override
    public void balanceReject(ExportCbuVehiclePoBalanceRejectReq req) {
        purchaseOrderApprovalCmdExe.balanceReject(req);
    }
}
