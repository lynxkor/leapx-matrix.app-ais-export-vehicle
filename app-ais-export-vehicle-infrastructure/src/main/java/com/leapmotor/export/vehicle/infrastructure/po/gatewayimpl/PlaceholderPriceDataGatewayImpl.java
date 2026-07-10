package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.leapmotor.export.vehicle.po.gateway.PlaceholderPriceDataGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 【占位】价格主数据查询，暂返回 null（匹配留空）。TODO 团队真代码到位后替换。 */
@Slf4j
@Service
public class PlaceholderPriceDataGatewayImpl implements PlaceholderPriceDataGateway {

    @Override
    public BigDecimal getLatestApprovedPrice(String materialCode, Integer orderType, LocalDateTime orderCreateTime) {
        log.warn("【占位】价格主数据查询：materialCode={}, orderType={}, 暂返回 null", materialCode, orderType);
        return null;
    }
}
