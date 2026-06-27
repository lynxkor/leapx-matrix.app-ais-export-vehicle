package com.leapmotor.export.vehicle.infrastructure.demo.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.export.vehicle.demo.gateway.DemoOrderGateway;
import com.leapmotor.export.vehicle.demo.model.DemoOrderDo;
import com.leapmotor.export.vehicle.demo.query.DemoOrderQo;
import com.leapmotor.export.vehicle.infrastructure.demo.convertor.DemoOrderStructMapper;
import com.leapmotor.export.vehicle.infrastructure.demo.dao.DemoOrderDao;
import com.leapmotor.export.vehicle.infrastructure.demo.entity.DemoOrder;
import com.leapmotor.export.vehicle.infrastructure.demo.mapper.DemoOrderMapper;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 整车订单示例领域网关实现
 */
@AllArgsConstructor
@Service
public class DemoOrderGatewayImpl implements DemoOrderGateway {

    private DemoOrderMapper demoOrderMapper;
    private DemoOrderDao demoOrderDao;

    @Override
    public String addDemoOrder(DemoOrderDo demoOrderDo) {
        DemoOrder demoOrder = DemoOrderStructMapper.INSTANCE.domainToDataObject(demoOrderDo);
        int result = demoOrderMapper.insert(demoOrder);
        demoOrderDo.setId(demoOrder.getId());
        if (result > 0) {
            return demoOrder.getId();
        } else {
            return null;
        }
    }

    @Override
    public String updateDemoOrder(DemoOrderDo demoOrderDo) {
        DemoOrder demoOrder = DemoOrderStructMapper.INSTANCE.domainToDataObject(demoOrderDo);
        int result = demoOrderMapper.updateById(demoOrder);
        if (result > 0) {
            return demoOrder.getId();
        } else {
            return null;
        }
    }

    @Override
    public DemoOrderDo getDemoOrderById(String id) {
        DemoOrder demoOrder = demoOrderDao.getById(id);
        return DemoOrderStructMapper.INSTANCE.dataObjectToDomain(demoOrder);
    }

    @Override
    public List<DemoOrderDo> listDemoOrder(DemoOrderQo demoOrderQo) {
        List<DemoOrder> demoOrderList = demoOrderDao.queryList(demoOrderQo);
        return DemoOrderStructMapper.INSTANCE.dataObjectsToDomains(demoOrderList);
    }

    @Override
    public PageResult<DemoOrderDo> pageDemoOrder(DemoOrderQo demoOrderQo) {
        Page<DemoOrder> page = demoOrderDao.findPage(demoOrderQo);
        return PageUtils.transferPage(DemoOrderStructMapper.INSTANCE::dataObjectsToDomains, page);
    }

    @Override
    public boolean deleteDemoOrderByIds(Collection<String> ids) {
        return demoOrderDao.removeByIds(ids);
    }
}
