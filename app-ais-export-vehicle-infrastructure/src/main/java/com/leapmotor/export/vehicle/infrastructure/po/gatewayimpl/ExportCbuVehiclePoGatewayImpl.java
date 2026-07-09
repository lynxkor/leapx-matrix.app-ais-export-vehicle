package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDo;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoQo;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.ExportCbuVehiclePoStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportCbuVehiclePoDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportCbuVehiclePoMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePo;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

/**
 * PO订单主表领域网关实现
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class ExportCbuVehiclePoGatewayImpl implements ExportCbuVehiclePoGateway {

    private ExportCbuVehiclePoMapper purchaseOrderMapper;
    private ExportCbuVehiclePoDao purchaseOrderDao;

    @Override
    public String addExportCbuVehiclePo(ExportCbuVehiclePoDo purchaseOrderDo) {
        ExportCbuVehiclePo purchaseOrder = ExportCbuVehiclePoStructMapper.INSTANCE.domainToDataObject(purchaseOrderDo);
        int result = purchaseOrderMapper.insert(purchaseOrder);

        purchaseOrderDo.setId(purchaseOrder.getId());

         if (result > 0) {
             return purchaseOrder.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportCbuVehiclePo(List<ExportCbuVehiclePoDo> purchaseOrderDos) {
        List<ExportCbuVehiclePo> purchaseOrders = ExportCbuVehiclePoStructMapper.INSTANCE.domainToDataObjects(purchaseOrderDos);

        return purchaseOrderDao.batchSaveSomeColumn(purchaseOrders);
    }

    @Override
    public String updateExportCbuVehiclePo(ExportCbuVehiclePoDo purchaseOrderDo) {
        ExportCbuVehiclePo purchaseOrder = ExportCbuVehiclePoStructMapper.INSTANCE.domainToDataObject(purchaseOrderDo);
        int result = purchaseOrderMapper.updateById(purchaseOrder);
        if (result > 0) {
            return purchaseOrder.getId();
        }else {
            return null;
        }
    }

    @Override
    public ExportCbuVehiclePoDo getExportCbuVehiclePoById(String id) {
        ExportCbuVehiclePo purchaseOrder = purchaseOrderDao.getById(id);
        return ExportCbuVehiclePoStructMapper.INSTANCE.dataObjectToDomain(purchaseOrder);
    }

    @Override
    public ExportCbuVehiclePoDo getOneExportCbuVehiclePo(ExportCbuVehiclePoQo purchaseOrderQo) {
        ExportCbuVehiclePo purchaseOrder = purchaseOrderDao.getOneForceLimit(purchaseOrderQo);
        return ExportCbuVehiclePoStructMapper.INSTANCE.dataObjectToDomain(purchaseOrder);
    }

    @Override
    public List<ExportCbuVehiclePoDo> listExportCbuVehiclePo(ExportCbuVehiclePoQo purchaseOrderQo) {
        List<ExportCbuVehiclePo> purchaseOrders = purchaseOrderDao.queryList(purchaseOrderQo);
        return ExportCbuVehiclePoStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrders);
    }

    @Override
    public PageResult<ExportCbuVehiclePoDo> pageExportCbuVehiclePo(ExportCbuVehiclePoQo purchaseOrderQo) {
        Page<ExportCbuVehiclePo> page = purchaseOrderDao.findPage(purchaseOrderQo);
        return PageUtils.transferPage(ExportCbuVehiclePoStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportCbuVehiclePoById(String id) {
        return purchaseOrderDao.removeById(id);
    }

    @Override
    public boolean deleteExportCbuVehiclePoByIds(Collection<String> ids) {
        return purchaseOrderDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportCbuVehiclePoByIds(LpBaseParam param) {
        ExportCbuVehiclePo purchaseOrder = new ExportCbuVehiclePo();
        purchaseOrder.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrder.setId(id);
            purchaseOrderDao.updateById(purchaseOrder);
        }
        return true;
    }
}

