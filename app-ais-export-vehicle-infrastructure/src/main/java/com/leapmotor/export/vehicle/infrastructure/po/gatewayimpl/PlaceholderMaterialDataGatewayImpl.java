package com.leapmotor.export.vehicle.infrastructure.po.gatewayimpl;

import com.leapmotor.export.vehicle.po.gateway.PlaceholderMaterialDataGateway;
import com.leapmotor.export.vehicle.po.model.PlaceholderMaterialDataDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** 【占位】整编主数据查询，暂返回 null（匹配留空）。TODO 团队真代码到位后替换。 */
@Slf4j
@Service
public class PlaceholderMaterialDataGatewayImpl implements PlaceholderMaterialDataGateway {

    @Override
    public PlaceholderMaterialDataDo getBySyncMaterialCode(String syncMaterialCode) {
        log.warn("【占位】整编主数据查询：syncMaterialCode={}, 暂返回 null", syncMaterialCode);
        return null;
    }
}
