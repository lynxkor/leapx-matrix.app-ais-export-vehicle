package com.leapmotor.export.vehicle.demo.service.impl;

import com.leapmotor.export.vehicle.demo.convertor.DemoOrderDTOStructMapper;
import com.leapmotor.export.vehicle.demo.executor.cmd.DemoOrderCmdExe;
import com.leapmotor.export.vehicle.demo.executor.query.DemoOrderQryExe;
import com.leapmotor.export.vehicle.demo.gateway.DemoOrderGateway;
import com.leapmotor.export.vehicle.demo.model.DemoOrderDo;
import com.leapmotor.export.vehicle.demo.req.DemoOrderAddOrUpdateReq;
import com.leapmotor.export.vehicle.demo.req.DemoOrderParam;
import com.leapmotor.export.vehicle.demo.resp.DemoOrderResp;
import com.leapmotor.export.vehicle.demo.service.DemoOrderService;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 整车订单示例 服务实现类
 */
@AllArgsConstructor
@Service
public class DemoOrderServiceImpl implements DemoOrderService {

    private DemoOrderCmdExe demoOrderCmdExe;
    private DemoOrderQryExe demoOrderQryExe;
    private DemoOrderGateway demoOrderGateway;

    @Override
    public DemoOrderResp getDemoOrderById(String id) {
        DemoOrderDo demoOrderDo = demoOrderGateway.getDemoOrderById(id);
        return DemoOrderDTOStructMapper.INSTANCE.domainToResp(demoOrderDo);
    }

    @Override
    public List<DemoOrderResp> listDemoOrder(DemoOrderParam demoOrderParam) {
        List<DemoOrderDo> demoOrderDoList = demoOrderGateway.listDemoOrder(
                DemoOrderDTOStructMapper.INSTANCE.paramToQuery(demoOrderParam));
        return DemoOrderDTOStructMapper.INSTANCE.domainsToResps(demoOrderDoList);
    }

    @Override
    public PageResult<DemoOrderResp> pageDemoOrder(DemoOrderParam demoOrderParam) {
        PageResult<DemoOrderDo> page = demoOrderQryExe.pageDemoOrder(demoOrderParam);
        return DemoOrderDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public boolean addOrUpdateDemoOrder(DemoOrderAddOrUpdateReq demoOrderAddOrUpdateReq) {
        DemoOrderDo demoOrderDo = DemoOrderDTOStructMapper.INSTANCE.addCmdToDomain(demoOrderAddOrUpdateReq);
        return demoOrderCmdExe.addOrUpdateDemoOrder(demoOrderDo) != null;
    }

    @Override
    public boolean deleteDemoOrderByIds(List<String> ids) {
        return demoOrderCmdExe.deleteDemoOrderByIds(ids);
    }
}
