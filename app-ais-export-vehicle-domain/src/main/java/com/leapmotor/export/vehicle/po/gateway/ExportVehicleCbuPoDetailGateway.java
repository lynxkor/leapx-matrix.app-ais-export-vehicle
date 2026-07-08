package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * PO订单明细表领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoDetailGateway  {

    /**
     * 新增ExportVehicleCbuPoDetail
     *
     * @param purchaseOrderDetailDo
     * @return 结果
     */
    String addExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailDo purchaseOrderDetailDo);

    /**
     * 新增ExportVehicleCbuPoDetail
     *
     * @param purchaseOrderDetailDos
     * @return 结果
     */
    int addBatchExportVehicleCbuPoDetail(List<ExportVehicleCbuPoDetailDo> purchaseOrderDetailDos);

    /**
     * 修改ExportVehicleCbuPoDetail
     *
     * @param purchaseOrderDetailDo
     * @return 结果
     */
    String updateExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailDo purchaseOrderDetailDo);

    /**
     * 按主键更新明细单价/金额（显式 set，可写入 null 值；用于刷新价格时未匹配行留空）
     *
     * @param id 明细主键
     * @param unitPrice 单价（可为 null）
     * @param totalAmount 金额合计（可为 null）
     * @return 受影响行数
     */
    int updatePriceById(String id, BigDecimal unitPrice, BigDecimal totalAmount);

    /**
     * 查询ExportVehicleCbuPoDetail
     *
     * @param id
     * @return 结果
     */
    ExportVehicleCbuPoDetailDo getExportVehicleCbuPoDetailById(String id);

    /**
     * 唯一查询ExportVehicleCbuPoDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    ExportVehicleCbuPoDetailDo getOneExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailQo purchaseOrderDetailQo);

    /**
     * 列表查询ExportVehicleCbuPoDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    List<ExportVehicleCbuPoDetailDo> listExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailQo purchaseOrderDetailQo);

    /**
     * 分页查询ExportVehicleCbuPoDetail
     *
     * @param purchaseOrderDetailQo
     * @return 结果
     */
    PageResult<ExportVehicleCbuPoDetailDo> pageExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailQo purchaseOrderDetailQo);

    /**
     * 删除ExportVehicleCbuPoDetail
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportVehicleCbuPoDetailById(String id);

    /**
     * 批量删除ExportVehicleCbuPoDetail
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleCbuPoDetailByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportVehicleCbuPoDetail
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleCbuPoDetailByIds(LpBaseParam param);
}
