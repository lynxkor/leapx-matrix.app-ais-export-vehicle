package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.export.vehicle.po.model.PlaceholderMaterialDataDo;

/** 【占位】整编主数据查询网关。真 Gateway 到位后替换。 */
public interface PlaceholderMaterialDataGateway {

    /** 按 18 位同步物料编码查整编主数据 */
    PlaceholderMaterialDataDo getBySyncMaterialCode(String syncMaterialCode);
}
