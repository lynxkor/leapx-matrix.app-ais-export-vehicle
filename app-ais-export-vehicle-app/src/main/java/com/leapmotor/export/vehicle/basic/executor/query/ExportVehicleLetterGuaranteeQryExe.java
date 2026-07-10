package com.leapmotor.export.vehicle.basic.executor.query;

import com.leapmotor.export.vehicle.basic.convertor.ExportVehicleLetterGuaranteeDTOStructMapper;
import com.leapmotor.export.vehicle.basic.gateway.ExportVehicleLetterGuaranteeGateway;
import com.leapmotor.export.vehicle.basic.model.ExportVehicleLetterGuaranteeDo;
import com.leapmotor.export.vehicle.basic.query.ExportVehicleLetterGuaranteeQo;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeListAvailableReq;
import com.leapmotor.export.vehicle.basic.req.ExportVehicleLetterGuaranteeParam;
import com.leapmotor.paltform.ais.common.model.response.PageResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ExportVehicleLetterGuarantee 查询执行器
 *
 * @author wangkai2527148
 * @since 2026-07-02
 */
@AllArgsConstructor
@Component
public class ExportVehicleLetterGuaranteeQryExe {

    private ExportVehicleLetterGuaranteeGateway exportVehicleLetterGuaranteeGateway;

    public PageResult<ExportVehicleLetterGuaranteeDo> pageExportVehicleLetterGuarantee(ExportVehicleLetterGuaranteeParam exportVehicleLetterGuaranteeParam) {
        ExportVehicleLetterGuaranteeQo exportVehicleLetterGuaranteeQo =
                ExportVehicleLetterGuaranteeDTOStructMapper.INSTANCE.paramToQuery(exportVehicleLetterGuaranteeParam);
        return exportVehicleLetterGuaranteeGateway.pageExportVehicleLetterGuarantee(exportVehicleLetterGuaranteeQo);
    }

    /**
     * 列出客户可用保函(资金校验阶段)
     * <p>Gateway 按 a/b/c/d/e 5 条规则在 SQL 层过滤;f(SUM(availableAmount) ≥ requiredAmount)由调用方(ServiceImpl.validateForPo)累加判断。
     */
    public List<ExportVehicleLetterGuaranteeDo> listAvailableForCustomer(ExportVehicleLetterGuaranteeListAvailableReq req) {
        List<ExportVehicleLetterGuaranteeDo> dos = exportVehicleLetterGuaranteeGateway.listAvailableForCustomer(req.getCustomerId(), req.getOrderType(), req.getOrderCreateTime());
        if (dos == null || dos.isEmpty()) {
            return List.of();
        }
        return dos;
    }
}
