package com.leapmotor.export.vehicle.basic.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeResp;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * 保函主表 服务类
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
public interface ExportVehicleLetterGuaranteeService {

    /**
     * 查询ExportVehicleLetterGuarantee
     *
     * @param id
     * @return ExportVehicleLetterGuarantee
     */
    ExportVehicleLetterGuaranteeResp getExportVehicleLetterGuaranteeById(String id);

    /**
     * 查询ExportVehicleLetterGuarantee列表
     *
     * @param exportVehicleLetterGuaranteeParam
     * @return ExportVehicleLetterGuarantee集合
     */
    List<ExportVehicleLetterGuaranteeResp> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam);

    /**
     * 分页查询ExportVehicleLetterGuarantee列表
     *
     * @param exportVehicleLetterGuaranteeParam
     * @return ExportVehicleLetterGuarantee集合
     */
    PageResult<ExportVehicleLetterGuaranteeResp> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam);

    /**
     * 新增/编辑ExportVehicleLetterGuarantee
     *
     * @param exportVehicleLetterGuaranteeAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeAddOrUpdateReq exportVehicleLetterGuaranteeAddOrUpdateReq);


    /**
     * 删除ExportVehicleLetterGuarantee信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleLetterGuaranteeByIds(List<String> ids);


    /**
     * 启用/禁用ExportVehicleLetterGuarantee信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param);

}
