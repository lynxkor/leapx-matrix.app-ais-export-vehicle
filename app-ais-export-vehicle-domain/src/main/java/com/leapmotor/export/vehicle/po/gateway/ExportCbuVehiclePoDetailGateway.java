package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoDetailQo;
import java.util.Collection;
import java.util.List;

/**
 * PO订单明细表领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportCbuVehiclePoDetailGateway  {

    /**
     * 新增ExportCbuVehiclePoDetail
     *
     * @param purchaseOrderDetailDo
     * @return 结果
     */
    String addExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailDo purchaseOrderDetailDo);

    /**
     * 新增ExportCbuVehiclePoDetail
     *
     * @param purchaseOrderDetailDos
     * @return 结果
     */
    int addBatchExportCbuVehiclePoDetail(List<ExportCbuVehiclePoDetailDo> purchaseOrderDetailDos);

    /**
     * 修改ExportCbuVehiclePoDetail
     *
     * @param purchaseOrderDetailDo
     * @return 结果
     */
    String updateExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailDo purchaseOrderDetailDo);

    /**
     * 查询ExportCbuVehiclePoDetail
     *
     * @param id
     * @return 结果
     */
    ExportCbuVehiclePoDetailDo getExportCbuVehiclePoDetailById(String id);

    /**
     * 唯一查询ExportCbuVehiclePoDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    ExportCbuVehiclePoDetailDo getOneExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailQo purchaseOrderDetailQo);

    /**
     * 列表查询ExportCbuVehiclePoDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    List<ExportCbuVehiclePoDetailDo> listExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailQo purchaseOrderDetailQo);

    /**
     * 分页查询ExportCbuVehiclePoDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    PageResult<ExportCbuVehiclePoDetailDo> pageExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailQo purchaseOrderDetailQo);

    /**
     * 删除ExportCbuVehiclePoDetail
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportCbuVehiclePoDetailById(String id);

    /**
     * 批量删除ExportCbuVehiclePoDetail
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportCbuVehiclePoDetailByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportCbuVehiclePoDetail
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportCbuVehiclePoDetailByIds(LpBaseParam param);
}
