package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportCbuVehiclePoGuaranteeCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.ExportCbuVehiclePoGuaranteeQryExe;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeParam;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoGuaranteeQo;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoGuaranteeDo;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.ExportCbuVehiclePoGuaranteeService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGuaranteeGateway;
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
public class ExportCbuVehiclePoGuaranteeServiceImpl implements ExportCbuVehiclePoGuaranteeService {


    private ExportCbuVehiclePoGuaranteeCmdExe purchaseOrderGuaranteeCmdExe;
    private ExportCbuVehiclePoGuaranteeQryExe purchaseOrderGuaranteeQryExe;
    private ExportCbuVehiclePoGuaranteeGateway purchaseOrderGuaranteeGateway;

    @Override
    public ExportCbuVehiclePoGuaranteeResp getExportCbuVehiclePoGuaranteeById(String id) {
        ExportCbuVehiclePoGuaranteeDo purchaseOrderGuaranteeDo = purchaseOrderGuaranteeGateway.getExportCbuVehiclePoGuaranteeById(id);
        return ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.domainToResp(purchaseOrderGuaranteeDo);
    }

    @Override
    public List<ExportCbuVehiclePoGuaranteeResp> listExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeParam purchaseOrderGuaranteeParam) {
    ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderGuaranteeParam);
        List<ExportCbuVehiclePoGuaranteeDo> purchaseOrderGuaranteeDoList =  purchaseOrderGuaranteeGateway.listExportCbuVehiclePoGuarantee(purchaseOrderGuaranteeQo);
        return ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderGuaranteeDoList);
    }

    @Override
    public PageResult<ExportCbuVehiclePoGuaranteeResp> pageExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeParam purchaseOrderGuaranteeParam) {
        PageResult<ExportCbuVehiclePoGuaranteeDo> page = purchaseOrderGuaranteeQryExe.pageExportCbuVehiclePoGuarantee(purchaseOrderGuaranteeParam);
        return ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq) {
        ExportCbuVehiclePoGuaranteeDo purchaseOrderGuaranteeDo = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderGuaranteeAddOrUpdateReq);
        return purchaseOrderGuaranteeCmdExe.addOrUpdateExportCbuVehiclePoGuarantee(purchaseOrderGuaranteeDo);
    }

    @Override
    public boolean deleteExportCbuVehiclePoGuaranteeByIds(List<String> ids) {
        return purchaseOrderGuaranteeCmdExe.deleteExportCbuVehiclePoGuaranteeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportCbuVehiclePoGuaranteeByIds(LpBaseParam param) {
        return purchaseOrderGuaranteeCmdExe.enableOrDisableExportCbuVehiclePoGuaranteeByIds(param);
    }
}
