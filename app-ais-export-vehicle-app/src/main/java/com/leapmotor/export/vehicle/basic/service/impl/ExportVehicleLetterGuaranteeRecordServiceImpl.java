package com.leapmotor.export.vehicle.basic.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.executor.cmd.ExportVehicleLetterGuaranteeRecordCmdExe;
import com.leapmotor.export.vehicle.basic.executor.query.ExportVehicleLetterGuaranteeRecordQryExe;
import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeRecordDTOStructMapper;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeRecordService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
/**
 * <p>
 * 保函交易明细(追加写) 服务实现类
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeRecordServiceImpl implements ExportVehicleLetterGuaranteeRecordService {


    private ExportVehicleLetterGuaranteeRecordCmdExe exportVehicleLetterGuaranteeRecordCmdExe;
    private ExportVehicleLetterGuaranteeRecordQryExe exportVehicleLetterGuaranteeRecordQryExe;
    private ExportVehicleLetterGuaranteeRecordGateway exportVehicleLetterGuaranteeRecordGateway;

    @Override
    public ExportVehicleLetterGuaranteeRecordResp getExportVehicleLetterGuaranteeRecordById(String id) {
        ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo = exportVehicleLetterGuaranteeRecordGateway.getExportVehicleLetterGuaranteeRecordById(id);
        return ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainToResp(exportVehicleLetterGuaranteeRecordDo);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeRecordResp> listExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
    ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.paramToQuery(exportVehicleLetterGuaranteeRecordParam);
        List<ExportVehicleLetterGuaranteeRecordDo> exportVehicleLetterGuaranteeRecordDoList =  exportVehicleLetterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordQo);
        return ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToResps(exportVehicleLetterGuaranteeRecordDoList);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeRecordResp> pageExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
        PageResult<ExportVehicleLetterGuaranteeRecordDo> page = exportVehicleLetterGuaranteeRecordQryExe.pageExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordParam);
        return ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordAddOrUpdateReq exportVehicleLetterGuaranteeRecordAddOrUpdateReq) {
        ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.addCmdToDomain(exportVehicleLetterGuaranteeRecordAddOrUpdateReq);
        return exportVehicleLetterGuaranteeRecordCmdExe.addOrUpdateExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordDo);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeRecordByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeRecordCmdExe.deleteExportVehicleLetterGuaranteeRecordByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleLetterGuaranteeRecordByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeRecordCmdExe.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param);
    }
}
