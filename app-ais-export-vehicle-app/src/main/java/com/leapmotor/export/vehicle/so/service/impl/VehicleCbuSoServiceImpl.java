package com.leapmotor.export.vehicle.so.service.impl;

import com.leapmotor.export.nexus.company.api.CompanyApi;
import com.leapmotor.export.nexus.company.request.CompanyRequest;
import com.leapmotor.export.nexus.company.response.CompanyResponse;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDTOStructMapper;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.so.executor.cmd.VehicleCbuSoCmdExe;
import com.leapmotor.export.vehicle.so.executor.query.VehicleCbuSoQryExe;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.export.vehicle.so.req.SoCreateReq;
import com.leapmotor.export.vehicle.so.req.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoAddOrUpdateReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoIssueReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoCancelReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveStatusReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReceiveQcReq;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoReissueSapReq;
import com.leapmotor.export.vehicle.so.resp.SoResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoOperateResult;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailAggregateResp;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoDetailResp;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoResp;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoService;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.response.AjaxResult;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * CBU整车销售订单主表 服务实现类
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoServiceImpl implements VehicleCbuSoService {

    private VehicleCbuSoCmdExe vehicleCbuSoCmdExe;
    private VehicleCbuSoQryExe vehicleCbuSoQryExe;
    private VehicleCbuSoGateway vehicleCbuSoGateway;
    private CompanyApi companyApi;

    @Override
    public VehicleCbuSoResp getVehicleCbuSoById(String id) {
        VehicleCbuSoDo vehicleCbuSoDo = vehicleCbuSoGateway.getVehicleCbuSoById(id);
        return VehicleCbuSoDTOStructMapper.INSTANCE.domainToResp(vehicleCbuSoDo);
    }

    @Override
    public List<VehicleCbuSoResp> listVehicleCbuSo(VehicleCbuSoParam vehicleCbuSoParam) {
        VehicleCbuSoQo vehicleCbuSoQo = VehicleCbuSoDTOStructMapper.INSTANCE.paramToQuery(vehicleCbuSoParam);
        List<VehicleCbuSoDo> vehicleCbuSoDoList = vehicleCbuSoQryExe.listSoList(vehicleCbuSoQo);
        List<VehicleCbuSoResp> resps = VehicleCbuSoDTOStructMapper.INSTANCE.domainsToResps(vehicleCbuSoDoList);
        fillSalesCompanyName(resps);
        return resps;
    }

    @Override
    public PageResult<VehicleCbuSoResp> pageVehicleCbuSo(VehicleCbuSoParam vehicleCbuSoParam) {
        PageResult<VehicleCbuSoDo> page = vehicleCbuSoQryExe.pageVehicleCbuSo(vehicleCbuSoParam);
        PageResult<VehicleCbuSoResp> result = VehicleCbuSoDTOStructMapper.INSTANCE.domainsToRespPage(page);
        fillSalesCompanyName(result.getRecords());
        return result;
    }

    @Override
    public String addOrUpdateVehicleCbuSo(VehicleCbuSoAddOrUpdateReq vehicleCbuSoAddOrUpdateReq) {
        VehicleCbuSoDo vehicleCbuSoDo = VehicleCbuSoDTOStructMapper.INSTANCE.addCmdToDomain(vehicleCbuSoAddOrUpdateReq);
        return vehicleCbuSoCmdExe.addOrUpdateVehicleCbuSo(vehicleCbuSoDo);
    }

    @Override
    public boolean deleteVehicleCbuSoByIds(List<String> ids) {
        return vehicleCbuSoCmdExe.deleteVehicleCbuSoByIds(ids);
    }

    @Override
    public boolean enableOrDisableVehicleCbuSoByIds(LpBaseParam param) {
        return vehicleCbuSoCmdExe.enableOrDisableVehicleCbuSoByIds(param);
    }

    @Override
    public SoResult createSo(SoCreateReq req) {
        return vehicleCbuSoCmdExe.createSo(req);
    }

    @Override
    public void updatePrice(SoUpdatePriceReq req) {
        vehicleCbuSoCmdExe.updatePrice(req);
    }

    @Override
    public List<VehicleCbuSoDo> listVehicleCbuSoByPoNo(String poNo) {
        return vehicleCbuSoCmdExe.listVehicleCbuSoByPoNo(poNo);
    }

    @Override
    public VehicleCbuSoOperateResult issueToProduction(VehicleCbuSoIssueReq req) {
        return vehicleCbuSoCmdExe.issueToProduction(req);
    }

    @Override
    public VehicleCbuSoOperateResult cancel(VehicleCbuSoCancelReq req) {
        return vehicleCbuSoCmdExe.cancel(req);
    }

    @Override
    public VehicleCbuSoOperateResult receiveProductionStatus(List<VehicleCbuSoReceiveStatusReq> reqs) {
        return vehicleCbuSoCmdExe.receiveProductionStatusBatch(reqs);
    }

    @Override
    public void receiveQcResult(VehicleCbuSoReceiveQcReq req) {
        vehicleCbuSoCmdExe.receiveQcResult(req);
    }

    @Override
    public void updateReceivedStatus(String soNo) {
        vehicleCbuSoCmdExe.updateReceivedStatus(soNo);
    }

    @Override
    public VehicleCbuSoOperateResult reissueSap(VehicleCbuSoReissueSapReq req) {
        return vehicleCbuSoCmdExe.reissueSap(req);
    }

    @Override
    public VehicleCbuSoDetailAggregateResp detail(String id) {
        VehicleCbuSoDo orderDo = vehicleCbuSoQryExe.getSoDetailById(id);
        if (orderDo == null) {
            return null;
        }
        VehicleCbuSoResp orderResp = VehicleCbuSoDTOStructMapper.INSTANCE.domainToResp(orderDo);
        fillSalesCompanyName(Collections.singletonList(orderResp));
        VehicleCbuSoDetailResp detailResp = VehicleCbuSoDetailDTOStructMapper.INSTANCE.domainToResp(orderDo);

        VehicleCbuSoDetailAggregateResp resp = new VehicleCbuSoDetailAggregateResp();
        resp.setOrderInfo(orderResp);
        resp.setDetailInfo(detailResp);
        return resp;
    }

    private void fillSalesCompanyName(List<VehicleCbuSoResp> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        Set<String> codes = records.stream()
                .map(VehicleCbuSoResp::getSalesCompanyCode)
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
        for (VehicleCbuSoResp record : records) {
            if (StringUtils.isNotBlank(record.getSalesCompanyCode())) {
                record.setSalesCompanyName(nameMap.get(record.getSalesCompanyCode()));
            }
        }
    }
}
