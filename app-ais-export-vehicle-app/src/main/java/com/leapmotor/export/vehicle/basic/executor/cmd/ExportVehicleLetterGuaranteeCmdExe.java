package com.leapmotor.export.vehicle.basic.executor.cmd;


import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleLetterGuarantee 写操作执行器
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Component
public class ExportVehicleLetterGuaranteeCmdExe {

    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;


    public String addOrUpdateExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeDo exportVehicleLetterGuaranteeDo) {
        return exportVehicleLetterGuaranteeDo.getId() == null
                    ? exportVehicleLetterGuaranteeGateway.addExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeDo)
                    : exportVehicleLetterGuaranteeGateway.updateExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeDo);
    }
    public boolean deleteExportVehicleLetterGuaranteeByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeGateway.deleteExportVehicleLetterGuaranteeByIds(ids);
    }
    public boolean enableOrDisableExportVehicleLetterGuaranteeByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeGateway.enableOrDisableExportVehicleLetterGuaranteeByIds(param);
    }
}
