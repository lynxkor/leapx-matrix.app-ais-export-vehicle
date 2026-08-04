package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoGuaranteeResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * PO-保函关联表(一个PO多保函) 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoGuaranteeService {

    /**
     * 查询ExportVehicleCbuPoGuarantee
     *
     * @param id
     * @return ExportVehicleCbuPoGuarantee
     */
    ExportVehicleCbuPoGuaranteeResp getExportVehicleCbuPoGuaranteeById(String id);

    /**
     * 查询ExportVehicleCbuPoGuarantee列表
     *
     * @param purchaseOrderGuaranteeParam
     * @return ExportVehicleCbuPoGuarantee集合
     */
    List<ExportVehicleCbuPoGuaranteeResp> listExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeParam purchaseOrderGuaranteeParam);

    /**
     * 分页查询ExportVehicleCbuPoGuarantee列表
     *
     * @param purchaseOrderGuaranteeParam
     * @return ExportVehicleCbuPoGuarantee集合
     */
    PageResult<ExportVehicleCbuPoGuaranteeResp> pageExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeParam purchaseOrderGuaranteeParam);

    /**
     * 新增/编辑ExportVehicleCbuPoGuarantee
     *
     * @param purchaseOrderGuaranteeAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeAddOrUpdateReq purchaseOrderGuaranteeAddOrUpdateReq);


    /**
     * 删除ExportVehicleCbuPoGuarantee信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleCbuPoGuaranteeByIds(List<String> ids);


    /**
     * 启用/禁用ExportVehicleCbuPoGuarantee信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleCbuPoGuaranteeByIds(LpBaseParam param);

}
