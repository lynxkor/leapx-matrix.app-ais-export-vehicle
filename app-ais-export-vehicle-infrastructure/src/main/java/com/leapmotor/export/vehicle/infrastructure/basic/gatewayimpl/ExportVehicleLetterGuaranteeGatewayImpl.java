package com.leapmotor.export.vehicle.infrastructure.basic.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.basic.convertor.ExportVehicleLetterGuaranteeStructMapper;
import com.leapmotor.export.vehicle.infrastructure.basic.dao.ExportVehicleLetterGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.basic.mapper.ExportVehicleLetterGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.basic.entity.ExportVehicleLetterGuarantee;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

/**
 * 保函主表领域网关实现
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Service
public class ExportVehicleLetterGuaranteeGatewayImpl implements ExportVehicleLetterGuaranteeGateway {

    private ExportVehicleLetterGuaranteeMapper exportVehicleLetterGuaranteeMapper;
    private ExportVehicleLetterGuaranteeDao exportVehicleLetterGuaranteeDao;

    @Override
    public String addExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = ExportVehicleLetterGuaranteeStructMapper.INSTANCE.domainToDataObject(exportVehicleLetterGuaranteeDo);
        int result = exportVehicleLetterGuaranteeMapper.insert(exportVehicleLetterGuarantee);

        exportVehicleLetterGuaranteeDo.setId(exportVehicleLetterGuarantee.getId());

         if (result > 0) {
             return exportVehicleLetterGuarantee.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportVehicleLetterGuarantee(List<ExportVehicleLetterGuaranteeDo> exportVehicleLetterGuaranteeDos) {
        List<ExportVehicleLetterGuarantee> exportVehicleLetterGuarantees = ExportVehicleLetterGuaranteeStructMapper.INSTANCE.domainToDataObjects(exportVehicleLetterGuaranteeDos);

        return exportVehicleLetterGuaranteeDao.batchSaveSomeColumn(exportVehicleLetterGuarantees);
    }

    @Override
    public String updateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = ExportVehicleLetterGuaranteeStructMapper.INSTANCE.domainToDataObject(exportVehicleLetterGuaranteeDo);
        int result = exportVehicleLetterGuaranteeMapper.updateById(exportVehicleLetterGuarantee);
        if (result > 0) {
            return exportVehicleLetterGuarantee.getId();
        }else {
            return null;
        }
    }

    @Override
    public ExportVehicleLetterGuaranteeDo getExportVehicleLetterGuaranteeById(String id) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = exportVehicleLetterGuaranteeDao.getById(id);
        return ExportVehicleLetterGuaranteeStructMapper.INSTANCE.dataObjectToDomain(exportVehicleLetterGuarantee);
    }

    @Override
    public ExportVehicleLetterGuaranteeDo getOneExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = exportVehicleLetterGuaranteeDao.getOneForceLimit(exportVehicleLetterGuaranteeQo);
        return ExportVehicleLetterGuaranteeStructMapper.INSTANCE.dataObjectToDomain(exportVehicleLetterGuarantee);
    }

    @Override
    public List<ExportVehicleLetterGuaranteeDo> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo) {
        List<ExportVehicleLetterGuarantee> exportVehicleLetterGuarantees = exportVehicleLetterGuaranteeDao.queryList(exportVehicleLetterGuaranteeQo);
        return ExportVehicleLetterGuaranteeStructMapper.INSTANCE.dataObjectsToDomains(exportVehicleLetterGuarantees);
    }

    @Override
    public PageResult<ExportVehicleLetterGuaranteeDo> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo) {
        Page<ExportVehicleLetterGuarantee> page = exportVehicleLetterGuaranteeDao.findPage(exportVehicleLetterGuaranteeQo);
        return PageUtils.transferPage(ExportVehicleLetterGuaranteeStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeById(String id) {
        return exportVehicleLetterGuaranteeDao.removeById(id);
    }

    @Override
    public boolean deleteExportVehicleLetterGuaranteeByIds(Collection<String> ids) {
        return exportVehicleLetterGuaranteeDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param) {
        ExportVehicleLetterGuarantee exportVehicleLetterGuarantee = new ExportVehicleLetterGuarantee();
        exportVehicleLetterGuarantee.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            exportVehicleLetterGuarantee.setId(id);
            exportVehicleLetterGuaranteeDao.updateById(exportVehicleLetterGuarantee);
        }
        return true;
    }
}

