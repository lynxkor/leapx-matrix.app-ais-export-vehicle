package com.leapmotor.export.vehicle.payablelist.executor.cmd;

import com.leapmotor.export.vehicle.base.util.PayableListStatusChecker;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListDetailGateway;
import com.leapmotor.export.vehicle.payablelist.gateway.PayableListGateway;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDetailDo;
import com.leapmotor.export.vehicle.payablelist.model.PayableListDo;
import com.leapmotor.export.vehicle.payablelist.req.PayableListAddVehiclesReq;
import com.leapmotor.export.vehicle.so.enums.VehicleCbuSoQcResultEnum;
import com.leapmotor.export.vehicle.so.gateway.VehicleCbuSoGateway;
import com.leapmotor.export.vehicle.so.model.VehicleCbuSoDo;
import com.leapmotor.export.vehicle.so.query.VehicleCbuSoQo;
import com.leapmotor.paltform.ais.common.model.enums.ResultEnum;
import com.leapmotor.paltform.ais.common.model.exception.BizException;
import com.leapmotor.platform.ais.common.serial.support.NoGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 应付清单添加车辆执行器
 *
 * @author 赵吴涯
 * @since 2026-08-10
 */
@Slf4j
@Component
@AllArgsConstructor
public class PayableListAddVehiclesExe {

    private PayableListGateway payableListGateway;
    private PayableListDetailGateway payableListDetailGateway;
    private VehicleCbuSoGateway vehicleCbuSoGateway;

    @Transactional(rollbackFor = Exception.class)
    public String execute(PayableListAddVehiclesReq req) {
        // 查询应付清单
        PayableListDo payableList = payableListGateway.getPayableListById(req.getPayableListId());
        if (payableList == null) {
            throw new BizException(ResultEnum.BAD_REQUEST, "应付清单不存在");
        }

        // 校验状态（只有草稿或驳回状态可以添加）
        PayableListStatusChecker.checkCanEdit(payableList);

        // 查询质检通过的车辆（主表+明细联表查询，聚合 VIN/物料/销售市场等车辆明细字段）
        VehicleCbuSoQo qo = new VehicleCbuSoQo();
        qo.setQcResult(VehicleCbuSoQcResultEnum.PASSED.getCode());
        qo.setIds(req.getVehicleIds());

        // 已绑定到应付清单的 SO 通过 NOT IN 排除，避免内存过滤
        List<String> boundSoIds = payableListDetailGateway.listAllBoundSoIds();
        qo.setExcludeIds(boundSoIds);

        List<VehicleCbuSoDo> soOrders = vehicleCbuSoGateway.listSoList(qo);

        if (soOrders.isEmpty()) {
            throw new BizException(ResultEnum.BAD_REQUEST, "所选车辆均已绑定到其他应付清单");
        }

        // 创建应付清单明细
        List<PayableListDetailDo> detailDos = buildDetailList(req.getPayableListId(), soOrders, payableList);
        payableListDetailGateway.addBatchPayableListDetail(detailDos);

        // 重新计算统计信息
        updatePayableListStatistics(req.getPayableListId(), payableList);

        log.info("应付清单添加车辆成功，payableListId={}, addedCount={}, totalVehicles={}, totalAmount={}",
                req.getPayableListId(), soOrders.size(), payableList.getTotalVehicles(), payableList.getTotalAmount());
        return req.getPayableListId();
    }

    /**
     * 构建应付清单明细列表
     */
    private List<PayableListDetailDo> buildDetailList(String payableListId, List<VehicleCbuSoDo> soOrders, PayableListDo payableList) {
        List<PayableListDetailDo> detailDos = new ArrayList<>();
        for (VehicleCbuSoDo so : soOrders) {
            PayableListDetailDo detail = new PayableListDetailDo();
            // 应付清单相关信息
            detail.setPayableListId(payableListId);
            detail.setGenerateMonth(payableList.getGenerateMonth());
            detail.setBillStatus(payableList.getBillStatus());

            // 明细编号
            detail.setDetailNo(NoGenerator.genYearMonthDay("YX", 6));

            // SO订单信息
            detail.setSoId(so.getId());
            detail.setSoNo(so.getSoNo());
            detail.setPoNo(so.getPoNo());

            // 客户信息
            detail.setCustomerCode(payableList.getCustomerCode());
            detail.setCustomerName(payableList.getCustomerName());

            // 车辆信息
            detail.setVin(so.getVin());
            detail.setMaterialCode(so.getMaterialCode());
            detail.setMaterialDesc(so.getMaterialDescription());
            detail.setPrice(so.getVehiclePrice());
            detail.setQcPassTime(so.getQcResultTime());
            detail.setIsReceived(0);

            // 从SO获取的其他信息
            detail.setPoCreateTime(so.getReceiveOrderTime());
            detail.setSaleMarket(so.getSalesMarket());
            detail.setModelYear(so.getModelYear());
            detail.setOptionPackage(so.getOptionalPackage());

            // 提交日期和确认时间从应付清单主表获取
            detail.setSubmitDate(payableList.getSubmitTime());
            detail.setConfirmTime(payableList.getConfirmTime());

            detailDos.add(detail);
        }
        return detailDos;
    }

    /**
     * 更新应付清单统计信息
     */
    private void updatePayableListStatistics(String payableListId, PayableListDo payableList) {
        // 统计车辆数量
        int totalVehicles = payableListDetailGateway.countByPayableListId(payableListId);

        // 统计金额合计
        BigDecimal totalAmount = payableListDetailGateway.sumAmountByPayableListId(payableListId);
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        // 更新应付清单
        payableList.setTotalVehicles(totalVehicles);
        payableList.setTotalAmount(totalAmount);
        payableList.setVersion(payableList.getVersion() + 1);
        payableListGateway.updatePayableList(payableList);
    }
}
