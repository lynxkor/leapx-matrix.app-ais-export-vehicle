package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.po.model.PurchaseOrderGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.PurchaseOrderGuaranteeQo;
import com.leapmotor.export.vehicle.po.gateway.PurchaseOrderGuaranteeGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.po.convertor.PurchaseOrderGuaranteeStructMapper;
import com.leapmotor.export.vehicle.infrastructure.po.dao.PurchaseOrderGuaranteeDao;
import com.leapmotor.export.vehicle.infrastructure.po.mapper.PurchaseOrderGuaranteeMapper;
import com.leapmotor.export.vehicle.infrastructure.po.entity.PurchaseOrderGuarantee;
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
public class PurchaseOrderGuaranteeGatewayImpl implements PurchaseOrderGuaranteeGateway {

    private PurchaseOrderGuaranteeMapper purchaseOrderGuaranteeMapper;
    private PurchaseOrderGuaranteeDao purchaseOrderGuaranteeDao;

    @Override
    public String addPurchaseOrderGuarantee(PurchaseOrderGuaranteeDo purchaseOrderGuaranteeDo) {
        PurchaseOrderGuarantee purchaseOrderGuarantee = PurchaseOrderGuaranteeStructMapper.INSTANCE.domainToDataObject(purchaseOrderGuaranteeDo);
        int result = purchaseOrderGuaranteeMapper.insert(purchaseOrderGuarantee);

        purchaseOrderGuaranteeDo.setId(purchaseOrderGuarantee.getId());

         if (result > 0) {
             return purchaseOrderGuarantee.getId();
         }else {
             return null;
         }
    }

    @Override
    public int addBatchPurchaseOrderGuarantee(List<PurchaseOrderGuaranteeDo> purchaseOrderGuaranteeDos) {
        List<PurchaseOrderGuarantee> purchaseOrderGuarantees = PurchaseOrderGuaranteeStructMapper.INSTANCE.domainToDataObjects(purchaseOrderGuaranteeDos);

        return purchaseOrderGuaranteeDao.batchSaveSomeColumn(purchaseOrderGuarantees);
    }

    @Override
    public String updatePurchaseOrderGuarantee(PurchaseOrderGuaranteeDo purchaseOrderGuaranteeDo) {
        PurchaseOrderGuarantee purchaseOrderGuarantee = PurchaseOrderGuaranteeStructMapper.INSTANCE.domainToDataObject(purchaseOrderGuaranteeDo);
        int result = purchaseOrderGuaranteeMapper.updateById(purchaseOrderGuarantee);
        if (result > 0) {
            return purchaseOrderGuarantee.getId();
        }else {
            return null;
        }
    }

    @Override
    public PurchaseOrderGuaranteeDo getPurchaseOrderGuaranteeById(String id) {
        PurchaseOrderGuarantee purchaseOrderGuarantee = purchaseOrderGuaranteeDao.getById(id);
        return PurchaseOrderGuaranteeStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderGuarantee);
    }

    @Override
    public PurchaseOrderGuaranteeDo getOnePurchaseOrderGuarantee(PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo) {
        PurchaseOrderGuarantee purchaseOrderGuarantee = purchaseOrderGuaranteeDao.getOneForceLimit(purchaseOrderGuaranteeQo);
        return PurchaseOrderGuaranteeStructMapper.INSTANCE.dataObjectToDomain(purchaseOrderGuarantee);
    }

    @Override
    public List<PurchaseOrderGuaranteeDo> listPurchaseOrderGuarantee(PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo) {
        List<PurchaseOrderGuarantee> purchaseOrderGuarantees = purchaseOrderGuaranteeDao.queryList(purchaseOrderGuaranteeQo);
        return PurchaseOrderGuaranteeStructMapper.INSTANCE.dataObjectsToDomains(purchaseOrderGuarantees);
    }

    @Override
    public PageResult<PurchaseOrderGuaranteeDo> pagePurchaseOrderGuarantee(PurchaseOrderGuaranteeQo purchaseOrderGuaranteeQo) {
        Page<PurchaseOrderGuarantee> page = purchaseOrderGuaranteeDao.findPage(purchaseOrderGuaranteeQo);
        return PageUtils.transferPage(PurchaseOrderGuaranteeStructMapper.INSTANCE::dataObjectsToDomains,page);
    }

    @Override
    public boolean deletePurchaseOrderGuaranteeById(String id) {
        return purchaseOrderGuaranteeDao.removeById(id);
    }

    @Override
    public boolean deletePurchaseOrderGuaranteeByIds(Collection<String> ids) {
        return purchaseOrderGuaranteeDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisablePurchaseOrderGuaranteeByIds(LpBaseParam param) {
        PurchaseOrderGuarantee purchaseOrderGuarantee = new PurchaseOrderGuarantee();
        purchaseOrderGuarantee.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            purchaseOrderGuarantee.setId(id);
            purchaseOrderGuaranteeDao.updateById(purchaseOrderGuarantee);
        }
        return true;
    }
}

