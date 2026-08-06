package com.leapmotor.export.vehicle.payablelist.executor.query;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.gateway.SoOrderGateway;
import com.leapmotor.export.vehicle.payablelist.resp.PayableListAvailableVehicleResp;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询可添加车辆执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Component
@AllArgsConstructor
public class PayableListAvailableVehicleQryExe {

    private SoOrderGateway soOrderGateway;

    public List<PayableListAvailableVehicleResp> execute() {
        List<SoOrderForPayableDto> soList = soOrderGateway.queryQcPassedAndUnbound();
        return soList.stream().map(this::toResp).collect(Collectors.toList());
    }

    private PayableListAvailableVehicleResp toResp(SoOrderForPayableDto dto) {
        PayableListAvailableVehicleResp resp = new PayableListAvailableVehicleResp();
        resp.setSoId(dto.getSoId());
        resp.setSoNo(dto.getSoNo());
        resp.setPoNo(dto.getPoNo());
        resp.setVin(dto.getVin());
        resp.setMaterialCode(dto.getMaterialCode());
        resp.setMaterialDesc(dto.getMaterialDesc());
        resp.setVehiclePrice(dto.getVehiclePrice());
        resp.setQcPassTime(dto.getQcResultTime());
        return resp;
    }
}
