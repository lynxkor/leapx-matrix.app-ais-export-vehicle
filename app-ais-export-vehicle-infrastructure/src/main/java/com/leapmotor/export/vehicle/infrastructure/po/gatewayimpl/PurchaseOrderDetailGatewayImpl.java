package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDetailDo;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderDetailQo;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderDetailGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.PurchaseOrderDetailStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.PurchaseOrderDetailDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.PurchaseOrderDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderDetail;
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
public class PurchaseOrderDetailGatewayImpl implements PurchaseOrderDetailGateway {

    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;
    private PurchaseOrderDetailDao purchaseOrderDetailDao;

    @Override
    public String addPurchaseOrderDetail(PurchaseOrderDetailDo purchaseOrderDetailDo) {
        PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetailStructMapper.INSTANCE.domainToDataObject(purchaseOrderDetailDo);
        int result = purchaseOrderDetailMapper.insert(purchaseOrderDetail);

        purchaseOrderDetailDo.setId(purchaseOrderDetail.getId());

         if (result > 0) {
             return purchaseOrderDetail.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchPurchaseOrderDetail(List<PurchaseOrderDetailDo> purchaseOrderDetailDos) {
        List<PurchaseOrderDetail> purchaseOrderDetails = PurchaseOrderDetailStructMapper.INSTANCE.domainToDataObjects(purchaseOrderDetailDos);

        return purchaseOrderDetailDao.batchSaveSomeColumn(purchaseOrderDetails);
    }

    @Override
    public String updatePurchaseOrderDetail(PurchaseOrderDetailDo purchaseOrderDetailDo) {
        PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetailStructMapper.INSTANCE.domainToDataObject(purchaseOrderDetailDo);
        int result = purchaseOrderDetailMapper.updateById(purchaseOrderDetail);
        if (result > 0) {
            return purchaseOrderDetail.getId();
        }else {
            return null;
        }
    }

    @Override
    public PurchaseOrderDetailDo getPurchaseOrderDetailById(String id) {
        PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailDao.getById(id);
        return PurchaseOrderDetailStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderDetail);
    }

    @Override
    public PurchaseOrderDetailDo getOnePurchaseOrderDetail(PurchaseOrderDetailQo purchaseOrderDetailQo) {
        PurchaseOrderDetail purchaseOrderDetail = purchaseOrderDetailDao.getOneForceLimit(purchaseOrderDetailQo);
        return PurchaseOrderDetailStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderDetail);
    }

    @Override
    public List<PurchaseOrderDetailDo> listPurchaseOrderDetail(PurchaseOrderDetailQo purchaseOrderDetailQo) {
        List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailDao.queryList(purchaseOrderDetailQo);
        return PurchaseOrderDetailStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrderDetails);
    }

    @Override
    public PageResult<PurchaseOrderDetailDo> pagePurchaseOrderDetail(PurchaseOrderDetailQo purchaseOrderDetailQo) {
        Page<PurchaseOrderDetail> page = purchaseOrderDetailDao.findPage(purchaseOrderDetailQo);
        return PageUtils.transferPage(PurchaseOrderDetailStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deletePurchaseOrderDetailById(String id) {
        return purchaseOrderDetailDao.removeById(id);
    }

    @Override
    public boolean deletePurchaseOrderDetailByIds(Collection<String> ids) {
        return purchaseOrderDetailDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisablePurchaseOrderDetailByIds(LpBaseParam param) {
        PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
        purchaseOrderDetail.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrderDetail.setId(id);
            purchaseOrderDetailDao.updateById(purchaseOrderDetail);
        }
        return true;
    }
}

