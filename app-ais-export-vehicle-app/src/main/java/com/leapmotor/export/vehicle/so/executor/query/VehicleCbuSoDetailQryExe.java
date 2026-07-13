package com.leapmotor.export.vehicle.so.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoDetailParam;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;
import lombok.AllArgsConstructor;

/**
 *
 * VehicleCbuSoDetail查询操作执行器
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Component
public class VehicleCbuSoDetailQryExe {

    private VehicleCbuSoDetailGateway vehicleCbuSoDetailGateway;


    public PageResult<VehicleCbuSoDetailDo> pageVehicleCbuSoDetail(VehicleCbuSoDetailParam vehicleCbuSoDetailParam) {
        VehicleCbuSoDetailQo vehicleCbuSoDetailQo = VehicleCbuSoDetailDTOStructMapper.INSTANCE.paramToQuery(vehicleCbuSoDetailParam);
        return vehicleCbuSoDetailGateway.pageVehicleCbuSoDetail(vehicleCbuSoDetailQo);
    }
}
