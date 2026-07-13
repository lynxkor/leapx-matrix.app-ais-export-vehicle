package com.leapmotor.export.vehicle.so.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDTOStructMapper;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import lombok.AllArgsConstructor;

/**
 *
 * VehicleCbuSo查询操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Component
public class VehicleCbuSoQryExe {

    private VehicleCbuSoGateway vehicleCbuSoGateway;

    public PageResult<VehicleCbuSoDo> pageVehicleCbuSo(VehicleCbuSoParam vehicleCbuSoParam) {
        VehicleCbuSoQo vehicleCbuSoQo = VehicleCbuSoDTOStructMapper.INSTANCE.paramToQuery(vehicleCbuSoParam);
        return vehicleCbuSoGateway.pageVehicleCbuSo(vehicleCbuSoQo);
    }
}
