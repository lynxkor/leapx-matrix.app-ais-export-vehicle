package com.leapmotor.export.vehicle.po.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailParam;
import com.leapmotor.export.vehicle.po.resp.ExportCbuVehiclePoDetailResp;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * PO订单明细表 服务类
 * </p>
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportCbuVehiclePoDetailService {

    /**
     * 查询ExportCbuVehiclePoDetail
     *
     * @param id
     * @return ExportCbuVehiclePoDetail
     */
    ExportCbuVehiclePoDetailResp getExportCbuVehiclePoDetailById(String id);

    /**
     * 查询ExportCbuVehiclePoDetail列表
     *
     * @param purchaseOrderDetailParam
     * @return ExportCbuVehiclePoDetail集合
     */
    List<ExportCbuVehiclePoDetailResp> listExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailParam purchaseOrderDetailParam);

    /**
     * 分页查询ExportCbuVehiclePoDetail列表
     *
     * @param purchaseOrderDetailParam
     * @return ExportCbuVehiclePoDetail集合
     */
    PageResult<ExportCbuVehiclePoDetailResp> pageExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailParam purchaseOrderDetailParam);

    /**
     * 新增/编辑ExportCbuVehiclePoDetail
     *
     * @param purchaseOrderDetailAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailAddOrUpdateReq purchaseOrderDetailAddOrUpdateReq);


    /**
     * 删除ExportCbuVehiclePoDetail信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportCbuVehiclePoDetailByIds(List<String> ids);


    /**
     * 启用/禁用ExportCbuVehiclePoDetail信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportCbuVehiclePoDetailByIds(LpBaseParam param);

}
