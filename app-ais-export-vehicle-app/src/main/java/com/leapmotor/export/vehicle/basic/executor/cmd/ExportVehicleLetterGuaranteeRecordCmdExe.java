package com.leapmotor.export.vehicle.basic.executor.cmd;


import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.paltform.ais.common.model.base.LpBaseParam;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleLetterGuaranteeRecord 写操作执行器
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Component
public class ExportVehicleLetterGuaranteeRecordCmdExe {

    private ExportVehicleLetterGuaranteeRecordGateway exportVehicleLetterGuaranteeRecordGateway;


    public String addOrUpdateExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordDo exportVehicleLetterGuaranteeRecordDo) {
        return exportVehicleLetterGuaranteeRecordDo.getId() == null
                    ? exportVehicleLetterGuaranteeRecordGateway.addExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordDo)
                    : exportVehicleLetterGuaranteeRecordGateway.updateExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordDo);
    }
    public boolean deleteExportVehicleLetterGuaranteeRecordByIds(List<String> ids) {
        return exportVehicleLetterGuaranteeRecordGateway.deleteExportVehicleLetterGuaranteeRecordByIds(ids);
    }
    public boolean enableOrDisableExportVehicleLetterGuaranteeRecordByIds(LpBaseParam param) {
        return exportVehicleLetterGuaranteeRecordGateway.enableOrDisableExportVehicleLetterGuaranteeRecordByIds(param);
    }
}
