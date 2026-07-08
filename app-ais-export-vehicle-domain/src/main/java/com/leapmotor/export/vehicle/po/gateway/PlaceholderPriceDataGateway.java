package com.leapmotor.export.vehicle.po.gateway;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 【占位】价格主数据查询网关。真 Gateway 到位后替换。 */
public interface PlaceholderPriceDataGateway {

    /**
     * 按 物料编码 + 订单类型 + 订单创建时间 查价格主数据，多条取审批通过时间最新。
     *
     * @return 单价；匹配不到返回 null
     */
    BigDecimal getLatestApprovedPrice(String materialCode, String orderType, LocalDateTime orderCreateTime);
}
