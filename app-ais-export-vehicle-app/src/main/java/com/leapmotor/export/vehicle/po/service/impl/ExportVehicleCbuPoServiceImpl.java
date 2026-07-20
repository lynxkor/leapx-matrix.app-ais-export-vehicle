package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.export.nexus.company.api.CompanyApi;
import com.leapmotor.export.nexus.company.request.CompanyRequest;
import com.leapmotor.export.nexus.company.response.CompanyResponse;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportVehicleCbuPoApprovalCmdExe;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportVehicleCbuPoCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.ExportVehicleCbuPoQryExe;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDTOStructMapper;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailFullResp;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoResp;
import com.leapmotor.export.vehicle.po.req.HongqiCbuPurchaseOrderReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSaveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSubmitReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoSupplyRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceApproveReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBalanceRejectReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoBatchReq;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoRefreshPriceReq;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoBatchResp;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private CompanyApi companyApi;

    @Override
    public ExportVehicleCbuPoResp getExportVehicleCbuPoById(String id) {
        ExportVehicleCbuPoDo purchaseOrderDo = purchaseOrderGateway.getExportVehicleCbuPoById(id);
        if (purchaseOrderDo == null) {
            throw new BizException("PO订单不存在");
        }
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
        PageResult<ExportVehicleCbuPoResp> result = ExportVehicleCbuPoDTOStructMapper.INSTANCE.domainsToRespPage(page);
        fillSalesCompanyName(result.getRecords());
        return result;
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

    @Override
    public void refreshPrice(ExportVehicleCbuPoRefreshPriceReq req) {
        purchaseOrderApprovalCmdExe.refreshPrice(req);
    }

    @Override
    public ExportVehicleCbuPoBatchResp batchSubmit(ExportVehicleCbuPoBatchReq req) {
        return purchaseOrderApprovalCmdExe.batchSubmit(req);
    }

    @Override
    public ExportVehicleCbuPoBatchResp batchReject(ExportVehicleCbuPoBatchReq req) {
        return purchaseOrderApprovalCmdExe.batchReject(req);
    }

    @Override
    public ExportVehicleCbuPoBatchResp batchSupplyApprove(ExportVehicleCbuPoBatchReq req) {
        return purchaseOrderApprovalCmdExe.batchSupplyApprove(req);
    }

    @Override
    public ExportVehicleCbuPoBatchResp batchSupplyReject(ExportVehicleCbuPoBatchReq req) {
        return purchaseOrderApprovalCmdExe.batchSupplyReject(req);
    }

    @Override
    public ExportVehicleCbuPoBatchResp batchBalanceApprove(ExportVehicleCbuPoBatchReq req) {
        return purchaseOrderApprovalCmdExe.batchBalanceApprove(req);
    }

    @Override
    public ExportVehicleCbuPoBatchResp batchBalanceReject(ExportVehicleCbuPoBatchReq req) {
        return purchaseOrderApprovalCmdExe.batchBalanceReject(req);
    }

    private void fillSalesCompanyName(List<ExportVehicleCbuPoResp> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        Set<String> codes = records.stream()
                .map(ExportVehicleCbuPoResp::getSalesCompanyCode)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        Map<String, String> nameMap = codes.stream()
                .map(code -> {
                    CompanyRequest request = new CompanyRequest();
                    request.setCompanyCode(code);
                    AjaxResult<List<CompanyResponse>> ajaxResult = companyApi.list(request);
                    if (ajaxResult == null || !Objects.equals(ResultEnum.SUCCESS.getCode(), ajaxResult.getCode())) {
                        throw new BizException("销售公司信息查询失败");
                    }
                    return ajaxResult.getData();
                })
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(List::stream)
                .filter(c -> StringUtils.isNotBlank(c.getCompanyCode()) && StringUtils.isNotBlank(c.getCompanyName()))
                .collect(Collectors.toMap(CompanyResponse::getCompanyCode, CompanyResponse::getCompanyName,
                        (v1, v2) -> v1));
        for (ExportVehicleCbuPoResp record : records) {
            if (StringUtils.isNotBlank(record.getSalesCompanyCode())) {
                record.setSalesCompanyName(nameMap.get(record.getSalesCompanyCode()));
            }
        }
    }
}
