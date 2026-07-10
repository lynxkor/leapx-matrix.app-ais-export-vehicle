package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import java.util.Collection;
import java.util.List;

/**
 * PO订单主表领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoGateway  {

    /**
     * 新增ExportVehicleCbuPo
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String addExportVehicleCbuPo(ExportVehicleCbuPoDo purchaseOrderDo);

    /**
     * 新增ExportVehicleCbuPo
     *
     * @param purchaseOrderDos
     * @return 结果
     */
    int addBatchExportVehicleCbuPo(List<ExportVehicleCbuPoDo> purchaseOrderDos);

    /**
     * 修改ExportVehicleCbuPo
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String updateExportVehicleCbuPo(ExportVehicleCbuPoDo purchaseOrderDo);

    /**
     * 查询ExportVehicleCbuPo
     *
     * @param id
     * @return 结果
     */
    ExportVehicleCbuPoDo getExportVehicleCbuPoById(String id);

    /**
     * 唯一查询ExportVehicleCbuPo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    ExportVehicleCbuPoDo getOneExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo);

    /**
     * 列表查询ExportVehicleCbuPo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    List<ExportVehicleCbuPoDo> listExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo);

    /**
     * 分页查询ExportVehicleCbuPo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    PageResult<ExportVehicleCbuPoDo> pageExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo);

    /**
     * 删除ExportVehicleCbuPo
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportVehicleCbuPoById(String id);

    /**
     * 批量删除ExportVehicleCbuPo
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleCbuPoByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportVehicleCbuPo
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleCbuPoByIds(LpBaseParam param);
}
