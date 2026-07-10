package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDo;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoParam;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleCbuPo查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportVehicleCbuPoQryExe {

    private ExportVehicleCbuPoGateway purchaseOrderGateway;


    public PageResult<ExportVehicleCbuPoDo> pageExportVehicleCbuPo(ExportVehicleCbuPoParam purchaseOrderParam) {
        ExportVehicleCbuPoQo purchaseOrderQo = ExportVehicleCbuPoDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderParam);
        return purchaseOrderGateway.pageExportVehicleCbuPo(purchaseOrderQo);
    }
}
