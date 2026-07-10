package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeParam;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * PO-保函关联表(一个PO多保函) 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportCbuVehiclePoGuaranteeService {

    /**
     * 查询ExportCbuVehiclePoGuarantee
     *
     * @param id
     * @return ExportCbuVehiclePoGuarantee
     */
    ExportCbuVehiclePoGuaranteeResp getExportCbuVehiclePoGuaranteeById(String id);

    /**
     * 查询ExportCbuVehiclePoGuarantee列表
     *
     * @param purchaseOrderGuaranteeParam
     * @return ExportCbuVehiclePoGuarantee集合
     */
    List<ExportCbuVehiclePoGuaranteeResp> listExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeParam purchaseOrderGuaranteeParam);

    /**
     * 分页查询ExportCbuVehiclePoGuarantee列表
     *
     * @param purchaseOrderGuaranteeParam
     * @return ExportCbuVehiclePoGuarantee集合
     */
    PageResult<ExportCbuVehiclePoGuaranteeResp> pageExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeParam purchaseOrderGuaranteeParam);

    /**
     * 新增/编辑ExportCbuVehiclePoGuarantee
     *
     * @param purchaseOrderGuaranteeAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq);


    /**
     * 删除ExportCbuVehiclePoGuarantee信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportCbuVehiclePoGuaranteeByIds(List<String> ids);


    /**
     * 启用/禁用ExportCbuVehiclePoGuarantee信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportCbuVehiclePoGuaranteeByIds(LpBaseParam param);

}
