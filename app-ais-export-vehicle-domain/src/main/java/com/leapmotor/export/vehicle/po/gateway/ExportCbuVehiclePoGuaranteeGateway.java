package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoGuaranteeQo;
import java.util.Collection;
import java.util.List;

/**
 * PO-保函关联表(一个PO多保函)领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportCbuVehiclePoGuaranteeGateway  {

    /**
     * 新增ExportCbuVehiclePoGuarantee
     *
     * @param purchaseOrderGuaranteeDo
     * @return 结果
     */
    String addExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeDo purchaseOrderGuaranteeDo);

    /**
     * 新增ExportCbuVehiclePoGuarantee
     *
     * @param purchaseOrderGuaranteeDos
     * @return 结果
     */
    int addBatchExportCbuVehiclePoGuarantee(List<ExportCbuVehiclePoGuaranteeDo> purchaseOrderGuaranteeDos);

    /**
     * 修改ExportCbuVehiclePoGuarantee
     *
     * @param purchaseOrderGuaranteeDo
     * @return 结果
     */
    String updateExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeDo purchaseOrderGuaranteeDo);

    /**
     * 查询ExportCbuVehiclePoGuarantee
     *
     * @param id
     * @return 结果
     */
    ExportCbuVehiclePoGuaranteeDo getExportCbuVehiclePoGuaranteeById(String id);

    /**
     * 唯一查询ExportCbuVehiclePoGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    ExportCbuVehiclePoGuaranteeDo getOneExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 列表查询ExportCbuVehiclePoGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    List<ExportCbuVehiclePoGuaranteeDo> listExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 分页查询ExportCbuVehiclePoGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    PageResult<ExportCbuVehiclePoGuaranteeDo> pageExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 删除ExportCbuVehiclePoGuarantee
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportCbuVehiclePoGuaranteeById(String id);

    /**
     * 批量删除ExportCbuVehiclePoGuarantee
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportCbuVehiclePoGuaranteeByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportCbuVehiclePoGuarantee
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportCbuVehiclePoGuaranteeByIds(LpBaseParam param);
}
