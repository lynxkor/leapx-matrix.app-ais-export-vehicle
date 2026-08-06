package com.leapmotor.export.vehicle.payablelist.gatewayimpl;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.dto.SoUpdatePriceReq;
import com.leapmotor.export.vehicle.payablelist.dto.SoUpdateStatusReq;
import com.leapmotor.export.vehicle.payablelist.gateway.SoOrderGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * SO订单领域网关实现（同JVM调用SO模块）
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Repository
@AllArgsConstructor
public class SoOrderGatewayImpl implements SoOrderGateway {

    // TODO: 注入 SO 模块 ApplicationService，同 JVM 调用
    // private SoOrderApplicationService soOrderApplicationService;

    @Override
    public List<SoOrderForPayableDto> queryQcPassedAndUnbound() {
        // TODO: 调用 soOrderApplicationService.queryQcPassedAndUnbound()
        log.warn("[SoOrderGatewayImpl] queryQcPassedAndUnbound 待实现，SO模块Service未注入");
        return Collections.emptyList();
    }

    @Override
    public boolean updatePrice(SoUpdatePriceReq req) {
        // TODO: 调用 soOrderApplicationService.updatePrice(req)
        log.warn("[SoOrderGatewayImpl] updatePrice 待实现，SO模块Service未注入");
        return false;
    }

    @Override
    public boolean updateStatusReceived(SoUpdateStatusReq req) {
        // TODO: 调用 soOrderApplicationService.updateStatusReceived(req)
        log.warn("[SoOrderGatewayImpl] updateStatusReceived 待实现，SO模块Service未注入");
        return false;
    }
}
