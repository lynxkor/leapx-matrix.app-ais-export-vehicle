package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 月度应付清单生成执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListGenerateExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    @Transactional(rollbackFor = Exception.class)
    public void generateMonthly() {
        // 计算 generateMonth = 上一个月
        String generateMonth = LocalDateTime.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 查询质检通过的车辆
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setQcResult(VehicleCbuSoQcResultEnum.PASSED.getCode());
        List<VehicleCbuSoDo> qcPassedList = vehicleCbuSoGateway.listVehicleCbuSo(qo);

        if (qcPassedList.isEmpty()) {
            log.info("月度生成：无质检通过的车辆，任务结束");
            return;
        }

        // 查询已绑定的 SO 订单 ID 列表
        List<String> boundSoIds = payableListDetailGateway.listAllBoundSoIds();
        Set<String> boundSoIdSet = new HashSet<>(boundSoIds);

        // 过滤出未绑定的车辆
        List<VehicleCbuSoDo> unboundList = qcPassedList.stream()
                .filter(so -> !boundSoIdSet.contains(so.getId()))
                .toList();

        if (unboundList.isEmpty()) {
            log.info("月度生成：质检通过的车辆均已绑定，任务结束");
            return;
        }

        // 转换为 DTO
        List<SoOrderForPayableDto> soList = unboundList.stream()
                .map(this::convertToDto)
                .toList();

        // 按客户编码分组
        Map<String, List<SoOrderForPayableDto>> customerGroups = soList.stream()
                .collect(Collectors.groupingBy(SoOrderForPayableDto::getCustomerCode));

        for (Map.Entry<String, List<SoOrderForPayableDto>> entry : customerGroups.entrySet()) {
            String customerCode = entry.getKey();
            List<SoOrderForPayableDto> vehicles = entry.getValue();

            // 生成抬头
            PayableListDo header = new PayableListDo();
            // 编号规则：YF + 年月 + 6位流水号，如 YF202606000001
            header.setPayableListNo(NoGenerator.genNoOnly("YF", "yyyyMM", 6));
            header.setBillStatus(PayableListStatusEnum.DRAFT.getCode());
            header.setCustomerCode(customerCode);
            header.setCustomerName(vehicles.get(0).getCustomerName());
            header.setGenerateMonth(generateMonth);
            header.setTotalVehicles(0);
            header.setTotalAmount(BigDecimal.ZERO);
            header.setVersion(1);
            payableListGateway.addPayableList(header);

            // 生成明细
            for (SoOrderForPayableDto so : vehicles) {
                PayableListDetailDo detail = new PayableListDetailDo();
                detail.setPayableListId(header.getId());
                // 编号规则：YX + 年月日 + 6位流水号，如 YX20260601000001
                detail.setDetailNo(NoGenerator.genNoOnly("YX", "yyyyMMdd", 6));
                detail.setSoId(so.getSoId());
                detail.setSoNo(so.getSoNo());
                detail.setPoNo(so.getPoNo());
                detail.setVin(so.getVin());
                detail.setMaterialCode(so.getMaterialCode());
                detail.setMaterialDesc(so.getMaterialDesc());
                detail.setPrice(so.getVehiclePrice());
                detail.setQcPassTime(so.getQcResultTime());
                detail.setIsReceived(0);
                payableListDetailGateway.addPayableListDetail(detail);
            }

            // 汇总更新
            header.setTotalVehicles(vehicles.size());
            header.setTotalAmount(vehicles.stream()
                    .map(SoOrderForPayableDto::getVehiclePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            payableListGateway.updatePayableList(header);

            log.info("月度生成：客户={}, 车辆数={}, 清单号={}", customerCode, vehicles.size(), header.getPayableListNo());
        }

        log.info("月度生成完成，共{}个客户，{}辆车辆", customerGroups.size(), soList.size());
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
        return dto;
    }
}
