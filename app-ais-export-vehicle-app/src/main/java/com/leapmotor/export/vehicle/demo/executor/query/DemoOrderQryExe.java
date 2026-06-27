package com.leapmotor.export.vehicle.demo.executor.query;

import com.leapmotor.export.vehicle.demo.convertor.DemoOrderDTOStructMapper;
import com.leapmotor.export.vehicle.demo.gateway.DemoOrderGateway;
import com.leapmotor.export.vehicle.demo.model.DemoOrderDo;
import com.leapmotor.export.vehicle.demo.req.DemoOrderParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 整车订单示例 查询操作执行器
 */
@AllArgsConstructor
@Component
public class DemoOrderQryExe {

    private DemoOrderGateway demoOrderGateway;

    public PageResult<DemoOrderDo> pageDemoOrder(DemoOrderParam demoOrderParam) {
        return demoOrderGateway.pageDemoOrder(DemoOrderDTOStructMapper.INSTANCE.paramToQuery(demoOrderParam));
    }
}
