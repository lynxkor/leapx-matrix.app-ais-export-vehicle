package com.leapmotor.export.vehicle.so.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;

import java.time.LocalDateTime;
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
     * 批量新增VehicleCbuSo，并将生成的主键回写到领域对象
     *
     * @param vehicleCbuSoDos
     * @return 结果
     */
    int batchAddVehicleCbuSo(List<VehicleCbuSoDo> vehicleCbuSoDos);

    /**
     * 修改VehicleCbuSo
     *
     * @param vehicleCbuSoDo
     * @return 结果
     */
    String updateVehicleCbuSo(VehicleCbuSoDo vehicleCbuSoDo);

    /**
     * 批量按ID修改VehicleCbuSo（仅更新入参非null字段，避免全量回写覆盖并发变更）
     *
     * @param vehicleCbuSoDos 待更新领域对象列表（须携带ID与待更新字段）
     * @return 影响行数
     */
    int updateBatchById(List<VehicleCbuSoDo> vehicleCbuSoDos);

    /**
     * 查询VehicleCbuSo
     *
     * @param id
     * @return 结果
     */
    VehicleCbuSoDo getVehicleCbuSoById(String id);

    /**
     * 按 SO 单号查询 SO 主表
     *
     * @param soNo SO 单号
     * @return SO 领域对象
     */
    VehicleCbuSoDo getVehicleCbuSoBySoNo(String soNo);

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
     * 列表查询 SO 聚合车辆明细数据
     *
     * @param vehicleCbuSoQo
     * @return 结果
     */
    List<VehicleCbuSoDo> listSoList(VehicleCbuSoQo vehicleCbuSoQo);

    /**
     * 分页查询 SO 聚合车辆明细数据
     *
     * @param vehicleCbuSoQo
     * @return 结果
     */
    PageResult<VehicleCbuSoDo> pageSoList(VehicleCbuSoQo vehicleCbuSoQo);

    /**
     * 查询 SO 聚合详情（含车辆明细）
     *
     * @param id
     * @return 结果
     */
    VehicleCbuSoDo getSoDetailById(String id);

    /**
     * 按 ID 列表批量更新订单状态及下发/作废时间。
     *
     * @param ids         SO 主键列表
     * @param orderStatus 目标状态
     * @param issueTime   下发时间（可为空）
     * @param cancelTime  作废时间（可为空）
     * @return 受影响行数
     */
    int batchUpdateStatus(List<String> ids, Integer orderStatus, LocalDateTime issueTime, LocalDateTime cancelTime);

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
