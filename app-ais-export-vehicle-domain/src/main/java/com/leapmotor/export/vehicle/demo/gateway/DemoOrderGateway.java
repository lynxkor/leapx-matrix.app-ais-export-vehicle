package com.leapmotor.export.vehicle.demo.gateway;

import com.leapmotor.export.vehicle.demo.model.DemoOrderDo;
import com.leapmotor.export.vehicle.demo.query.DemoOrderQo;
import com.leapmotor.paltform.ais.common.model.response.PageResult;

import java.util.Collection;
import java.util.List;

/**
 * 整车订单示例领域网关
 */
public interface DemoOrderGateway {

    /**
     * 新增
     */
    String addDemoOrder(DemoOrderDo demoOrderDo);

    /**
     * 修改
     */
    String updateDemoOrder(DemoOrderDo demoOrderDo);

    /**
     * 查询
     */
    DemoOrderDo getDemoOrderById(String id);

    /**
     * 列表查询
     */
    List<DemoOrderDo> listDemoOrder(DemoOrderQo demoOrderQo);

    /**
     * 分页查询
     */
    PageResult<DemoOrderDo> pageDemoOrder(DemoOrderQo demoOrderQo);

    /**
     * 批量删除
     */
    boolean deleteDemoOrderByIds(Collection<String> ids);
}
