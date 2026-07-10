package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDo;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoParam;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportCbuVehiclePo查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportCbuVehiclePoQryExe {

    private ExportCbuVehiclePoGateway purchaseOrderGateway;


    public PageResult<ExportCbuVehiclePoDo> pageExportCbuVehiclePo(ExportCbuVehiclePoParam purchaseOrderParam) {
        ExportCbuVehiclePoQo purchaseOrderQo = ExportCbuVehiclePoDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderParam);
        return purchaseOrderGateway.pageExportCbuVehiclePo(purchaseOrderQo);
    }
}
