package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.ExportVehicleCbuPoStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportVehicleCbuPoDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportVehicleCbuPoMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
public class ExportVehicleCbuPoGatewayImpl implements ExportVehicleCbuPoGateway {

    private ExportVehicleCbuPoMapper purchaseOrderMapper;
    private ExportVehicleCbuPoDao purchaseOrderDao;

    @Override
    public String addExportVehicleCbuPo(ExportVehicleCbuPoDo purchaseOrderDo) {
        ExportVehicleCbuPo purchaseOrder = ExportVehicleCbuPoStructMapper.INSTANCE.domainToDataObject(purchaseOrderDo);
        int result = purchaseOrderMapper.insert(purchaseOrder);

        purchaseOrderDo.setId(purchaseOrder.getId());

         if (result > 0) {
             return purchaseOrder.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportVehicleCbuPo(List<ExportVehicleCbuPoDo> purchaseOrderDos) {
        List<ExportVehicleCbuPo> purchaseOrders = ExportVehicleCbuPoStructMapper.INSTANCE.domainToDataObjects(purchaseOrderDos);

        return purchaseOrderDao.batchSaveSomeColumn(purchaseOrders);
    }

    @Override
    public String updateExportVehicleCbuPo(ExportVehicleCbuPoDo purchaseOrderDo) {
        ExportVehicleCbuPo purchaseOrder = ExportVehicleCbuPoStructMapper.INSTANCE.domainToDataObject(purchaseOrderDo);
        int result = purchaseOrderMapper.updateById(purchaseOrder);
        if (result > 0) {
            return purchaseOrder.getId();
        }else {
            return null;
        }
    }

    @Override
    public ExportVehicleCbuPoDo getExportVehicleCbuPoById(String id) {
        ExportVehicleCbuPo purchaseOrder = purchaseOrderDao.getById(id);
        return ExportVehicleCbuPoStructMapper.INSTANCE.dataObjectToDomain(purchaseOrder);
    }

    @Override
    public ExportVehicleCbuPoDo getOneExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo) {
        ExportVehicleCbuPo purchaseOrder = purchaseOrderDao.getOneForceLimit(purchaseOrderQo);
        return ExportVehicleCbuPoStructMapper.INSTANCE.dataObjectToDomain(purchaseOrder);
    }

    @Override
    public List<ExportVehicleCbuPoDo> listExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo) {
        List<ExportVehicleCbuPo> purchaseOrders = purchaseOrderDao.queryList(purchaseOrderQo);
        return ExportVehicleCbuPoStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrders);
    }

    @Override
    public PageResult<ExportVehicleCbuPoDo> pageExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo) {
        Page<ExportVehicleCbuPo> page = purchaseOrderDao.findPage(purchaseOrderQo);
        return PageUtils.transferPage(ExportVehicleCbuPoStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportVehicleCbuPoById(String id) {
        return purchaseOrderDao.removeById(id);
    }

    @Override
    public boolean deleteExportVehicleCbuPoByIds(Collection<String> ids) {
        return purchaseOrderDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleCbuPoByIds(LpBaseParam param) {
        ExportVehicleCbuPo purchaseOrder = new ExportVehicleCbuPo();
        purchaseOrder.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrder.setId(id);
            purchaseOrderDao.updateById(purchaseOrder);
        }
        return true;
    }

    @Override
    public int updateApprovalSyncResult(String poId, LocalDateTime syncTime, String syncResult, String returnInfo) {
        return purchaseOrderMapper.update(null,
                new LambdaUpdateWrapper<ExportVehicleCbuPo>()
                        .eq(ExportVehicleCbuPo::getId, poId)
                        .set(ExportVehicleCbuPo::getAuditResultSyncTime, syncTime)
                        .set(ExportVehicleCbuPo::getAuditResultSyncResult, syncResult)
                        .set(ExportVehicleCbuPo::getCustomerSystemReturnInfo, returnInfo));
    }
}
