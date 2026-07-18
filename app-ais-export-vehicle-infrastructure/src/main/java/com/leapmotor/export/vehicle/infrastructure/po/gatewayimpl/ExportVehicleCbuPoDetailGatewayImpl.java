package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.ExportVehicleCbuPoDetailStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.ExportVehicleCbuPoDetailDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.ExportVehicleCbuPoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.ExportVehicleCbuPoDetail;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
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
public class ExportVehicleCbuPoDetailGatewayImpl implements ExportVehicleCbuPoDetailGateway {

    private ExportVehicleCbuPoDetailMapper purchaseOrderDetailMapper;
    private ExportVehicleCbuPoDetailDao purchaseOrderDetailDao;

    @Override
    public String addExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailDo purchaseOrderDetailDo) {
        ExportVehicleCbuPoDetail purchaseOrderDetail = ExportVehicleCbuPoDetailStructMapper.INSTANCE.domainToDataObject(purchaseOrderDetailDo);
        int result = purchaseOrderDetailMapper.insert(purchaseOrderDetail);

        purchaseOrderDetailDo.setId(purchaseOrderDetail.getId());

         if (result > 0) {
             return purchaseOrderDetail.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchExportVehicleCbuPoDetail(List<ExportVehicleCbuPoDetailDo> purchaseOrderDetailDos) {
        List<ExportVehicleCbuPoDetail> purchaseOrderDetails = ExportVehicleCbuPoDetailStructMapper.INSTANCE.domainToDataObjects(purchaseOrderDetailDos);

        return purchaseOrderDetailDao.batchSaveSomeColumn(purchaseOrderDetails);
    }

    @Override
    public String updateExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailDo purchaseOrderDetailDo) {
        ExportVehicleCbuPoDetail purchaseOrderDetail = ExportVehicleCbuPoDetailStructMapper.INSTANCE.domainToDataObject(purchaseOrderDetailDo);
        int result = purchaseOrderDetailMapper.updateById(purchaseOrderDetail);
        if (result > 0) {
            return purchaseOrderDetail.getId();
        }else {
            return null;
        }
    }

    @Override
    public int updatePriceById(String id, BigDecimal unitPrice, BigDecimal totalAmount) {
        return purchaseOrderDetailMapper.update(null,
                new LambdaUpdateWrapper<ExportVehicleCbuPoDetail>()
                        .set(ExportVehicleCbuPoDetail::getUnitPrice, unitPrice)
                        .set(ExportVehicleCbuPoDetail::getTotalAmount, totalAmount)
                        .eq(ExportVehicleCbuPoDetail::getId, id));
    }

    @Override
    public ExportVehicleCbuPoDetailDo getExportVehicleCbuPoDetailById(String id) {
        ExportVehicleCbuPoDetail purchaseOrderDetail = purchaseOrderDetailDao.getById(id);
        return ExportVehicleCbuPoDetailStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderDetail);
    }

    @Override
    public ExportVehicleCbuPoDetailDo getOneExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailQo purchaseOrderDetailQo) {
        ExportVehicleCbuPoDetail purchaseOrderDetail = purchaseOrderDetailDao.getOneForceLimit(purchaseOrderDetailQo);
        return ExportVehicleCbuPoDetailStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderDetail);
    }

    @Override
    public List<ExportVehicleCbuPoDetailDo> listExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailQo purchaseOrderDetailQo) {
        List<ExportVehicleCbuPoDetail> purchaseOrderDetails = purchaseOrderDetailDao.queryList(purchaseOrderDetailQo);
        return ExportVehicleCbuPoDetailStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrderDetails);
    }

    @Override
    public PageResult<ExportVehicleCbuPoDetailDo> pageExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailQo purchaseOrderDetailQo) {
        Page<ExportVehicleCbuPoDetail> page = purchaseOrderDetailDao.findPage(purchaseOrderDetailQo);
        return PageUtils.transferPage(ExportVehicleCbuPoDetailStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deleteExportVehicleCbuPoDetailById(String id) {
        return purchaseOrderDetailDao.removeById(id);
    }

    @Override
    public boolean deleteExportVehicleCbuPoDetailByIds(Collection<String> ids) {
        return purchaseOrderDetailDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableExportVehicleCbuPoDetailByIds(LpBaseParam param) {
        ExportVehicleCbuPoDetail purchaseOrderDetail = new ExportVehicleCbuPoDetail();
        purchaseOrderDetail.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrderDetail.setId(id);
            purchaseOrderDetailDao.updateById(purchaseOrderDetail);
        }
        return true;
    }
}

