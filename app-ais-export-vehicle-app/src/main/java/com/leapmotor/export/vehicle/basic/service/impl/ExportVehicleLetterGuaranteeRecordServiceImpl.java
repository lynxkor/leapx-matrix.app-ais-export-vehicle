package com.leapmotor.export.vehicle.basic.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.executor.cmd.ExportVehicleLetterGuaranteeRecordCmdExe;
import com.leapmotor.export.vehicle.basic.executor.query.ExportVehicleLetterGuaranteeRecordQryExe;
import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeRecordDTOStructMapper;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.basic.service.ExportVehicleLetterGuaranteeRecordService;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;

    @Override
    public ExportVehicleLetterGuaranteeRecordResp getExportVehicleLetterGuaranteeRecordById(String id) {
        ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo = exportVehicleLetterGuaranteeRecordGateway.getExportVehicleLetterGuaranteeRecordById(id);
        fillMainTableFields(exportVehicleLetterGuaranteeRecordDo);
        return ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainToResp(exportVehicleLetterGuaranteeRecordDo);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeRecordResp> listExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
    ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.paramToQuery(exportVehicleLetterGuaranteeRecordParam);
        List<ExportVehicleLetterGuaranteeRecordDo> exportVehicleLetterGuaranteeRecordDoList =  exportVehicleLetterGuaranteeRecordGateway.listExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordQo);
        fillMainTableFields(exportVehicleLetterGuaranteeRecordDoList);
        return ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.domainsToResps(exportVehicleLetterGuaranteeRecordDoList);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeRecordResp> pageExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
        PageResult<ExportVehicleLetterGuaranteeRecordDo> page = exportVehicleLetterGuaranteeRecordQryExe.pageExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordParam);
        fillMainTableFields(page.getRecords());
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

    /**
     * 单条: 关联主表查询并填充 Do 上 5 个主表字段
     */
    private void fillMainTableFields(ExportVehicleLetterGuaranteeRecordDo recordDo) {
        if (recordDo == null || recordDo.getLgId() == null) {
            return;
        }
        ExportVehicleLetterGuaranteeDo main = exportVehicleLetterGuaranteeGateway.getExportVehicleLetterGuaranteeById(recordDo.getLgId());
        applyMainFields(recordDo, main);
    }

    /**
     * 批量: 收集记录里所有 lgId,一次 IN 查询主表,按 id 建索引填充
     */
    private void fillMainTableFields(List<ExportVehicleLetterGuaranteeRecordDo> recordDos) {
        if (recordDos == null || recordDos.isEmpty()) {
            return;
        }
        Set<String> lgIds = new HashSet<>();
        for (ExportVehicleLetterGuaranteeRecordDo rec : recordDos) {
            if (rec.getLgId() != null) {
                lgIds.add(rec.getLgId());
            }
        }
        if (lgIds.isEmpty()) {
            return;
        }
        List<ExportVehicleLetterGuaranteeDo> mains = exportVehicleLetterGuaranteeGateway.listExportVehicleLetterGuaranteeByIds(lgIds);
        Map<String, ExportVehicleLetterGuaranteeDo> mainMap = mains.stream()
                .collect(Collectors.toMap(ExportVehicleLetterGuaranteeDo::getId, Function.identity(), (a, b) -> a));
        for (ExportVehicleLetterGuaranteeRecordDo rec : recordDos) {
            applyMainFields(rec, mainMap.get(rec.getLgId()));
        }
    }

    private void applyMainFields(ExportVehicleLetterGuaranteeRecordDo rec, ExportVehicleLetterGuaranteeDo main) {
        if (rec == null || main == null) {
            return;
        }
        rec.setLgStatus(main.getLgStatus());
        rec.setApproveTime(main.getApproveTime());
        rec.setTotalAmount(main.getTotalAmount());
        rec.setConversionRatio(main.getConversionRatio());
        rec.setAvailableAmount(main.getAvailableAmount());
    }
}
