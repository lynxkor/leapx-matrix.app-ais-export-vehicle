package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.leapmotor.export.vehicle.po.gateway.PlaceholderCustomerDataGateway;
import com.leapmotor.export.vehicle.po.model.PlaceholderCustomerDataDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 【占位】客户主数据查询，暂返回 null（匹配留空）。TODO 团队真代码到位后替换。 */
@Slf4j
@Service
public class PlaceholderCustomerDataGatewayImpl implements PlaceholderCustomerDataGateway {

    @Override
    public PlaceholderCustomerDataDo getByExternalCode(String externalCustomerCode) {
        log.warn("【占位】客户主数据查询：externalCustomerCode={}, 暂返回 null", externalCustomerCode);
        return null;
    }
}
