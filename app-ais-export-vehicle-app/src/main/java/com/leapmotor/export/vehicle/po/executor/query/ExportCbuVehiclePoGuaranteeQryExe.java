package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.ExportCbuVehiclePoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoGuaranteeDo;
import com.leapmotor.export.vehicle.po.req.ExportCbuVehiclePoGuaranteeParam;
import com.leapmotor.export.vehicle.po.query.ExportCbuVehiclePoGuaranteeQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportCbuVehiclePoGuarantee查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportCbuVehiclePoGuaranteeQryExe {

    private ExportCbuVehiclePoGuaranteeGateway purchaseOrderGuaranteeGateway;


    public PageResult<ExportCbuVehiclePoGuaranteeDo> pageExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeParam purchaseOrderGuaranteeParam) {
        ExportCbuVehiclePoGuaranteeQo purchaseOrderGuaranteeQo = ExportCbuVehiclePoGuaranteeDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderGuaranteeParam);
        return purchaseOrderGuaranteeGateway.pageExportCbuVehiclePoGuarantee(purchaseOrderGuaranteeQo);
    }
}
