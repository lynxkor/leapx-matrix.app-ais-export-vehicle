package com.leapmotor.export.vehicle.so.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncParam;
import com.leapmotor.export.vehicle.so.resp.VehicleCbuSoInterfaceSyncResp;
import com.leapmotor.export.vehicle.so.req.VehicleCbuSoInterfaceSyncAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * SO接口同步记录表 服务类
 * </p>
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface VehicleCbuSoInterfaceSyncService {

    /**
     * 查询VehicleCbuSoInterfaceSync
     *
     * @param id
     * @return VehicleCbuSoInterfaceSync
     */
    VehicleCbuSoInterfaceSyncResp getVehicleCbuSoInterfaceSyncById(String id);

    /**
     * 查询VehicleCbuSoInterfaceSync列表
     *
     * @param vehicleCbuSoInterfaceSyncParam
     * @return VehicleCbuSoInterfaceSync集合
     */
    List<VehicleCbuSoInterfaceSyncResp> listVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam);

    /**
     * 分页查询VehicleCbuSoInterfaceSync列表
     *
     * @param vehicleCbuSoInterfaceSyncParam
     * @return VehicleCbuSoInterfaceSync集合
     */
    PageResult<VehicleCbuSoInterfaceSyncResp> pageVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncParam vehicleCbuSoInterfaceSyncParam);

    /**
     * 新增/编辑VehicleCbuSoInterfaceSync
     *
     * @param vehicleCbuSoInterfaceSyncAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateVehicleCbuSoInterfaceSync(VehicleCbuSoInterfaceSyncAddOrUpdateReq vehicleCbuSoInterfaceSyncAddOrUpdateReq);


    /**
     * 删除VehicleCbuSoInterfaceSync信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteVehicleCbuSoInterfaceSyncByIds(List<String> ids);


    /**
     * 启用/禁用VehicleCbuSoInterfaceSync信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableVehicleCbuSoInterfaceSyncByIds(LpBaseParam param);

}
