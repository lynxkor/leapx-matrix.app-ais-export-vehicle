package com.leapmotor.export.vehicle.so.executor.cmd;

import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 *
 * VehicleCbuSo 写操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Component
public class VehicleCbuSoCmdExe {

    private VehicleCbuSoGateway vehicleCbuSoGateway;

    public String addOrUpdateVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo) {
        return vehicleCbuSoDo.getId() == null ? vehicleCbuSoGateway.addVehicleCbuSo(vehicleCbuSoDo) : vehicleCbuSoGateway.updateVehicleCbuSo(vehicleCbuSoDo);
    }

    public boolean deleteVehicleCbuSoByIds(List<String> ids) {
        return vehicleCbuSoGateway.deleteVehicleCbuSoByIds(ids);
    }

    public boolean enableOrDisableVehicleCbuSoByIds(LpBaseParam param) {
        return vehicleCbuSoGateway.enableOrDisableVehicleCbuSoByIds(param);
    }
}
