package com.leapmotor.export.vehicle.so.service.impl;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.executor.cmd.VehicleCbuSoInterfaceSyncCmdExe;
import com.leapmotor.export.vehicle.so.executor.query.VehicleCbuSoInterfaceSyncQryExe;
import com.leapmotor.export.vehicle.so.convertor.VehicleCbuSoInterfaceSyncDTOStructMapper;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncAddOrUpdateReq;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.so.service.VehicleCbuSoInterfaceSyncService;
import org.springframework.stereotype.Service;

import java.util.List;

import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;

/**
 * <p>
 * SO接口同步记录表 服务实现类
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoInterfaceSyncServiceImpl implements VehicleCbuSoInterfaceSyncService {

    private VehicleCbuSoInterfaceSyncCmdExe vehicleCbuSoInterfaceSyncCmdExe;
    private VehicleCbuSoInterfaceSyncQryExe vehicleCbuSoInterfaceSyncQryExe;
    private VehicleCbuSoInterfaceSyncGateway vehicleCbuSoInterfaceSyncGateway;

    @Override
    public VehicleCbuSoInterfaceSyncResp getVehicleCbuSoInterfaceSyncById(String id) {
        VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo = vehicleCbuSoInterfaceSyncGateway.getVehicleCbuSoInterfaceSyncById(id);
        return VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.domainToResp(vehicleCbuSoInterfaceSyncDo);
    }

    @Override
    public List<VehicleCbuSoInterfaceSyncResp> listVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam) {
        VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.paramToQuery(vehicleCbuSoInterfaceSyncParam);
        List<VehicleCbuSoInterfaceSyncDo> vehicleCbuSoInterfaceSyncDoList = vehicleCbuSoInterfaceSyncGateway.listVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncQo);
        return VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.domainsToResps(vehicleCbuSoInterfaceSyncDoList);
    }

    @Override
    public PageResult<VehicleCbuSoInterfaceSyncResp> pageVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam) {
        PageResult<VehicleCbuSoInterfaceSyncDo> page = vehicleCbuSoInterfaceSyncQryExe.pageVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncParam);
        return VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.domainsToRespPage(page);
    }

    @Override
    public String addOrUpdateVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncAddOrUpdateReq vehicleCbuSoInterfaceSyncAddOrUpdateReq) {
        VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo = VehicleCbuSoInterfaceSyncDTOStructMapper.INSTANCE.addCmdToDomain(vehicleCbuSoInterfaceSyncAddOrUpdateReq);
        return vehicleCbuSoInterfaceSyncCmdExe.addOrUpdateVehicleCbuSoInterfaceSync(vehicleCbuSoInterfaceSyncDo);
    }

    @Override
    public boolean deleteVehicleCbuSoInterfaceSyncByIds(List<String> ids) {
        return vehicleCbuSoInterfaceSyncCmdExe.deleteVehicleCbuSoInterfaceSyncByIds(ids);
    }

    @Override
    public boolean enableOrDisableVehicleCbuSoInterfaceSyncByIds(LpBaseParam param) {
        return vehicleCbuSoInterfaceSyncCmdExe.enableOrDisableVehicleCbuSoInterfaceSyncByIds(param);
    }
}
