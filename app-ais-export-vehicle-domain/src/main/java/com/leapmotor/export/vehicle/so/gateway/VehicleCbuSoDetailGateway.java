package com.leapmotor.export.vehicle.so.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDetailDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoDetailQo;

import java.util.Collection;
import java.util.List;

/**
 * SO车辆明细表领域网关
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface VehicleCbuSoDetailGateway {

    /**
     * 新增VehicleCbuSoDetail
     *
     * @param vehicleCbuSoDetailDo
     * @return 结果
     */
    String addVehicleCbuSoDetail(VehicleCbuSoDetailDo vehicleCbuSoDetailDo);

    /**
     * 新增VehicleCbuSoDetail
     *
     * @param vehicleCbuSoDetailDos
     * @return 结果
     */
    int addBatchVehicleCbuSoDetail(List<VehicleCbuSoDetailDo> vehicleCbuSoDetailDos);

    /**
     * 修改VehicleCbuSoDetail
     *
     * @param vehicleCbuSoDetailDo
     * @return 结果
     */
    String updateVehicleCbuSoDetail(VehicleCbuSoDetailDo vehicleCbuSoDetailDo);

    /**
     * 查询VehicleCbuSoDetail
     *
     * @param id
     * @return 结果
     */
    VehicleCbuSoDetailDo getVehicleCbuSoDetailById(String id);

    /**
     * 唯一查询VehicleCbuSoDetail
     *
     * @param vehicleCbuSoDetailQo
     * @return 结果
     */
    VehicleCbuSoDetailDo getOneVehicleCbuSoDetail(VehicleCbuSoDetailQo vehicleCbuSoDetailQo);

    /**
     * 列表查询VehicleCbuSoDetail
     *
     * @param vehicleCbuSoDetailQo
     * @return 结果
     */
    List<VehicleCbuSoDetailDo> listVehicleCbuSoDetail(VehicleCbuSoDetailQo vehicleCbuSoDetailQo);

    /**
     * 分页查询VehicleCbuSoDetail
     *
     * @param vehicleCbuSoDetailQo
     * @return 结果
     */
    PageResult<VehicleCbuSoDetailDo> pageVehicleCbuSoDetail(VehicleCbuSoDetailQo vehicleCbuSoDetailQo);

    /**
     * 删除VehicleCbuSoDetail
     *
     * @param id
     * @return 结果
     */
    boolean deleteVehicleCbuSoDetailById(String id);

    /**
     * 批量删除VehicleCbuSoDetail
     *
     * @param ids
     * @return 结果
     */
    boolean deleteVehicleCbuSoDetailByIds(Collection<String> ids);

    /**
     * 启用/禁用VehicleCbuSoDetail
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableVehicleCbuSoDetailByIds(LpBaseParam param);
}
