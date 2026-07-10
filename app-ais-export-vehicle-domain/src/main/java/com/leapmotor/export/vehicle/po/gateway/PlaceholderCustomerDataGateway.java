package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.export.vehicle.po.model.PlaceholderCustomerDataDo;

/**
 * 【占位】客户主数据查询网关。类名带 Placeholder 前缀避免与团队真代码冲突。
 * 真客户主数据 Gateway 到位后，删本占位、改 createFromHongqi 注入真 Gateway。
 */
public interface PlaceholderCustomerDataGateway {

    /** 按外部客户编码查客户主数据 */
    PlaceholderCustomerDataDo getByExternalCode(String externalCustomerCode);
}
