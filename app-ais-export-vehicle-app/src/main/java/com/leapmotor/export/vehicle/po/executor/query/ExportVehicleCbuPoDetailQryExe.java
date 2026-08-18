package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoDetailDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoDetailParam;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoDetailQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleCbuPoDetail查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportVehicleCbuPoDetailQryExe {

    private ExportVehicleCbuPoDetailGateway purchaseOrderDetailGateway;


    public PageResult<ExportVehicleCbuPoDetailDo> pageExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailParam purchaseOrderDetailParam) {
        ExportVehicleCbuPoDetailQo purchaseOrderDetailQo = ExportVehicleCbuPoDetailDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderDetailParam);
        return purchaseOrderDetailGateway.pageExportVehicleCbuPoDetail(purchaseOrderDetailQo);
    }
}
