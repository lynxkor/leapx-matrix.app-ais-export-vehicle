package com.leapmotor.export.vehicle.po.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.po.convertor.ExportVehicleCbuPoGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import com.leapmotor.export.vehicle.po.req.ExportVehicleCbuPoGuaranteeParam;
import com.leapmotor.export.vehicle.po.query.ExportVehicleCbuPoGuaranteeQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleCbuPoGuarantee查询操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportVehicleCbuPoGuaranteeQryExe {

    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;


    public PageResult<ExportVehicleCbuPoGuaranteeDo> pageExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeParam purchaseOrderGuaranteeParam) {
        ExportVehicleCbuPoGuaranteeQo purchaseOrderGuaranteeQo = ExportVehicleCbuPoGuaranteeDTOStructMapper.INSTANCE.paramToQuery(purchaseOrderGuaranteeParam);
        return purchaseOrderGuaranteeGateway.pageExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeQo);
    }
}
