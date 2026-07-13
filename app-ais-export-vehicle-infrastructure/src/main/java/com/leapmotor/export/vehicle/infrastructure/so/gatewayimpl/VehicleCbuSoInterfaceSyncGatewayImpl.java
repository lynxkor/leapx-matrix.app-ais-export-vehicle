package com.leapmotor.export.vehicle.infrastructure.so.gatewayimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.platform.ais.common.mybatis.utils.PageUtils;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoInterfaceSyncGateway;
import lombok.AllArgsConstructor;
import com.leapmotor.export.vehicle.infrastructure.so.convertor.VehicleCbuSoInterfaceSyncStructMapper;
import com.leapmotor.export.vehicle.infrastructure.so.dao.VehicleCbuSoInterfaceSyncDao;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoInterfaceSyncMapper;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoInterfaceSync;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * SO接口同步记录表领域网关实现
 *
 * @author lijianuo
 * @since 2026-07-13
 */
@AllArgsConstructor
@Service
public class VehicleCbuSoInterfaceSyncGatewayImpl implements VehicleCbuSoInterfaceSyncGateway {

    private VehicleCbuSoInterfaceSyncMapper vehicleCbuSoInterfaceSyncMapper;
    private VehicleCbuSoInterfaceSyncDao vehicleCbuSoInterfaceSyncDao;

    @Override
    public String addVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo) {
        VehicleCbuSoInterfaceSync vehicleCbuSoInterfaceSync = VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.domainToDataObject(vehicleCbuSoInterfaceSyncDo);
        int result = vehicleCbuSoInterfaceSyncMapper.insert(vehicleCbuSoInterfaceSync);

        vehicleCbuSoInterfaceSyncDo.setId(vehicleCbuSoInterfaceSync.getId());

        if (result > 0) {
            return vehicleCbuSoInterfaceSync.getId();
        } else {
            return null;
        }
    }

    @Override
    public int addBatchVehicleCbuSoInterfaceSync(List<VehicleCbuSoInterfaceSyncDo> vehicleCbuSoInterfaceSyncDos) {
        List<VehicleCbuSoInterfaceSync> vehicleCbuSoInterfaceSyncs = VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.domainToDataObjects(vehicleCbuSoInterfaceSyncDos);

        return vehicleCbuSoInterfaceSyncDao.batchSaveSomeColumn(vehicleCbuSoInterfaceSyncs);
    }

    @Override
    public String updateVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo) {
        VehicleCbuSoInterfaceSync vehicleCbuSoInterfaceSync = VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.domainToDataObject(vehicleCbuSoInterfaceSyncDo);
        int result = vehicleCbuSoInterfaceSyncMapper.updateById(vehicleCbuSoInterfaceSync);
        if (result > 0) {
            return vehicleCbuSoInterfaceSync.getId();
        } else {
            return null;
        }
    }

    @Override
    public VehicleCbuSoInterfaceSyncDo getVehicleCbuSoInterfaceSyncById(String id) {
        VehicleCbuSoInterfaceSync vehicleCbuSoInterfaceSync = vehicleCbuSoInterfaceSyncDao.getById(id);
        return VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSoInterfaceSync);
    }

    @Override
    public VehicleCbuSoInterfaceSyncDo getOneVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo) {
        VehicleCbuSoInterfaceSync vehicleCbuSoInterfaceSync = vehicleCbuSoInterfaceSyncDao.getOneForceLimit(vehicleCbuSoInterfaceSyncQo);
        return VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.dataObjectToDomain(vehicleCbuSoInterfaceSync);
    }

    @Override
    public List<VehicleCbuSoInterfaceSyncDo> listVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo) {
        List<VehicleCbuSoInterfaceSync> vehicleCbuSoInterfaceSyncs = vehicleCbuSoInterfaceSyncDao.queryList(vehicleCbuSoInterfaceSyncQo);
        return VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.dataObjectsToDomains(vehicleCbuSoInterfaceSyncs);
    }

    @Override
    public PageResult<VehicleCbuSoInterfaceSyncDo> pageVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo) {
        Page<VehicleCbuSoInterfaceSync> page = vehicleCbuSoInterfaceSyncDao.findPage(vehicleCbuSoInterfaceSyncQo);
        return PageUtils.transferPage(VehicleCbuSoInterfaceSyncStructMapper.INSTANCE::dataObjectsToDomains, page);
    }

    @Override
    public boolean deleteVehicleCbuSoInterfaceSyncById(String id) {
        return vehicleCbuSoInterfaceSyncDao.removeById(id);
    }

    @Override
    public boolean deleteVehicleCbuSoInterfaceSyncByIds(Collection<String> ids) {
        return vehicleCbuSoInterfaceSyncDao.removeByIds(ids);
    }

    @Override
    public boolean enableOrDisableVehicleCbuSoInterfaceSyncByIds(LpBaseParam param) {
        VehicleCbuSoInterfaceSync vehicleCbuSoInterfaceSync = new VehicleCbuSoInterfaceSync();
        vehicleCbuSoInterfaceSync.setStatus(param.getStatus());
        for (String id : param.getIdSet()) {
            vehicleCbuSoInterfaceSync.setId(id);
            vehicleCbuSoInterfaceSyncDao.updateById(vehicleCbuSoInterfaceSync);
        }
        return true;
    }
}

