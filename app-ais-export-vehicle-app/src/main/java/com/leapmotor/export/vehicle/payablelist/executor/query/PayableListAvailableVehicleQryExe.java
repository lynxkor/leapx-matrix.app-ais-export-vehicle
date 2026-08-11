package com.leapmotor.export.vehicle.payablelist.executor.query;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListQueryAvailableVehiclesReq;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 查询可添加车辆执行器
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListAvailableVehicleQryExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    /**
     * 执行查询可添加车辆
     *
     * @param req 查询请求
     * @return 可添加车辆列表
     */
    public List<SoOrderForPayableDto> execute(PayableListQueryAvailableVehiclesReq req) {
        // 查询应付清单
        PayableListDo payableList = payableListGateway.getPayableListById(req.getPayableListId());
        if (payableList == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }

        // 查询质检通过的车辆（复用月度生成的查询逻辑）
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setQcResult(VehicleCbuSoQcResultEnum.PASSED.getCode());
        qo.setCustomerCode(payableList.getCustomerCode());
        qo.setVinLike(req.getVin());
        qo.setSoNoLike(req.getSoNo());
        List<VehicleCbuSoDo> qcPassedList = vehicleCbuSoGateway.listVehicleCbuSo(qo);

        if (qcPassedList.isEmpty()) {
            log.info("查询可添加车辆：无符合条件的车辆，payableListId={}", req.getPayableListId());
            return List.of();
        }

        // 查询所有已绑定到应付清单的SO订单ID
        List<String> boundSoIds = payableListDetailGateway.listAllBoundSoIds();
        Set<String> boundSoIdSet = new HashSet<>(boundSoIds);

        // 过滤出未绑定的车辆
        List<SoOrderForPayableDto> availableVehicles = qcPassedList.stream()
                .filter(so -> !boundSoIdSet.contains(so.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());

        log.info("查询可添加车辆成功，payableListId={}, customerCode={}, total={}, available={}",
                req.getPayableListId(), payableList.getCustomerCode(), qcPassedList.size(), availableVehicles.size());

        return availableVehicles;
    }

    /**
     * 将 VehicleCbuSoDo 转换为 SoOrderForPayableDto
     *
     * @param soDo SO 领域对象
     * @return DTO
     */
    private SoOrderForPayableDto convertToDto(VehicleCbuSoDo soDo) {
        SoOrderForPayableDto dto = new SoOrderForPayableDto();
        dto.setSoId(soDo.getId());
        dto.setSoNo(soDo.getSoNo());
        dto.setPoNo(soDo.getPoNo());
        dto.setVin(soDo.getVin());
        dto.setMaterialCode(soDo.getMaterialCode());
        dto.setMaterialDesc(soDo.getMaterialDescription());
        dto.setCustomerCode(soDo.getCustomerCode());
        dto.setCustomerName(soDo.getCustomerName());
        dto.setVehiclePrice(soDo.getVehiclePrice());
        dto.setFrozenAmount(soDo.getFrozenAmount());
        dto.setReceivableAmount(soDo.getReceivableAmount());
        dto.setQcResultTime(soDo.getQcResultTime());
        dto.setPoCreateTime(soDo.getReceiveOrderTime());
        dto.setSaleMarket(soDo.getSalesMarket());
        dto.setModelYear(soDo.getModelYear());
        dto.setOptionPackage(soDo.getOptionalPackage());
        return dto;
    }
}
