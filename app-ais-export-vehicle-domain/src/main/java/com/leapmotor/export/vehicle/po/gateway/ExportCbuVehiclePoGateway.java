package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDo;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoQo;
import java.util.Collection;
import java.util.List;

/**
 * PO订单主表领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportCbuVehiclePoGateway  {

    /**
     * 新增ExportCbuVehiclePo
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String addExportCbuVehiclePo(ExportCbuVehiclePoDo purchaseOrderDo);

    /**
     * 新增ExportCbuVehiclePo
     *
     * @param purchaseOrderDos
     * @return 结果
     */
    int addBatchExportCbuVehiclePo(List<ExportCbuVehiclePoDo> purchaseOrderDos);

    /**
     * 修改ExportCbuVehiclePo
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String updateExportCbuVehiclePo(ExportCbuVehiclePoDo purchaseOrderDo);

    /**
     * 查询ExportCbuVehiclePo
     *
     * @param id
     * @return 结果
     */
    ExportCbuVehiclePoDo getExportCbuVehiclePoById(String id);

    /**
     * 唯一查询ExportCbuVehiclePo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    ExportCbuVehiclePoDo getOneExportCbuVehiclePo(ExportCbuVehiclePoQo purchaseOrderQo);

    /**
     * 列表查询ExportCbuVehiclePo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    List<ExportCbuVehiclePoDo> listExportCbuVehiclePo(ExportCbuVehiclePoQo purchaseOrderQo);

    /**
     * 分页查询ExportCbuVehiclePo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    PageResult<ExportCbuVehiclePoDo> pageExportCbuVehiclePo(ExportCbuVehiclePoQo purchaseOrderQo);

    /**
     * 删除ExportCbuVehiclePo
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportCbuVehiclePoById(String id);

    /**
     * 批量删除ExportCbuVehiclePo
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportCbuVehiclePoByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportCbuVehiclePo
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportCbuVehiclePoByIds(LpBaseParam param);
}
