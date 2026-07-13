package com.leapmotor.export.vehicle.so.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;

import java.util.Collection;
import java.util.List;

/**
 * CBU整车销售订单主表领域网关
 *
 * @author lijianuo
 * @since 2026-07-13
 */
public interface VehicleCbuSoGateway {

    /**
     * 新增VehicleCbuSo
     *
     * @param vehicleCbuSoDo
     * @return 结果
     */
    String addVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo);

    /**
     * 新增VehicleCbuSo
     *
     * @param vehicleCbuSoDos
     * @return 结果
     */
    int addBatchVehicleCbuSo(List<VehicleCbuSoDo> vehicleCbuSoDos);

    /**
     * 修改VehicleCbuSo
     *
     * @param vehicleCbuSoDo
     * @return 结果
     */
    String updateVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo);

    /**
     * 查询VehicleCbuSo
     *
     * @param id
     * @return 结果
     */
    VehicleCbuSoDo getVehicleCbuSoById(String id);

    /**
     * 唯一查询VehicleCbuSo
     *
     * @param vehicleCbuSoQo
     * @return 结果
     */
    VehicleCbuSoDo getOneVehicleCbuSo(VehicleCbuSoQo vehicleCbuSoQo);

    /**
     * 列表查询VehicleCbuSo
     *
     * @param vehicleCbuSoQo
     * @return 结果
     */
    List<VehicleCbuSoDo> listVehicleCbuSo(VehicleCbuSoQo vehicleCbuSoQo);

    /**
     * 分页查询VehicleCbuSo
     *
     * @param vehicleCbuSoQo
     * @return 结果
     */
    PageResult<VehicleCbuSoDo> pageVehicleCbuSo(VehicleCbuSoQo vehicleCbuSoQo);

    /**
     * 删除VehicleCbuSo
     *
     * @param id
     * @return 结果
     */
    boolean deleteVehicleCbuSoById(String id);

    /**
     * 批量删除VehicleCbuSo
     *
     * @param ids
     * @return 结果
     */
    boolean deleteVehicleCbuSoByIds(Collection<String> ids);

    /**
     * 启用/禁用VehicleCbuSo
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableVehicleCbuSoByIds(LpBaseParam param);
}
