package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.ExportCbuVehiclePoDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportCbuVehiclePoDetailDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportCbuVehiclePoDetail 写操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportCbuVehiclePoDetailCmdExe {

    private ExportCbuVehiclePoDetailGateway purchaseOrderDetailGateway;


    public String addOrUpdateExportCbuVehiclePoDetail(ExportCbuVehiclePoDetailDo purchaseOrderDetailDo) {
        return purchaseOrderDetailDo.getId() == null
                    ? purchaseOrderDetailGateway.addExportCbuVehiclePoDetail(purchaseOrderDetailDo)
                    : purchaseOrderDetailGateway.updateExportCbuVehiclePoDetail(purchaseOrderDetailDo);
    }
    public boolean deleteExportCbuVehiclePoDetailByIds(List<String> ids) {
        return purchaseOrderDetailGateway.deleteExportCbuVehiclePoDetailByIds(ids);
    }
    public boolean enableOrDisableExportCbuVehiclePoDetailByIds(LpBaseParam param) {
        return purchaseOrderDetailGateway.enableOrDisableExportCbuVehiclePoDetailByIds(param);
    }
}
