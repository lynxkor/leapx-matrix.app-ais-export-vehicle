package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.enums.PayableListStatusEnum;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.SoOrderGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    private SoOrderGateway soOrderGateway;

    @Transactional(rollbackFor = Exception.class)
    public void generateMonthly() {
        // 计算 generateMonth = 上一个月
        String generateMonth = LocalDateTime.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 查询所有质检通过且未绑定的SO车辆
        List<SoOrderForPayableDto> soList = soOrderGateway.queryQcPassedAndUnbound();
        if (soList.isEmpty()) {
            log.info("月度生成：无待生成车辆，任务结束");
            return;
        }

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
}
