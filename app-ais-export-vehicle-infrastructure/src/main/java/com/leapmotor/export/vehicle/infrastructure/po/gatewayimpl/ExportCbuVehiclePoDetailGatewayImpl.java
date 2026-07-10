package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoDetailQo;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoDetailGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.ExportCbuVehiclePoDetailStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportCbuVehiclePoDetailDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportCbuVehiclePoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportCbuVehiclePoDetail;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;

/**
 * PO订单明细表领域网关实现
 *
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Service
public class ExportCbuVehiclePoDetailGatewayImpl implements ExportCbuVehiclePoDetailGateway {

    private ExportCbuVehiclePoDetailMapper purchaseOrderDetailMapper;
    private ExportCbuVehiclePoDetailDao purchaseOrderDetailDao;

    @Override
    public String addExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailDo purchaseOrderDetailDo) {
        ExportCbuVehiclePoDetail purchaseOrderDetail = ExportCbuVehiclePoDetailStructMapper.INSTANCE.domainToDataObject(purchaseOrderDetailDo);
        int result = purchaseOrderDetailMapper.insert(purchaseOrderDetail);

        purchaseOrderDetailDo.setId(purchaseOrderDetail.getId());

         if (result > 0) {
             return purchaseOrderDetail.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportCbuVehiclePoDetail(List<ExportCbuVehiclePoDetailDo> purchaseOrderDetailDos) {
        List<ExportCbuVehiclePoDetail> purchaseOrderDetails = ExportCbuVehiclePoDetailStructMapper.INSTANCE.domainToDataObjects(purchaseOrderDetailDos);

        return purchaseOrderDetailDao.batchSaveSomeColumn(purchaseOrderDetails);
    }

    @Override
    public String updateExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailDo purchaseOrderDetailDo) {
        ExportCbuVehiclePoDetail purchaseOrderDetail = ExportCbuVehiclePoDetailStructMapper.INSTANCE.domainToDataObject(purchaseOrderDetailDo);
        int result = purchaseOrderDetailMapper.updateById(purchaseOrderDetail);
        if (result > 0) {
            return purchaseOrderDetail.getId();
        }else {
            return null;
        }
    }

    @Override
    public ExportCbuVehiclePoDetailDo getExportCbuVehiclePoDetailById(String id) {
        ExportCbuVehiclePoDetail purchaseOrderDetail = purchaseOrderDetailDao.getById(id);
        return ExportCbuVehiclePoDetailStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderDetail);
    }

    @Override
    public ExportCbuVehiclePoDetailDo getOneExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailQo purchaseOrderDetailQo) {
        ExportCbuVehiclePoDetail purchaseOrderDetail = purchaseOrderDetailDao.getOneForceLimit(purchaseOrderDetailQo);
        return ExportCbuVehiclePoDetailStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderDetail);
    }

    @Override
    public List<ExportCbuVehiclePoDetailDo> listExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailQo purchaseOrderDetailQo) {
        List<ExportCbuVehiclePoDetail> purchaseOrderDetails = purchaseOrderDetailDao.queryList(purchaseOrderDetailQo);
        return ExportCbuVehiclePoDetailStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrderDetails);
    }

    @Override
    public PageResult<ExportCbuVehiclePoDetailDo> pageExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailQo purchaseOrderDetailQo) {
        Page<ExportCbuVehiclePoDetail> page = purchaseOrderDetailDao.findPage(purchaseOrderDetailQo);
        return PageUtils.transferPage(ExportCbuVehiclePoDetailStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportCbuVehiclePoDetailById(String id) {
        return purchaseOrderDetailDao.removeById(id);
    }

    @Override
    public boolean deleteExportCbuVehiclePoDetailByIds(Collection<String> ids) {
        return purchaseOrderDetailDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportCbuVehiclePoDetailByIds(LpBaseParam param) {
        ExportCbuVehiclePoDetail purchaseOrderDetail = new ExportCbuVehiclePoDetail();
        purchaseOrderDetail.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrderDetail.setId(id);
            purchaseOrderDetailDao.updateById(purchaseOrderDetail);
        }
        return true;
    }
}

