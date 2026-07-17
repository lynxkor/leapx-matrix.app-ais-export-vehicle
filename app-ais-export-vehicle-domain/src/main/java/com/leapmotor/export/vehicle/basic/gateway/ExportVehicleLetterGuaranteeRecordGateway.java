package com.leapmotor.export.vehicle.basic.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import java.util.Collection;
import java.util.List;

/**
 * 保函交易明细领域网关
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
public interface ExportVehicleLetterGuaranteeRecordGateway  {

    /**
     * 新增ExportVehicleLetterGuaranteeRecord
     *
     * @param exportVehicleLetterGuaranteeRecordDo
     * @return 结果
     */
    String addExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo);

    /**
     * 新增ExportVehicleLetterGuaranteeRecord
     *
     * @param exportVehicleLetterGuaranteeRecordDos
     * @return 结果
     */
    int addBatchExportVehicleLetterGuaranteeRecord(List<ExportVehicleLetterGuaranteeRecordDo> exportVehicleLetterGuaranteeRecordDos);

    /**
     * 修改ExportVehicleLetterGuaranteeRecord
     *
     * @param exportVehicleLetterGuaranteeRecordDo
     * @return 结果
     */
    String updateExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo);

    /**
     * 查询ExportVehicleLetterGuaranteeRecord
     *
     * @param id
     * @return 结果
     */
    ExportVehicleLetterGuaranteeRecordDo getExportVehicleLetterGuaranteeRecordById(String id);

    /**
     * 唯一查询ExportVehicleLetterGuaranteeRecord
     *
     * @param exportVehicleLetterGuaranteeRecordQo
     * @return 结果
     */
    ExportVehicleLetterGuaranteeRecordDo getOneExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo);

    /**
     * 列表查询ExportVehicleLetterGuaranteeRecord
     *
     * @param exportVehicleLetterGuaranteeRecordQo
     * @return 结果
     */
    List<ExportVehicleLetterGuaranteeRecordDo> listExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo);

    /**
     * 分页查询ExportVehicleLetterGuaranteeRecord
     *
     * @param exportVehicleLetterGuaranteeRecordQo
     * @return 结果
     */
    PageResult<ExportVehicleLetterGuaranteeRecordDo> pageExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo);

    /**
     * 删除ExportVehicleLetterGuaranteeRecord
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportVehicleLetterGuaranteeRecordById(String id);

    /**
     * 批量删除ExportVehicleLetterGuaranteeRecord
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleLetterGuaranteeRecordByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportVehicleLetterGuaranteeRecord
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleLetterGuaranteeRecordByIds(LpBaseParam param);
}
