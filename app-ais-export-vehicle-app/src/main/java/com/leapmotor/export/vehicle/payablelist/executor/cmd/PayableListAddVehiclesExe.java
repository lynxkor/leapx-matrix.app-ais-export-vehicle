package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.payablelist.dto.SoOrderForPayableDto;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.SoOrderGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListAddVehiclesReq;
import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 批量添加车辆执行器
 *
 * @author 赵吴涯
 * @since 2026-07-21
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListAddVehiclesExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private SoOrderGateway soOrderGateway;

    @Transactional(rollbackFor = Exception.class)
    public String execute(PayableListAddVehiclesReq req) {
        PayableListDo db = payableListGateway.getPayableListById(req.getPayableListId());
        if (db == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }
        // 乐观锁校验
        if (db.getVersion() == null || !db.getVersion().equals(req.getVersion())) {
            throw new BizException(ResultEnum.BAD_REQUEST, "数据已被他人修改，请刷新后重试");
        }
        // 状态前置校验
        PayableListStatusChecker.checkEditable(db);

        // 查询SO车辆信息
        List<SoOrderForPayableDto> soList = soOrderGateway.queryQcPassedAndUnbound();
        List<SoOrderForPayableDto> selectedSo = soList.stream()
                .filter(so -> req.getSoIds().contains(so.getSoId()))
                .toList();

        if (selectedSo.isEmpty()) {
            throw new BizException(ResultEnum.BAD_REQUEST, "未找到可添加的SO车辆");
        }

        // 创建明细
        for (SoOrderForPayableDto so : selectedSo) {
            PayableListDetailDo detail = new PayableListDetailDo();
            detail.setPayableListId(db.getId());
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

        // 重算汇总 + 版本号 +1
        recalculateSummary(db, selectedSo);
        db.setVersion(db.getVersion() + 1);
        payableListGateway.updatePayableList(db);

        log.info("批量添加车辆成功，payableListId={}, count={}", db.getId(), selectedSo.size());
        return db.getId();
    }

    private void recalculateSummary(PayableListDo db, List<SoOrderForPayableDto> addedSo) {
        int newVehicles = (db.getTotalVehicles() == null ? 0 : db.getTotalVehicles()) + addedSo.size();
        BigDecimal newAmount = addedSo.stream()
                .map(SoOrderForPayableDto::getVehiclePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = (db.getTotalAmount() == null ? BigDecimal.ZERO : db.getTotalAmount()).add(newAmount);
        db.setTotalVehicles(newVehicles);
        db.setTotalAmount(totalAmount);
    }
}
