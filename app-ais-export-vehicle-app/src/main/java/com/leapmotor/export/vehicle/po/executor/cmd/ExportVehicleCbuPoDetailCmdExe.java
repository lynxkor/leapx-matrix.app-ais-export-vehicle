package com.leapmotor.export.vehicle.po.executor.cmd;


import com.leapmotor.export.vehicle.po.gateway.ExportVehicleCbuPoDetailGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.po.model.ExportVehicleCbuPoDetailDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleCbuPoDetail 写操作执行器
 * @author leapmotor
 * @since 2026-07-08
 */
@AllArgsConstructor
@Component
public class ExportVehicleCbuPoDetailCmdExe {

    private ExportVehicleCbuPoDetailGateway purchaseOrderDetailGateway;


    public String addOrUpdateExportVehicleCbuPoDetail(ExportVehicleCbuPoDetailDo purchaseOrderDetailDo) {
        return purchaseOrderDetailDo.getId() == null
                    ? purchaseOrderDetailGateway.addExportVehicleCbuPoDetail(purchaseOrderDetailDo)
                    : purchaseOrderDetailGateway.updateExportVehicleCbuPoDetail(purchaseOrderDetailDo);
    }
    public boolean deleteExportVehicleCbuPoDetailByIds(List<String> ids) {
        return purchaseOrderDetailGateway.deleteExportVehicleCbuPoDetailByIds(ids);
    }
    public boolean enableOrDisableExportVehicleCbuPoDetailByIds(LpBaseParam param) {
        return purchaseOrderDetailGateway.enableOrDisableExportVehicleCbuPoDetailByIds(param);
    }
}
