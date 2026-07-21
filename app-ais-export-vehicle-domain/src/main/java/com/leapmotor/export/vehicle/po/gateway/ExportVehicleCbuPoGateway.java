package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * PO订单主表领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoGateway  {

    /**
     * 新增ExportVehicleCbuPo
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String addExportVehicleCbuPo(ExportVehicleCbuPoDo purchaseOrderDo);

    /**
     * 新增ExportVehicleCbuPo
     *
     * @param purchaseOrderDos
     * @return 结果
     */
    int addBatchExportVehicleCbuPo(List<ExportVehicleCbuPoDo> purchaseOrderDos);

    /**
     * 修改ExportVehicleCbuPo
     *
     * @param purchaseOrderDo
     * @return 结果
     */
    String updateExportVehicleCbuPo(ExportVehicleCbuPoDo purchaseOrderDo);

    /**
     * 查询ExportVehicleCbuPo
     *
     * @param id
     * @return 结果
     */
    ExportVehicleCbuPoDo getExportVehicleCbuPoById(String id);

    /**
     * 唯一查询ExportVehicleCbuPo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    ExportVehicleCbuPoDo getOneExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo);

    /**
     * 列表查询ExportVehicleCbuPo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    List<ExportVehicleCbuPoDo> listExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo);

    /**
     * 分页查询ExportVehicleCbuPo
     *
     * @param purchaseOrderQo
     * @return 结果
     */
    PageResult<ExportVehicleCbuPoDo> pageExportVehicleCbuPo(ExportVehicleCbuPoQo purchaseOrderQo);

    /**
     * 删除ExportVehicleCbuPo
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportVehicleCbuPoById(String id);

    /**
     * 批量删除ExportVehicleCbuPo
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleCbuPoByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportVehicleCbuPo
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleCbuPoByIds(LpBaseParam param);

    /**
     * 更新审批结果同步信息(推红旗审批结果后回写:同步时间/结果/对方返回).
     * 独立事务,推送失败重试时该记录不被外层回滚带走.
     *
     * @param poId       PO主键ID(event bizNo)
     * @param syncTime   同步时间
     * @param syncResult 同步结果(成功/失败)
     * @param returnInfo 客户系统返回信息(红旗返回 JSON)
     * @return 影响行数
     */
    int updateApprovalSyncResult(String poId, LocalDateTime syncTime, String syncResult, String returnInfo);
}
