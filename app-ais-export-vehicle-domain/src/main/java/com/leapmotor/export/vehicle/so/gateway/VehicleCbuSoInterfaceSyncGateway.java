package com.leapmotor.export.vehicle.so.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoInterfaceSyncDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoInterfaceSyncQo;

import java.util.Collection;
import java.util.List;

/**
 * SO接口同步记录表领域网关
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface VehicleCbuSoInterfaceSyncGateway {

    /**
     * 新增VehicleCbuSoInterfaceSync
     *
     * @param vehicleCbuSoInterfaceSyncDo
     * @return 结果
     */
    String addVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo);

    /**
     * 新增VehicleCbuSoInterfaceSync
     *
     * @param vehicleCbuSoInterfaceSyncDos
     * @return 结果
     */
    int addBatchVehicleCbuSoInterfaceSync(List<VehicleCbuSoInterfaceSyncDo> vehicleCbuSoInterfaceSyncDos);

    /**
     * 修改VehicleCbuSoInterfaceSync
     *
     * @param vehicleCbuSoInterfaceSyncDo
     * @return 结果
     */
    String updateVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncDo vehicleCbuSoInterfaceSyncDo);

    /**
     * 查询VehicleCbuSoInterfaceSync
     *
     * @param id
     * @return 结果
     */
    VehicleCbuSoInterfaceSyncDo getVehicleCbuSoInterfaceSyncById(String id);

    /**
     * 唯一查询VehicleCbuSoInterfaceSync
     *
     * @param vehicleCbuSoInterfaceSyncQo
     * @return 结果
     */
    VehicleCbuSoInterfaceSyncDo getOneVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo);

    /**
     * 列表查询VehicleCbuSoInterfaceSync
     *
     * @param vehicleCbuSoInterfaceSyncQo
     * @return 结果
     */
    List<VehicleCbuSoInterfaceSyncDo> listVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo);

    /**
     * 分页查询VehicleCbuSoInterfaceSync
     *
     * @param vehicleCbuSoInterfaceSyncQo
     * @return 结果
     */
    PageResult<VehicleCbuSoInterfaceSyncDo> pageVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncQo vehicleCbuSoInterfaceSyncQo);

    /**
     * 删除VehicleCbuSoInterfaceSync
     *
     * @param id
     * @return 结果
     */
    boolean deleteVehicleCbuSoInterfaceSyncById(String id);

    /**
     * 批量删除VehicleCbuSoInterfaceSync
     *
     * @param ids
     * @return 结果
     */
    boolean deleteVehicleCbuSoInterfaceSyncByIds(Collection<String> ids);

    /**
     * 启用/禁用VehicleCbuSoInterfaceSync
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableVehicleCbuSoInterfaceSyncByIds(LpBaseParam param);
}
