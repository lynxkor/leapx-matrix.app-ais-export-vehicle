package com.leapmotor.export.vehicle.so.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoInterfaceSyncDTOStructMapper;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import lombok.AllArgsConstructor;

/**
 *
 * VehicleCbuSoInterfaceSync查询操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Component
public class VehicleCbuSoInterfaceSyncQryExe {

    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

    public PageResult<VehicleCbuSoInterfaceSyncDo> pageVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam) {
        VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.paramToQuery(vehicleCbuSoInterfaceSyncParam);
        return vehicleCbuSoInterfaceSyncGateway.pageVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncQo);
    }
}
