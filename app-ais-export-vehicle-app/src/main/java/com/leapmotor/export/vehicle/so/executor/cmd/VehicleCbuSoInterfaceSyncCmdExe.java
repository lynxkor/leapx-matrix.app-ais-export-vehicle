package com.leapmotor.export.vehicle.so.executor.cmd;


import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 *
 * VehicleCbuSoInterfaceSync 写操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Component
public class VehicleCbuSoInterfaceSyncCmdExe {

    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

    public String addOrUpdateVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo) {
        return vehicleCbuSoInterfaceSyncDo.getId() == null
                ? vehicleCbuSoInterfaceSyncGateway.addVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncDo)
                : vehicleCbuSoInterfaceSyncGateway.updateVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncDo);
    }

    public boolean deleteVehicleCbuSoInterfaceSyncByIds(List<String> ids) {
        return vehicleCbuSoInterfaceSyncGateway.deleteVehicleCbuSoInterfaceSyncByIds(ids);
    }

    public boolean enableOrDisableVehicleCbuSoInterfaceSyncByIds(LpBaseParam param) {
        return vehicleCbuSoInterfaceSyncGateway.enableOrDisableVehicleCbuSoInterfaceSyncByIds(param);
    }
}
