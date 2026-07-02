package com.leapmotor.export.vehicle.basic.service;


import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.resp.ExportVehicleLetterGuaranteeRecordResp;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordAddOrUpdateReq;

import java.util.List;

/**
 * <p>
 * 保函交易明细(追加写) 服务类
 * </p>
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
public interface ExportVehicleLetterGuaranteeRecordService {

    /**
     * 查询ExportVehicleLetterGuaranteeRecord
     *
     * @param id
     * @return ExportVehicleLetterGuaranteeRecord
     */
    ExportVehicleLetterGuaranteeRecordResp getExportVehicleLetterGuaranteeRecordById(String id);

    /**
     * 查询ExportVehicleLetterGuaranteeRecord列表
     *
     * @param exportVehicleLetterGuaranteeRecordParam
     * @return ExportVehicleLetterGuaranteeRecord集合
     */
    List<ExportVehicleLetterGuaranteeRecordResp> listExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam);

    /**
     * 分页查询ExportVehicleLetterGuaranteeRecord列表
     *
     * @param exportVehicleLetterGuaranteeRecordParam
     * @return ExportVehicleLetterGuaranteeRecord集合
     */
    PageResult<ExportVehicleLetterGuaranteeRecordResp> pageExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam);

    /**
     * 新增/编辑ExportVehicleLetterGuaranteeRecord
     *
     * @param exportVehicleLetterGuaranteeRecordAddOrUpdateReq
     * @return 结果
     */
    String addOrUpdateExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordAddOrUpdateReq exportVehicleLetterGuaranteeRecordAddOrUpdateReq);


    /**
     * 删除ExportVehicleLetterGuaranteeRecord信息
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleLetterGuaranteeRecordByIds(List<String> ids);


    /**
     * 启用/禁用ExportVehicleLetterGuaranteeRecord信息
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleLetterGuaranteeRecordByIds(LpBaseParam param);

}
