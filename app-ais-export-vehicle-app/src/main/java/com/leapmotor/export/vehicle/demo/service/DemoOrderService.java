package com.leapmotor.export.vehicle.demo.service;

import com.leapmotor.export.vehicle.demo.req.DemoOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.demo.req.DemoOrderParam;
import com.leapmotor.export.vehicle.demo.resp.DemoOrderResp;
import com.leapmotor.paltform.ais.common.model.response.PageResult;

import java.util.List;

/**
 * 整车订单示例 服务类
 */
public interface DemoOrderService {

    /**
     * 查询详情
     */
    DemoOrderResp getDemoOrderById(String id);

    /**
     * 列表查询
     */
    List<DemoOrderResp> listDemoOrder(DemoOrderParam demoOrderParam);

    /**
     * 分页查询
     */
    PageResult<DemoOrderResp> pageDemoOrder(DemoOrderParam demoOrderParam);

    /**
     * 新增/编辑
     */
    boolean addOrUpdateDemoOrder(DemoOrderAddOrUpdateReq demoOrderAddOrUpdateReq);

    /**
     * 删除
     */
    boolean deleteDemoOrderByIds(List<String> ids);
}
