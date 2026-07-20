package com.leapmotor.export.vehicle.infrastructure.so.gatewayimpl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoDetail;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoDetailMapper;
import com.leapmotor.export.vehicle.infrastructure.so.mapper.VehicleCbuSoInterfaceSyncMapper;
import com.leapmotor.export.vehicle.infrastructure.so.entity.VehicleCbuSoInterfaceSync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    private VehicleCbuSoDetailMapper vehicleCbuSoDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo) {
        VehicleCbuSoInterfaceSync vehicleCbuSoInterfaceSync = VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.domainToDataObject(vehicleCbuSoInterfaceSyncDo);
        int result = vehicleCbuSoInterfaceSyncMapper.insert(vehicleCbuSoInterfaceSync);

        vehicleCbuSoInterfaceSyncDo.setId(vehicleCbuSoInterfaceSync.getId());

        if (result > 0) {
            refreshDetailSyncSnapshot(vehicleCbuSoInterfaceSyncDo);
            return vehicleCbuSoInterfaceSync.getId();
        }
        return null;
    }

    /**
     * 根据接口同步记录刷新明细表同步快照。
     * 同步表 so_id 实际存的是主表 id，明细表 1:1 复用主表 id 作为自身主键，故匹配明细表 id。
     */
    private void refreshDetailSyncSnapshot(VehicleCbuSoInterfaceSyncDo syncDo) {
        if (StringUtils.isBlank(syncDo.getSoId()) || Objects.isNull(syncDo.getInterfaceType())) {
            return;
        }
        Integer interfaceType = syncDo.getInterfaceType();
        UpdateWrapper<VehicleCbuSoDetail> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", syncDo.getSoId());

        if (interfaceType == 1 || interfaceType == 2) {
            updateWrapper.set("sync_aps_result", syncDo.getResultStatus())
                    .set("sync_aps_time", syncDo.getCallTime())
                    .set("sync_aps_response_info", syncDo.getResponseInfo());
        } else if (interfaceType == 3) {
            updateWrapper.set("sync_customer_result", syncDo.getResultStatus())
                    .set("sync_customer_time", syncDo.getCallTime())
                    .set("sync_customer_response_info", syncDo.getResponseInfo());
        } else if (interfaceType == 4) {
            updateWrapper.set("sync_sap_result", syncDo.getResultStatus())
                    .set("sync_sap_time", syncDo.getCallTime())
                    .set("sync_sap_response_info", syncDo.getResponseInfo());
        } else {
            return;
        }
        vehicleCbuSoDetailMapper.update(null, updateWrapper);
    }

    @Override
    public int addBatchVehicleCbuSoInterfaceSync(List<VehicleCbuSoInterfaceSyncDo> vehicleCbuSoInterfaceSyncDos) {
        List<VehicleCbuSoInterfaceSync> vehicleCbuSoInterfaceSyncs = VehicleCbuSoInterfaceSyncStructMapper.INSTANCE.domainToDataObjects(vehicleCbuSoInterfaceSyncDos);
        // 显式填充 owner_user_id / owner_org_id 默认值，避免 NOT NULL 字段报错
        for (VehicleCbuSoInterfaceSync entity : vehicleCbuSoInterfaceSyncs) {
            if (entity.getOwnerUserId() == null) {
                entity.setOwnerUserId(0L);
            }
            if (entity.getOwnerOrgId() == null) {
                entity.setOwnerOrgId(0L);
            }
        }
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

