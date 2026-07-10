package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoDetailParam;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoDetailQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportCbuVehiclePoDetail查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportCbuVehiclePoDetailQryExe {

    private ExportCbuVehiclePoDetailGateway purchaseOrderDetailGateway;


    public PageResult<ExportCbuVehiclePoDetailDo> pageExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailParam purchaseOrderDetailParam) {
        ExportCbuVehiclePoDetailQo purchaseOrderDetailQo = ExportCbuVehiclePoDetailDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderDetailParam);
        return purchaseOrderDetailGateway.pageExportCbuVehiclePoDetail(purchaseOrderDetailQo);
    }
}
