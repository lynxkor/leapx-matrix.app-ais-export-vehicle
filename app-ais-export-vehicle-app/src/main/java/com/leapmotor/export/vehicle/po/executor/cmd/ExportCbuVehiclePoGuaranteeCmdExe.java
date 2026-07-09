package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoGuaranteeDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportCbuVehiclePoGuarantee 写操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportCbuVehiclePoGuaranteeCmdExe {

    private ExportCbuVehiclePoGuaranteeGateway purchaseOrderGuaranteeGateway;


    public String addOrUpdateExportCbuVehiclePoGuarantee(ExportCbuVehiclePoGuaranteeDo purchaseOrderGuaranteeDo) {
        return purchaseOrderGuaranteeDo.getId() == null
                    ? purchaseOrderGuaranteeGateway.addExportCbuVehiclePoGuarantee(purchaseOrderGuaranteeDo)
                    : purchaseOrderGuaranteeGateway.updateExportCbuVehiclePoGuarantee(purchaseOrderGuaranteeDo);
    }
    public boolean deleteExportCbuVehiclePoGuaranteeByIds(List<String> ids) {
        return purchaseOrderGuaranteeGateway.deleteExportCbuVehiclePoGuaranteeByIds(ids);
    }
    public boolean enableOrDisableExportCbuVehiclePoGuaranteeByIds(LpBaseParam param) {
        return purchaseOrderGuaranteeGateway.enableOrDisableExportCbuVehiclePoGuaranteeByIds(param);
    }
}
