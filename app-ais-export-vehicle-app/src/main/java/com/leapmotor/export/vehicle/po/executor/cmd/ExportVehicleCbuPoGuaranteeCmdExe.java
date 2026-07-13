package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoGuaranteeDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleCbuPoGuarantee 写操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportVehicleCbuPoGuaranteeCmdExe {

    private ExportVehicleCbuPoGuaranteeGateway purchaseOrderGuaranteeGateway;


    public String addOrUpdateExportVehicleCbuPoGuarantee(ExportVehicleCbuPoGuaranteeDo purchaseOrderGuaranteeDo) {
        return purchaseOrderGuaranteeDo.getId() == null
                    ? purchaseOrderGuaranteeGateway.addExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeDo)
                    : purchaseOrderGuaranteeGateway.updateExportVehicleCbuPoGuarantee(purchaseOrderGuaranteeDo);
    }
    public boolean deleteExportVehicleCbuPoGuaranteeByIds(List<String> ids) {
        return purchaseOrderGuaranteeGateway.deleteExportVehicleCbuPoGuaranteeByIds(ids);
    }
    public boolean enableOrDisableExportVehicleCbuPoGuaranteeByIds(LpBaseParam param) {
        return purchaseOrderGuaranteeGateway.enableOrDisableExportVehicleCbuPoGuaranteeByIds(param);
    }
}
