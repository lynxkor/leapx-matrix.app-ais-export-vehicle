package com.leapmotor.export.vehicle.basic.executor.query;

import com.leapmotor.paltform.ais.common.model.response.PageResult;
import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import org.springframework.stereotype.Component;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 *
 * ExportVehicleLetterGuarantee查询操作执行器
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Component
public class ExportVehicleLetterGuaranteeQryExe {

    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;


    public PageResult<ExportVehicleLetterGuaranteeDo> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam) {
        ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo = ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.paramToQuery(exportVehicleLetterGuaranteeParam);
        return exportVehicleLetterGuaranteeGateway.pageExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeQo);
    }
}
