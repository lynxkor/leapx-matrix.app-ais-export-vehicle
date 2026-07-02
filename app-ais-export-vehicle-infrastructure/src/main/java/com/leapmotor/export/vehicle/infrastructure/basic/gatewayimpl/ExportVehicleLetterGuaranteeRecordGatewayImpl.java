package com.leapmotor.export.vehicle.infrastructure.basic.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.basic.convertor.ExportVehicleLetterGuaranteeRecordStructMapper;
import com.leapmotor.export.vehicle.infrastructure.basic.dao.ExportVehicleLetterGuaranteeRecordDao;
import com.leapmotor.export.vehicle.infrastructure.basic.mapper.ExportVehicleLetterGuaranteeRecordMapper;
import com.leapmotor.export.vehicle.infrastructure.basic.entity.ExportVehicleLetterGuaranteeRecord;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

/**
 * 保函交易明细(追加写)领域网关实现
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeRecordGatewayImpl implements ExportVehicleLetterGuaranteeRecordGateway {

    private ExportVehicleLetterGuaranteeRecordMapper exportVehicleLetterGuaranteeRecordMapper;
    private ExportVehicleLetterGuaranteeRecordDao exportVehicleLetterGuaranteeRecordDao;

    @Override
    public String addExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo) {
        ExportVehicleLetterGuaranteeRecord exportVehicleLetterGuaranteeRecord = ExportVehicleLetterGuaranteeRecordStructMapper.INSTANCE.domainToDataObject(exportVehicleLetterGuaranteeRecordDo);
        int result = exportVehicleLetterGuaranteeRecordMapper.insert(exportVehicleLetterGuaranteeRecord);

        exportVehicleLetterGuaranteeRecordDo.setId(exportVehicleLetterGuaranteeRecord.getId());

         if (result > 0) {
             return exportVehicleLetterGuaranteeRecord.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportVehicleLetterGuaranteeRecord(List<ExportVehicleLetterGuaranteeRecordDo> exportVehicleLetterGuaranteeRecordDos) {
        List<ExportVehicleLetterGuaranteeRecord> exportVehicleLetterGuaranteeRecords = ExportVehicleLetterGuaranteeRecordStructMapper.INSTANCE.domainToDataObjects(exportVehicleLetterGuaranteeRecordDos);

        return exportVehicleLetterGuaranteeRecordDao.batchSaveSomeColumn(exportVehicleLetterGuaranteeRecords);
    }

    @Override
    public String updateExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo) {
        ExportVehicleLetterGuaranteeRecord exportVehicleLetterGuaranteeRecord = ExportVehicleLetterGuaranteeRecordStructMapper.INSTANCE.domainToDataObject(exportVehicleLetterGuaranteeRecordDo);
        int result = exportVehicleLetterGuaranteeRecordMapper.updateById(exportVehicleLetterGuaranteeRecord);
        if (result > 0) {
            return exportVehicleLetterGuaranteeRecord.getId();
        }else {
            return null;
        }
    }

    @Override
    public ExportVehicleLetterGuaranteeRecordDo getExportVehicleLetterGuaranteeRecordById(String id) {
        ExportVehicleLetterGuaranteeRecord exportVehicleLetterGuaranteeRecord = exportVehicleLetterGuaranteeRecordDao.getById(id);
        return ExportVehicleLetterGuaranteeRecordStructMapper.INSTANCE.dataObjectToDomain(exportVehicleLetterGuaranteeRecord);
    }

    @Override
    public ExportVehicleLetterGuaranteeRecordDo getOneExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo) {
        ExportVehicleLetterGuaranteeRecord exportVehicleLetterGuaranteeRecord = exportVehicleLetterGuaranteeRecordDao.getOneForceLimit(exportVehicleLetterGuaranteeRecordQo);
        return ExportVehicleLetterGuaranteeRecordStructMapper.INSTANCE.dataObjectToDomain(exportVehicleLetterGuaranteeRecord);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeRecordDo> listExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo) {
        List<ExportVehicleLetterGuaranteeRecord> exportVehicleLetterGuaranteeRecords = exportVehicleLetterGuaranteeRecordDao.queryList(exportVehicleLetterGuaranteeRecordQo);
        return ExportVehicleLetterGuaranteeRecordStructMapper.INSTANCE.dataObjectsToDomains(exportVehicleLetterGuaranteeRecords);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeRecordDo> pageExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo) {
        Page<ExportVehicleLetterGuaranteeRecord> page = exportVehicleLetterGuaranteeRecordDao.findPage(exportVehicleLetterGuaranteeRecordQo);
        return PageUtils.transferPage(ExportVehicleLetterGuaranteeRecordStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeRecordById(String id) {
        return exportVehicleLetterGuaranteeRecordDao.removeById(id);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeRecordByIds(Collection<String> ids) {
        return exportVehicleLetterGuaranteeRecordDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleLetterGuaranteeRecordByIds(LpBaseParam param) {
        ExportVehicleLetterGuaranteeRecord exportVehicleLetterGuaranteeRecord = new ExportVehicleLetterGuaranteeRecord();
        exportVehicleLetterGuaranteeRecord.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            exportVehicleLetterGuaranteeRecord.setId(id);
            exportVehicleLetterGuaranteeRecordDao.updateById(exportVehicleLetterGuaranteeRecord);
        }
        return true;
    }
}

