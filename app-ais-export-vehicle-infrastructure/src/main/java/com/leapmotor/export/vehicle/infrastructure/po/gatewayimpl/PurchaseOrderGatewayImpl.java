package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderDo;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderQo;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.PurchaseOrderStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.PurchaseOrderDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.PurchaseOrderMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrder;
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
public class PurchaseOrderGatewayImpl implements PurchaseOrderGateway {

    private PurchaseOrderMapper purchaseOrderMapper;
    private PurchaseOrderDao purchaseOrderDao;

    @Override
    public String addPurchaseOrder(PurchaseOrderDo purchaseOrderDo) {
        PurchaseOrder purchaseOrder = PurchaseOrderStructMapper.INSTANCE.domainToDataObject(purchaseOrderDo);
        int result = purchaseOrderMapper.insert(purchaseOrder);

        purchaseOrderDo.setId(purchaseOrder.getId());

         if (result > 0) {
             return purchaseOrder.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchPurchaseOrder(List<PurchaseOrderDo> purchaseOrderDos) {
        List<PurchaseOrder> purchaseOrders = PurchaseOrderStructMapper.INSTANCE.domainToDataObjects(purchaseOrderDos);

        return purchaseOrderDao.batchSaveSomeColumn(purchaseOrders);
    }

    @Override
    public String updatePurchaseOrder(PurchaseOrderDo purchaseOrderDo) {
        PurchaseOrder purchaseOrder = PurchaseOrderStructMapper.INSTANCE.domainToDataObject(purchaseOrderDo);
        int result = purchaseOrderMapper.updateById(purchaseOrder);
        if (result > 0) {
            return purchaseOrder.getId();
        }else {
            return null;
        }
    }

    @Override
    public PurchaseOrderDo getPurchaseOrderById(String id) {
        PurchaseOrder purchaseOrder = purchaseOrderDao.getById(id);
        return PurchaseOrderStructMapper.INSTANCE.dataObjectToDomain(purchaseOrder);
    }

    @Override
    public PurchaseOrderDo getOnePurchaseOrder(PurchaseOrderQo purchaseOrderQo) {
        PurchaseOrder purchaseOrder = purchaseOrderDao.getOneForceLimit(purchaseOrderQo);
        return PurchaseOrderStructMapper.INSTANCE.dataObjectToDomain(purchaseOrder);
    }

    @Override
    public List<PurchaseOrderDo> listPurchaseOrder(PurchaseOrderQo purchaseOrderQo) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderDao.queryList(purchaseOrderQo);
        return PurchaseOrderStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrders);
    }

    @Override
    public PageResult<PurchaseOrderDo> pagePurchaseOrder(PurchaseOrderQo purchaseOrderQo) {
        Page<PurchaseOrder> page = purchaseOrderDao.findPage(purchaseOrderQo);
        return PageUtils.transferPage(PurchaseOrderStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deletePurchaseOrderById(String id) {
        return purchaseOrderDao.removeById(id);
    }

    @Override
    public boolean deletePurchaseOrderByIds(Collection<String> ids) {
        return purchaseOrderDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisablePurchaseOrderByIds(LpBaseParam param) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrder.setId(id);
            purchaseOrderDao.updateById(purchaseOrder);
        }
        return true;
    }
}

