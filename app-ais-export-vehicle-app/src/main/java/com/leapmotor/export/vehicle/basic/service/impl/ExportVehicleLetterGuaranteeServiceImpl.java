package com.leapmotor.export.vehicle.basic.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.executor.cmd.ExportVehicleLetterGuaranteeCmdExe;
import com.leapmotor.export.vehicle.basic.executor.query.ExportVehicleLetterGuaranteeQryExe;
import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
/**
 * <p>
 * 保函主表 服务实现类
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeServiceImpl implements ExportVehicleLetterGuaranteeService {


    private ExportVehicleLetterGuaranteeCmdExe exportVehicleLetterGuaranteeCmdExe;
    private ExportVehicleLetterGuaranteeQryExe exportVehicleLetterGuaranteeQryExe;
    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;

    @Override
    public ExportVehicleLetterGuaranteeResp getExportVehicleLetterGuaranteeById(String id) {
        ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(id);
        return ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainToResp(exportVehicleLetterGuaranteeDo);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeResp> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam) {
    ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.paramToQuery(exportVehicleLetterGuaranteeParam);
        List<ExportVehicleLetterGuaranteeDo> exportVehicleLetterGuaranteeDoList =  exportVehicleLetterGuaranteeGateway.listExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeQo);
        return ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToResps(exportVehicleLetterGuaranteeDoList);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeResp> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam) {
        PageResult<ExportVehicleLetterGuaranteeDo> page = exportVehicleLetterGuaranteeQryExe.pageExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeParam);
        return ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeAddOrUpdateReq exportVehicleLetterGuaranteeAddOrUpdateReq) {
        ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.addCmdToDomain(exportVehicleLetterGuaranteeAddOrUpdateReq);
        return exportVehicleLetterGuaranteeCmdExe.addOrUpdateExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeDo);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeCmdExe.deleteExportVehicleLetterGuaranteeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeCmdExe.enableOrDisableExportVehicleLetterGuaranteeByIds(param);
    }
}
