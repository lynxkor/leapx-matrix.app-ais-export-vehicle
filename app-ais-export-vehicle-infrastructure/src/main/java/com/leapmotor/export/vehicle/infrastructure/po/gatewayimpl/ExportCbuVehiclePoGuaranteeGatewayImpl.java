package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoGuaranteeQo;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGuaranteeGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.ExportCbuVehiclePoGuaranteeStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportCbuVehiclePoGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportCbuVehiclePoGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePoGuarantee;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

/**
 * PO-保函关联表(一个PO多保函)领域网关实现
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class ExportCbuVehiclePoGuaranteeGatewayImpl implements ExportCbuVehiclePoGuaranteeGateway {

    private ExportCbuVehiclePoGuaranteeMapper purchaseOrderGuaranteeMapper;
    private ExportCbuVehiclePoGuaranteeDao purchaseOrderGuaranteeDao;

    @Override
    public String addExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeDo purchaseOrderGuaranteeDo) {
        ExportCbuVehiclePoGuarantee purchaseOrderGuarantee = ExportCbuVehiclePoGuaranteeStructMapper.INSTANCE.domainToDataObject(purchaseOrderGuaranteeDo);
        int result = purchaseOrderGuaranteeMapper.insert(purchaseOrderGuarantee);

        purchaseOrderGuaranteeDo.setId(purchaseOrderGuarantee.getId());

         if (result > 0) {
             return purchaseOrderGuarantee.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportCbuVehiclePoGuarantee(List<ExportCbuVehiclePoGuaranteeDo> purchaseOrderGuaranteeDos) {
        List<ExportCbuVehiclePoGuarantee> purchaseOrderGuarantees = ExportCbuVehiclePoGuaranteeStructMapper.INSTANCE.domainToDataObjects(purchaseOrderGuaranteeDos);

        return purchaseOrderGuaranteeDao.batchSaveSomeColumn(purchaseOrderGuarantees);
    }

    @Override
    public String updateExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeDo purchaseOrderGuaranteeDo) {
        ExportCbuVehiclePoGuarantee purchaseOrderGuarantee = ExportCbuVehiclePoGuaranteeStructMapper.INSTANCE.domainToDataObject(purchaseOrderGuaranteeDo);
        int result = purchaseOrderGuaranteeMapper.updateById(purchaseOrderGuarantee);
        if (result > 0) {
            return purchaseOrderGuarantee.getId();
        }else {
            return null;
        }
    }

    @Override
    public ExportCbuVehiclePoGuaranteeDo getExportCbuVehiclePoGuaranteeById(String id) {
        ExportCbuVehiclePoGuarantee purchaseOrderGuarantee = purchaseOrderGuaranteeDao.getById(id);
        return ExportCbuVehiclePoGuaranteeStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderGuarantee);
    }

    @Override
    public ExportCbuVehiclePoGuaranteeDo getOneExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo) {
        ExportCbuVehiclePoGuarantee purchaseOrderGuarantee = purchaseOrderGuaranteeDao.getOneForceLimit(purchaseOrderGuaranteeQo);
        return ExportCbuVehiclePoGuaranteeStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderGuarantee);
    }

    @Override
    public List<ExportCbuVehiclePoGuaranteeDo> listExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo) {
        List<ExportCbuVehiclePoGuarantee> purchaseOrderGuarantees = purchaseOrderGuaranteeDao.queryList(purchaseOrderGuaranteeQo);
        return ExportCbuVehiclePoGuaranteeStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrderGuarantees);
    }

    @Override
    public PageResult<ExportCbuVehiclePoGuaranteeDo> pageExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo) {
        Page<ExportCbuVehiclePoGuarantee> page = purchaseOrderGuaranteeDao.findPage(purchaseOrderGuaranteeQo);
        return PageUtils.transferPage(ExportCbuVehiclePoGuaranteeStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportCbuVehiclePoGuaranteeById(String id) {
        return purchaseOrderGuaranteeDao.removeById(id);
    }

    @Override
    public boolean deleteExportCbuVehiclePoGuaranteeByIds(Collection<String> ids) {
        return purchaseOrderGuaranteeDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportCbuVehiclePoGuaranteeByIds(LpBaseParam param) {
        ExportCbuVehiclePoGuarantee purchaseOrderGuarantee = new ExportCbuVehiclePoGuarantee();
        purchaseOrderGuarantee.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrderGuarantee.setId(id);
            purchaseOrderGuaranteeDao.updateById(purchaseOrderGuarantee);
        }
        return true;
    }
}

