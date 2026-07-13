package com.leapmotor.export.vehicle.so.executor.cmd;

import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 *
 * VehicleCbuSoDetail 写操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Component
public class VehicleCbuSoDetailCmdExe {

    private VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;

    public String addOrUpdateVehicleCbuSoDetail(VehicleCbuSoDetailDo vehicleCbuSoDetailDo) {
        return vehicleCbuSoDetailDo.getId() == null
                ? vehicleCbuSoDetailGateway.addVehicleCbuSoDetail(vehicleCbuSoDetailDo)
                : vehicleCbuSoDetailGateway.updateVehicleCbuSoDetail(vehicleCbuSoDetailDo);
    }

    public boolean deleteVehicleCbuSoDetailByIds(List<String> ids) {
        return vehicleCbuSoDetailGateway.deleteVehicleCbuSoDetailByIds(ids);
    }

    public boolean enableOrDisableVehicleCbuSoDetailByIds(LpBaseParam param) {
        return vehicleCbuSoDetailGateway.enableOrDisableVehicleCbuSoDetailByIds(param);
    }
}
