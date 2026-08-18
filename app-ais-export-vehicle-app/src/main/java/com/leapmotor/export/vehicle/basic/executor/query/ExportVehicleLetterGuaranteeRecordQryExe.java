package com.leapmotor.export.vehicle.basic.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeRecordDTOStructMapper;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeRecordGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeRecordDo;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeRecordParam;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeRecordQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleLetterGuaranteeRecord查询操作执行器
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Component
public class ExportVehicleLetterGuaranteeRecordQryExe {

    private ExportVehicleLetterGuaranteeRecordGateway exportVehicleLetterGuaranteeRecordGateway;


    public PageResult<ExportVehicleLetterGuaranteeRecordDo> pageExportVehicleLetterGuaranteeRecord(ExportVehicleLetterGuaranteeRecordParam exportVehicleLetterGuaranteeRecordParam) {
        ExportVehicleLetterGuaranteeRecordQo exportVehicleLetterGuaranteeRecordQo = ExportVehicleLetterGuaranteeRecordDTOStructMapper.INSTANCE.paramToQuery(exportVehicleLetterGuaranteeRecordParam);
        return exportVehicleLetterGuaranteeRecordGateway.pageExportVehicleLetterGuaranteeRecord(exportVehicleLetterGuaranteeRecordQo);
    }
}
