package com.leapmotor.export.vehicle.po.gateway;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * PO-保函关联表(一个PO多保函)领域网关
 *
 * @author leapmotor
 * @since 2026-07-08
 */
public interface ExportVehicleCbuPoGuaranteeGateway  {

    /**
     * 新增ExportVehicleCbuPoGuarantee
     *
     * @param purchaseOrderGuaranteeDo
     * @return 结果
     */
    String addExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeDo purchaseOrderGuaranteeDo);

    /**
     * 新增ExportVehicleCbuPoGuarantee
     *
     * @param purchaseOrderGuaranteeDos
     * @return 结果
     */
    int addBatchExportVehicleCbuPoGuarantee(List<ExportVehicleCbuPoGuaranteeDo> purchaseOrderGuaranteeDos);

    /**
     * 修改ExportVehicleCbuPoGuarantee
     *
     * @param purchaseOrderGuaranteeDo
     * @return 结果
     */
    String updateExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeDo purchaseOrderGuaranteeDo);

    /**
     * 查询ExportVehicleCbuPoGuarantee
     *
     * @param id
     * @return 结果
     */
    ExportVehicleCbuPoGuaranteeDo getExportVehicleCbuPoGuaranteeById(String id);

    /**
     * 唯一查询ExportVehicleCbuPoGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    ExportVehicleCbuPoGuaranteeDo getOneExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 列表查询ExportVehicleCbuPoGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    List<ExportVehicleCbuPoGuaranteeDo> listExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 分页查询ExportVehicleCbuPoGuarantee
     *
     * @param purchaseOrderGuaranteeQo
     * @return 结果
     */
    PageResult<ExportVehicleCbuPoGuaranteeDo> pageExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo);

    /**
     * 删除ExportVehicleCbuPoGuarantee
     *
     * @param id
     * @return 结果
     */
     boolean deleteExportVehicleCbuPoGuaranteeById(String id);

    /**
     * 批量删除ExportVehicleCbuPoGuarantee
     *
     * @param ids
     * @return 结果
     */
    boolean deleteExportVehicleCbuPoGuaranteeByIds(Collection<String> ids);

    /**
     * 启用/禁用ExportVehicleCbuPoGuarantee
     *
     * @param param
     * @return 结果
     */
    boolean enableOrDisableExportVehicleCbuPoGuaranteeByIds(LpBaseParam param);

    /**
     * 原子扣减 PO 关联保函当前应收金额：
     *
     * @param id     记录ID
     * @param amount 扣减金额（必须大于 0）
     * @return 是否扣减成功；false 表示记录不存在或当前应收金额不足
     */
    boolean reducePoReceivableAmount(String id, BigDecimal amount);
}
