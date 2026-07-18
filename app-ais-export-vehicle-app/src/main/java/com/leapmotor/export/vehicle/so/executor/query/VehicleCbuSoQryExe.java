package com.leapmotor.export.vehicle.so.executor.query;

import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoDTOStructMapper;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
        return vehicleCbuSoGateway.pageSoList(vehicleCbuSoQo);
    }

    /**
     * 列表查询 SO 聚合车辆明细数据
     */
    public List<VehicleCbuSoDo> listSoList(VehicleCbuSoQo vehicleCbuSoQo) {
        return vehicleCbuSoGateway.listSoList(vehicleCbuSoQo);
    }

    /**
     * 根据主键查询 SO 聚合详情（含车辆明细）
     */
    public VehicleCbuSoDo getSoDetailById(String id) {
        return vehicleCbuSoGateway.getSoDetailById(id);
    }
}
