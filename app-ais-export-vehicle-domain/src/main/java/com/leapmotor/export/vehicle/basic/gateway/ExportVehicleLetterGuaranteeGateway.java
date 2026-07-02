package com.leapmotor.export.vehicle.basic.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import java.util.Collection;
import java.util.List;

/**
 * 保函主表领域网关
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
public interface ExportVehicleLetterGuaranteeGateway  {

    /**
     * 新增ExportVehicleLetterGuarantee
     *
     * @param exportVehicleLetterGuaranteeDo
     * @return 结果
     */
    String addExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo);

    /**
     * 新增ExportVehicleLetterGuarantee
     *
     * @param exportVehicleLetterGuaranteeDos
     * @return 结果
     */
    int addBatchExportVehicleLetterGuarantee(List<ExportVehicleLetterGuaranteeDo> exportVehicleLetterGuaranteeDos);

    /**
     * 修改ExportVehicleLetterGuarantee
     *
     * @param exportVehicleLetterGuaranteeDo
     * @return 结果
     */
    String updateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo);

    /**
     * 查询ExportVehicleLetterGuarantee
     *
     * @param id
     * @return 结果
     */
    ExportVehicleLetterGuaranteeDo getExportVehicleLetterGuaranteeById(String id);

    /**
     * 唯一查询ExportVehicleLetterGuarantee
     *
     * @param exportVehicleLetterGuaranteeQo
     * @return 结果
     */
    ExportVehicleLetterGuaranteeDo getOneExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo);

    /**
     * 列表查询ExportVehicleLetterGuarantee
     *
     * @param exportVehicleLetterGuaranteeQo
     * @return 结果
     */
    List<ExportVehicleLetterGuaranteeDo> listExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo);

    /**
     * 分页查询ExportVehicleLetterGuarantee
     *
     * @param exportVehicleLetterGuaranteeQo
     * @return 结果
     */
    PageResult<ExportVehicleLetterGuaranteeDo> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo);

    /**
     * 删除ExportVehicleLetterGuarantee
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportVehicleLetterGuaranteeById(String id);

    /**
     * 批量删除ExportVehicleLetterGuarantee
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleLetterGuaranteeByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportVehicleLetterGuarantee
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param);
}
