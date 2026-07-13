package com.leapmotor.export.vehicle.po.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.executor.cmd.ExportVehicleCbuPoGuaranteeCmdExe;
import com.leapmotor.export.vehicle.po.executor.query.ExportVehicleCbuPoGuaranteeQryExe;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.po.service.ExportVehicleCbuPoGuaranteeService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
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
public class ExportVehicleCbuPoGuaranteeServiceImpl implements ExportVehicleCbuPoGuaranteeService {


    private ExportVehicleCbuPoGuaranteeCmdExe purchaseOrderGuaranteeCmdExe;
    private ExportVehicleCbuPoGuaranteeQryExe purchaseOrderGuaranteeQryExe;
    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;

    @Override
    public ExportVehicleCbuPoGuaranteeResp getExportVehicleCbuPoGuaranteeById(String id) {
        ExportVehicleCbuPoGuaranteeDo purchaseOrderGuaranteeDo = purchaseOrderGuaranteeGateway.getExportVehicleCbuPoGuaranteeById(id);
        return ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainToResp(purchaseOrderGuaranteeDo);
    }

    @Override
    public List<ExportVehicleCbuPoGuaranteeResp> listExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeParam purchaseOrderGuaranteeParam) {
    ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderGuaranteeParam);
        List<ExportVehicleCbuPoGuaranteeDo> purchaseOrderGuaranteeDoList =  purchaseOrderGuaranteeGateway.listExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeQo);
        return ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainsToResps(purchaseOrderGuaranteeDoList);
    }

    @Override
    public PageResult<ExportVehicleCbuPoGuaranteeResp> pageExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeParam purchaseOrderGuaranteeParam) {
        PageResult<ExportVehicleCbuPoGuaranteeDo> page = purchaseOrderGuaranteeQryExe.pageExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeParam);
        return ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq) {
        ExportVehicleCbuPoGuaranteeDo purchaseOrderGuaranteeDo = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.addCmdToDomain(purchaseOrderGuaranteeAddOrUpdateReq);
        return purchaseOrderGuaranteeCmdExe.addOrUpdateExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeDo);
    }

    @Override
    public boolean deleteExportVehicleCbuPoGuaranteeByIds(List<String> ids) {
        return purchaseOrderGuaranteeCmdExe.deleteExportVehicleCbuPoGuaranteeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleCbuPoGuaranteeByIds(LpBaseParam param) {
        return purchaseOrderGuaranteeCmdExe.enableOrDisableExportVehicleCbuPoGuaranteeByIds(param);
    }
}
