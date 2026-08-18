package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.ExportVehicleCbuPoGuaranteeStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportVehicleCbuPoGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportVehicleCbuPoGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoGuarantee;
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
public class ExportVehicleCbuPoGuaranteeGatewayImpl implements ExportVehicleCbuPoGuaranteeGateway {

    private ExportVehicleCbuPoGuaranteeMapper purchaseOrderGuaranteeMapper;
    private ExportVehicleCbuPoGuaranteeDao purchaseOrderGuaranteeDao;

    @Override
    public String addExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeDo purchaseOrderGuaranteeDo) {
        ExportVehicleCbuPoGuarantee purchaseOrderGuarantee = ExportVehicleCbuPoGuaranteeStructMapper.INSTANCE.domainToDataObject(purchaseOrderGuaranteeDo);
        int result = purchaseOrderGuaranteeMapper.insert(purchaseOrderGuarantee);

        purchaseOrderGuaranteeDo.setId(purchaseOrderGuarantee.getId());

         if (result > 0) {
             return purchaseOrderGuarantee.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportVehicleCbuPoGuarantee(List<ExportVehicleCbuPoGuaranteeDo> purchaseOrderGuaranteeDos) {
        List<ExportVehicleCbuPoGuarantee> purchaseOrderGuarantees = ExportVehicleCbuPoGuaranteeStructMapper.INSTANCE.domainToDataObjects(purchaseOrderGuaranteeDos);

        return purchaseOrderGuaranteeDao.batchSaveSomeColumn(purchaseOrderGuarantees);
    }

    @Override
    public String updateExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeDo purchaseOrderGuaranteeDo) {
        ExportVehicleCbuPoGuarantee purchaseOrderGuarantee = ExportVehicleCbuPoGuaranteeStructMapper.INSTANCE.domainToDataObject(purchaseOrderGuaranteeDo);
        int result = purchaseOrderGuaranteeMapper.updateById(purchaseOrderGuarantee);
        if (result > 0) {
            return purchaseOrderGuarantee.getId();
        }else {
            return null;
        }
    }

    @Override
    public ExportVehicleCbuPoGuaranteeDo getExportVehicleCbuPoGuaranteeById(String id) {
        ExportVehicleCbuPoGuarantee purchaseOrderGuarantee = purchaseOrderGuaranteeDao.getById(id);
        return ExportVehicleCbuPoGuaranteeStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderGuarantee);
    }

    @Override
    public ExportVehicleCbuPoGuaranteeDo getOneExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo) {
        ExportVehicleCbuPoGuarantee purchaseOrderGuarantee = purchaseOrderGuaranteeDao.getOneForceLimit(purchaseOrderGuaranteeQo);
        return ExportVehicleCbuPoGuaranteeStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderGuarantee);
    }

    @Override
    public List<ExportVehicleCbuPoGuaranteeDo> listExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo) {
        List<ExportVehicleCbuPoGuarantee> purchaseOrderGuarantees = purchaseOrderGuaranteeDao.queryList(purchaseOrderGuaranteeQo);
        return ExportVehicleCbuPoGuaranteeStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrderGuarantees);
    }

    @Override
    public PageResult<ExportVehicleCbuPoGuaranteeDo> pageExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo) {
        Page<ExportVehicleCbuPoGuarantee> page = purchaseOrderGuaranteeDao.findPage(purchaseOrderGuaranteeQo);
        return PageUtils.transferPage(ExportVehicleCbuPoGuaranteeStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportVehicleCbuPoGuaranteeById(String id) {
        return purchaseOrderGuaranteeDao.removeById(id);
    }

    @Override
    public boolean deleteExportVehicleCbuPoGuaranteeByIds(Collection<String> ids) {
        return purchaseOrderGuaranteeDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleCbuPoGuaranteeByIds(LpBaseParam param) {
        ExportVehicleCbuPoGuarantee purchaseOrderGuarantee = new ExportVehicleCbuPoGuarantee();
        purchaseOrderGuarantee.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrderGuarantee.setId(id);
            purchaseOrderGuaranteeDao.updateById(purchaseOrderGuarantee);
        }
        return true;
    }
}

