package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.resp.ExportVehicleCbuPoDetailResp;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * PO订单明细表 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoDetailService {

    /**
     * 查询ExportVehicleCbuPoDetail
     *
     * @param id
     * @return ExportVehicleCbuPoDetail
     */
    ExportVehicleCbuPoDetailResp getExportVehicleCbuPoDetailById(String id);

    /**
     * 查询ExportVehicleCbuPoDetail列表
     *
     * @param purchaseOrderDetailParam
     * @return ExportVehicleCbuPoDetail集合
     */
    List<ExportVehicleCbuPoDetailResp> listExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailParam purchaseOrderDetailParam);

    /**
     * 分页查询ExportVehicleCbuPoDetail列表
     *
     * @param purchaseOrderDetailParam
     * @return ExportVehicleCbuPoDetail集合
     */
    PageResult<ExportVehicleCbuPoDetailResp> pageExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailParam purchaseOrderDetailParam);

    /**
     * 新增/编辑ExportVehicleCbuPoDetail
     *
     * @param purchaseOrderDetailAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq);


    /**
     * 删除ExportVehicleCbuPoDetail信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleCbuPoDetailByIds(List<String> ids);


    /**
     * 启用/禁用ExportVehicleCbuPoDetail信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleCbuPoDetailByIds(LpBaseParam param);

}
