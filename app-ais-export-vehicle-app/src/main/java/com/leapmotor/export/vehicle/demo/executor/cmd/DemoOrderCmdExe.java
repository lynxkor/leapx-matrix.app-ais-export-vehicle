package com.leapmotor.export.vehicle.demo.executor.cmd;

import com.leapmotor.export.vehicle.demo.gateway.DemoOrderGateway;
import com.leapmotor.export.vehicle.demo.model.DemoOrderDo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 整车订单示例 写操作执行器
 */
@AllArgsConstructor
@Component
@Transactional(rollbackFor = Exception.class)
public class DemoOrderCmdExe {

    private DemoOrderGateway demoOrderGateway;

    public String addOrUpdateDemoOrder(DemoOrderDo demoOrderDo) {
        return demoOrderDo.getId() == null
                ? demoOrderGateway.addDemoOrder(demoOrderDo)
                : demoOrderGateway.updateDemoOrder(demoOrderDo);
    }

    public boolean deleteDemoOrderByIds(List<String> ids) {
        return demoOrderGateway.deleteDemoOrderByIds(ids);
    }
}
